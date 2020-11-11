//
//  ContestsViewController.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/15/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit
import EventKit
import FirebaseAnalytics
import common

class ContestsViewController: UIViewControllerWithFab, ReKampStoreSubscriber {
    
    private let contestsRulesCardView = ContestsRulesCardView()
    private let tableView = UITableView()
    private let tableAdapter = ContestsTableViewAdapter()
    private let refreshControl = UIRefreshControl()

    override func viewDidLoad() {
        super.viewDidLoad()

        setupView()
        setupTableView()
        setFabImage(named: "eyeIcon")
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        store.subscribe(subscriber: self) { subscription in
            subscription.skipRepeats { oldState, newState in
                return KotlinBoolean(bool: oldState.contests == newState.contests)
            }.select { state in
                return state.contests
            }
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        store.unsubscribe(subscriber: self)
    }

    private func setupView() {
        view.backgroundColor = .white

        buildViewTree()
        setConstraints()

        navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(named: "filterIcon"), style: .plain, target: self, action: #selector(filterTapped))

        contestsRulesCardView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.contestsRulesTapped)))
    }

    private func buildViewTree() {
        view.addSubview(tableView)

        tableView.tableHeaderView = contestsRulesCardView
    }

    private func setConstraints() {
        tableView.run {
            $0.edgesToSuperview()
            $0.tableHeaderView?.widthToSuperview()
        }

        contestsRulesCardView.run {
            $0.setNeedsLayout()
            $0.layoutIfNeeded()
        }
    }

    private func setupTableView() {
        tableView.run {
            $0.delegate = tableAdapter
            $0.dataSource = tableAdapter
            $0.separatorStyle = .none
        }

        tableAdapter.onCalendarTap = { contest in
            self.addEventToCalendar(contest) { success, NSError in
                if (success) {
                    DispatchQueue.main.async {
                        analyticsControler.logEvent(eventName: "add_contest_to_google_calendar", params: ["contest_name": contest.name, "contest_platform": contest.platform.name])
                        self.showAlertWithOK(title: contest.name, message: "Has been added to your calendar".localized)
                    }
                } else {
                    DispatchQueue.main.async {
                        self.showAlertWithOK(title: "Can't add contest to Calendar without permission".localized, message: "Enable it in Settings, please".localized)
                    }
                }
            }
        }

        [ContestTableViewCell.self, NoItemsTableViewCell.self].forEach(tableView.registerForReuse(cellType:))

        tableAdapter.onContestClick = { (contest) in
            let webViewController = WebViewController().apply {
                $0.link = contest.link
                $0.shareText = buildShareText(contest.name, contest.link)
                $0.onOpenEvent = "contest_opened"
                $0.onShareEvent = "contest_shared"
            }

            self.presentModal(webViewController)
        }

        tableView.refreshControl = refreshControl

        refreshControl.run {
            $0.addTarget(self, action: #selector(refreshContests(_:)), for: .valueChanged)
            $0.tintColor = Palette.colorPrimaryDark
        }
    }

    @objc func filterTapped(sender: Any) {
        presentModal(FiltersViewController())
    }

    @objc func contestsRulesTapped(sender: Any) {
        let rulesLink = "https://codeforces.com/blog/entry/4088"
        
        let webViewController = WebViewController().apply {
            $0.link = rulesLink
            $0.shareText = buildShareText("Official Codeforces rules".localized, rulesLink)
        }
        presentModal(webViewController)
    }

    private func saveContestEvent(eventStore: EKEventStore, contest: Contest, completion: ((Bool, NSError?) -> Void)?) {
        let event = EKEvent(eventStore: eventStore)
        event.title = contest.name

        let startDate = Date(timeIntervalSince1970: Double(contest.startTimeSeconds / 1000))
        let endDate = Date(timeIntervalSince1970: Double(contest.startTimeSeconds / 1000 + contest.durationSeconds))

        event.run {
            $0.title = contest.name
            $0.startDate = startDate
            $0.endDate = endDate
            $0.calendar = eventStore.defaultCalendarForNewEvents
        }

        do {
            try eventStore.save(event, span: .thisEvent)
        } catch let e as NSError {
            completion?(false, e)
            return
        }
        completion?(true, nil)
    }

    private func addEventToCalendar(_ contest: Contest, completion: ((Bool, NSError?) -> Void)?) {
        let eventStore = EKEventStore()

        if (EKEventStore.authorizationStatus(for: .event) != EKAuthorizationStatus.authorized) {
            eventStore.requestAccess(to: .event, completion: { (granted, error) in
                if (granted) && (error == nil) {
                    self.saveContestEvent(eventStore: eventStore, contest: contest, completion: { success, NSError in
                        completion?(success, NSError)
                    })
                } else {
                    completion?(false, error as NSError?)
                }
            })
        } else {
            saveContestEvent(eventStore: eventStore, contest: contest, completion: { success, NSError in
                completion?(success, NSError)
            })
        }
    }

    private func showAlertWithOK(title: String, message: String) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okButton = UIAlertAction(title: "OK", style: .cancel)
        alertController.addAction(okButton)

        present(alertController, animated: true, completion: nil)
    }

    @objc private func refreshContests(_ sender: Any) {
        analyticsControler.logEvent(eventName: "contests_list_refresh", params: [:])
        fetchContests()
    }
    
    override func fabButtonTapped() {
        let contestsLink = "https://clist.by/"
        
        let webViewController = WebViewController().apply {
            $0.link = contestsLink
            $0.shareText = buildShareText("Check upcoming programming contests".localized, contestsLink)
        }
        presentModal(webViewController)
    }

    func onNewState(state: Any) {
        let state = state as! ContestsState

        if (state.status == ContestsState.Status.idle) {
            refreshControl.endRefreshing()
        }

        tableAdapter.contests = state.contests
            .filter { $0.phase == "BEFORE" && state.filters.contains($0.platform) }
            .sorted(by: {
                $0.startTimeSeconds < $1.startTimeSeconds
            })

        tableView.reloadData()
    }

    private func fetchContests() {
        store.dispatch(action: ContestsRequests.FetchContests(isInitiatedByUser: true, language: "locale".localized))
    }
}
