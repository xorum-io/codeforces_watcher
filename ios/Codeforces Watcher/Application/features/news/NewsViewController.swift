//
//  NewsViewController.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 12/31/19.
//  Copyright © 2019 xorum.io. All rights reserved.
//

import UIKit
import TinyConstraints
import WebKit
import common
import FirebaseAnalytics

class NewsViewController: UIViewControllerWithFab, ReKampStoreSubscriber {

    private let tableView = UITableView()
    private let tableAdapter = NewsTableViewAdapter()
    private let refreshControl = UIRefreshControl()
    private let feedbackCardView = FeedbackCardView()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        setupView()
        setupTableView()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        store.subscribe(subscriber: self) { subscription in
            subscription.skipRepeats { oldState, newState in
                return KotlinBoolean(bool: oldState.news == newState.news)
            }.select { state in
                return state.news
            }
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        store.unsubscribe(subscriber: self)
    }

    private func setupView() {
        navigationController?.interactivePopGestureRecognizer?.isEnabled = false
        view.backgroundColor = .white

        buildViewTree()
        setConstraints()
        setFabImage(named: "shareImage")
    }

    private func buildViewTree() {
        view.addSubview(tableView)
    }

    private func setConstraints() {
        tableView.edgesToSuperview()
    }

    private func setupTableView() {
        tableView.run {
            $0.delegate = tableAdapter
            $0.dataSource = tableAdapter
            $0.separatorStyle = .none
        }

        [CommentTableViewCell.self, PostTableViewCell.self, NoItemsTableViewCell.self, PinnedPostTableViewCell.self].forEach(tableView.registerForReuse(cellType:))

        tableAdapter.onNewsClick = { link, shareText, onOpen, onShare in
            let webViewController = WebViewController().apply {
                $0.link = link
                $0.shareText = shareText
                $0.onOpen = onOpen
                $0.onShare = onShare
            }
            self.presentModal(webViewController)
        }

        tableView.refreshControl = refreshControl

        refreshControl.run {
            $0.addTarget(self, action: #selector(refreshNews(_:)), for: .valueChanged)
            $0.tintColor = Palette.colorPrimaryDark
        }
    }

    func onNewState(state: Any) {
        let state = state as! NewsState
        
        if (state.status == .idle) {
            refreshControl.endRefreshing()
            
            if (feedbackController.shouldShowFeedbackCell()) {
                showFeedbackCardView()
                feedbackCardView.callback = {
                    self.onNewState(state: state)
                }
            } else {
                tableView.tableHeaderView = nil
            }
        }
        
        let news = state.news.filter { news in
            if let news = news as? News.PinnedPost {
                return SettingsKt.settings.readLastPinnedPostLink() != news.link
            } else {
                return true
            }
        }
        
        tableAdapter.news = news

        tableView.reloadData()
    }
    
    private func showFeedbackCardView() {
        feedbackCardView.bind()
        
        tableView.run {
            $0.tableHeaderView = feedbackCardView
            $0.tableHeaderView?.widthToSuperview()
        }
        
        feedbackCardView.run {
            $0.setNeedsLayout()
            $0.layoutIfNeeded()
        }
    }
    
    override func fabButtonTapped() {
        let activityController = UIActivityViewController(activityItems: ["share_cw_message".localized], applicationActivities: nil).apply {
            $0.popoverPresentationController?.barButtonItem = navigationItem.rightBarButtonItem
        }
        
        present(activityController, animated: true)
        
        analyticsControler.logShareApp()
    }

    @objc private func refreshNews(_ sender: Any) {
        analyticsControler.logRefreshingData(refreshScreen: .news)
        store.dispatch(action: NewsRequests.FetchNews(isInitializedByUser: true, language: "locale".localized))
    }
}
