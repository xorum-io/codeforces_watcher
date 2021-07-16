//
//  ProblemsViewController.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/17/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import UIKit
import common
import FirebaseAnalytics

class ProblemsViewController: UIViewControllerWithFab, ReKampStoreSubscriber, UISearchResultsUpdating {

    private let tableView = UITableView()
    private let tableAdapter = ProblemsTableViewAdapter()
    private let refreshControl = UIRefreshControl()
    private let searchController = UISearchController(searchResultsController: nil)
    private var problems: [Problem] = []

    override func viewDidLoad() {
        super.viewDidLoad()

        setupView()
        setupTableView()
        setupSearchView()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        store.subscribe(subscriber: self) { subscription in
            subscription.skipRepeats { oldState, newState in
                return KotlinBoolean(bool: oldState.problems == newState.problems)
            }.select { state in
                return state.problems
            }
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        store.unsubscribe(subscriber: self)
    }

    private func setupView() {
        view.backgroundColor = Palette.white

        buildViewTree()
        setConstraints()
        updateFabButton(store.state.problems.isFavourite)
        navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(named: "filterIcon"), style: .plain, target: self, action: #selector(filterTapped))
    }
    
    @objc private func filterTapped() {
        presentModal(ProblemsFiltersViewController(["tag 1", "tag 2", "tag 3", "tag 4"]))
    }

    private func buildViewTree() {
        view.addSubview(tableView)
    }

    private func setConstraints() {
        edgesForExtendedLayout = []

        tableView.edgesToSuperview()
    }

    private func setupTableView() {
        tableView.run {
            $0.delegate = tableAdapter
            $0.dataSource = tableAdapter
            $0.separatorStyle = .none
        }

        [ProblemTableViewCell.self, NoItemsTableViewCell.self].forEach(tableView.registerForReuse(cellType:))

        tableAdapter.onProblemClick = { (link, title) in
            let webViewController = WebViewController(
                link,
                title,
                AnalyticsEvents().PROBLEM_OPENED,
                AnalyticsEvents().PROBLEM_SHARED
            )
            self.searchController.dismiss(animated: false)
            self.presentModal(webViewController)
        }

        tableView.refreshControl = refreshControl

        refreshControl.run {
            $0.addTarget(self, action: #selector(refreshProblems(_:)), for: .valueChanged)
            $0.tintColor = Palette.colorPrimaryDark
        }
    }

    override func fabButtonTapped() {
        store.dispatch(action: ProblemsActions.ChangeTypeProblems(isFavourite: !store.state.problems.isFavourite))
    }

    private func updateFabButton(_ isFavourite: Bool) {
        setFabImage(named: isFavourite ? "infinityIcon" : "starIcon")
    }

    private func setupSearchView() {
        searchController.run {
            $0.searchResultsUpdater = self
            $0.obscuresBackgroundDuringPresentation = false
            $0.hidesNavigationBarDuringPresentation = false

            $0.searchBar.run {
                $0.placeholder = "Search for problems...".localized
                $0.returnKeyType = .search
                $0.tintColor = .darkGray
                $0.barStyle = .default
                $0.searchBarStyle = .minimal
                $0.backgroundColor = .white
                $0.delegate = self
            }
        }

        tableView.tableHeaderView = searchController.searchBar
    }

    func updateSearchResults(for searchController: UISearchController) {
        guard let text = searchController.searchBar.text?.lowercased() else { return }

        let filteredProblems = problems.filter {
            var shouldAdd = false

            [$0.fullName, $0.enName, $0.ruName, $0.contestName].forEach {
                if $0.lowercased().contains(text) {
                    shouldAdd = true
                }
            }
            return shouldAdd
        }

        tableAdapter.problems = text.isEmpty ? problems : filteredProblems

        tableView.reloadData()
    }

    func onNewState(state: Any) {
        let state = state as! ProblemsState

        problems = state.isFavourite ? state.problems.filter { $0.isFavourite } : state.problems

        if (state.status == ProblemsState.Status.idle) {
            refreshControl.endRefreshing()
        }

        updateFabButton(state.isFavourite)

        tableAdapter.run {
            $0.problems = problems
            $0.noProblemsExplanation = state.isFavourite ? "no_favourite_problems_explanation" : "problems_explanation"
        }

        updateSearchResults(for: searchController)
    }

    @objc private func refreshProblems(_ sender: Any) {
        fetchProblems()
        analyticsControler.logEvent(eventName: AnalyticsEvents().PROBLEMS_REFRESH, params: [:])
    }

    private func fetchProblems() {
        store.dispatch(action: ProblemsRequests.FetchProblems(isInitiatedByUser: true))
    }
}

// Fix for https://github.com/xorum-io/codeforces_watcher/issues/139
extension ProblemsViewController: UISearchBarDelegate {

    func searchBarShouldBeginEditing(_ searchBar: UISearchBar) -> Bool {
        return !refreshControl.isRefreshing
    }

    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        tableView.refreshControl = nil
    }

    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        tableView.refreshControl = refreshControl
        searchController.searchBar.setShowsCancelButton(false, animated: true)
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        tableView.refreshControl = refreshControl
        searchController.dismiss(animated: false)
    }
}
