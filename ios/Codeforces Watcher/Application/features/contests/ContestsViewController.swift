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

        navigationItem.rightBarButtonItem = UIBarButtonItem(
            image: UIImage(named: "filterIcon"),
            style: .plain,
            target: self,
            action: #selector(filterTapped)
        )

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
            self.addEventToCalendar(contest) { success, _ in
                if (success) {
                    analyticsControler.logEvent(
                        eventName: AnalyticsEvents().ADD_CONTEST_TO_CALENDAR,
                        params: ["contest_name": contest.title, "contest_platform": contest.platform.name]
                    )
                    self.showAlertWithOK(title: contest.title, message: "Has been added to your calendar".localized)
                } else {
                    self.showAlertWithOK(title: "Can't add contest to Calendar without permission".localized, message: "Enable it in Settings, please".localized)
                }
            }
        }

        [ContestTableViewCell.self, NoItemsTableViewCell.self].forEach(tableView.registerForReuse(cellType:))

        tableAdapter.onContestClick = { (contest) in
            let webViewController = WebViewController(
                contest.link,
                contest.title,
                AnalyticsEvents().CONTEST_OPENED,
                AnalyticsEvents().CONTEST_SHARED
            )

            self.presentModal(webViewController)
        }

        tableView.refreshControl = refreshControl

        refreshControl.run {
            $0.addTarget(self, action: #selector(refreshContests(_:)), for: .valueChanged)
            $0.tintColor = Palette.colorPrimaryDark
        }
    }

    @objc func filterTapped(sender: Any) {
        presentModal(ContestsFiltersViewController())
    }

    @objc func contestsRulesTapped(sender: Any) {
        let rulesLink = "https://codeforces.com/blog/entry/4088"
        
        let webViewController = WebViewController(rulesLink, "Official Codeforces rules".localized)
        presentModal(webViewController)
    }

    private func saveContestEvent(
        eventStore: EKEventStore,
        contest: Contest,
        completion: ((Bool, NSError?) -> Void)?
    ) {
        let startDate = Date(timeIntervalSince1970: Double(contest.startDateInMillis / 1000))
        let endDate = Date(timeIntervalSince1970: Double(contest.startDateInMillis / 1000 + contest.durationInMillis / 1000))
        
        let event = EKEvent(eventStore: eventStore).apply {
            $0.title = contest.title
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
                DispatchQueue.main.async {
                    if (granted) && (error == nil) {
                        self.saveContestEvent(eventStore: eventStore, contest: contest, completion: { success, NSError in
                            completion?(success, NSError)
                        })
                    } else {
                        completion?(false, error as NSError?)
                    }
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
        let okButton = UIAlertAction(title: "OK".localized, style: .cancel)
        alertController.addAction(okButton)

        present(alertController, animated: true, completion: nil)
    }

    @objc private func refreshContests(_ sender: Any) {
        analyticsControler.logEvent(eventName: AnalyticsEvents().CONTESTS_REFRESH, params: [:])
        fetchContests()
    }
    
    override func fabButtonTapped() {
        let contestsLink = "https://clist.by/"
        
        let webViewController = WebViewController(contestsLink, "upcoming_contests".localized)
        presentModal(webViewController)
    }
    
    func onNewState(state: Any) {
        let state = state as! ContestsState

        if (state.status == ContestsState.Status.idle) {
            refreshControl.endRefreshing()
        }

        tableAdapter.contests = state.contests
            .filter { $0.phase == .pending && state.filters.contains($0.platform) }
            .sorted(by: {
                $0.startDateInMillis < $1.startDateInMillis
            })

        tableView.reloadData()
    }

    private func fetchContests() {
        store.dispatch(action: ContestsRequests.FetchContests(isInitiatedByUser: true))
    }
}
