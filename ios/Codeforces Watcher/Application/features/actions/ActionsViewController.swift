//
//  ActionsViewController.swift
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

class ActionsViewController: UIViewControllerWithFab, ReKampStoreSubscriber {

    private let tableView = UITableView()
    private let tableAdapter = ActionsTableViewAdapter()
    private let refreshControl = UIRefreshControl()
    private let pinnedPostView = PinnedPostCardView()
    
    private var pinnedPost: PinnedPost!
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
                return KotlinBoolean(bool: oldState.actions == newState.actions)
            }.select { state in
                return state.actions
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
        setInteractions()
        setFabImage(named: "shareImage")
    }

    private func buildViewTree() {
        view.addSubview(tableView)
    }

    private func setConstraints() {
        tableView.edgesToSuperview()
    }
    
    private func setInteractions() {
        pinnedPostView.run {
            $0.isUserInteractionEnabled = true
            $0.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(pinnedPostTapped)))
        }
    }

    private func setupTableView() {
        tableView.run {
            $0.delegate = tableAdapter
            $0.dataSource = tableAdapter
            $0.separatorStyle = .none
        }

        [CommentTableViewCell.self, BlogEntryTableViewCell.self, NoItemsTableViewCell.self].forEach(tableView.registerForReuse(cellType:))

        tableAdapter.onActionClick = { (link, shareText) in
            let webViewController = WebViewController().apply {
                $0.link = link
                $0.shareText = shareText
                $0.openEventName = "action_opened"
                $0.shareEventName = "action_share_comment"
            }
            self.presentModal(webViewController)
        }

        tableView.refreshControl = refreshControl

        refreshControl.run {
            $0.addTarget(self, action: #selector(refreshActions(_:)), for: .valueChanged)
            $0.tintColor = Palette.colorPrimaryDark
        }
    }

    func onNewState(state: Any) {
        let state = state as! ActionsState
        
        if (state.status == .idle) {
            refreshControl.endRefreshing()
            
            if (feedbackController.shouldShowFeedbackCell()) {
                showFeedbackCardView()
                feedbackCardView.callback = {
                    self.onNewState(state: state)
                }
            } else if let pinnedPost = state.pinnedPost {
                let shouldShowPinnedPost = SettingsKt.settings.readPinnedPostLink() != pinnedPost.link && tableView.tableHeaderView != pinnedPostView && !state.actions.isEmpty
                
                if (shouldShowPinnedPost) {
                    self.pinnedPost = pinnedPost
                    
                    showPinnedPost()
                }
            } else {
                tableView.tableHeaderView = nil
            }
        }
        
        tableAdapter.actions = state.actions

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
    
    private func showPinnedPost() {
        pinnedPostView.bind(pinnedPost)
        
        tableView.run {
            $0.tableHeaderView = pinnedPostView
            $0.tableHeaderView?.widthToSuperview()
        }

        pinnedPostView.run {
            $0.setNeedsLayout()
            $0.layoutIfNeeded()
        }
    }
    
    override func fabButtonTapped() {
        let activityController = UIActivityViewController(activityItems: ["share_cw_message".localized], applicationActivities: nil).apply {
            $0.popoverPresentationController?.barButtonItem = navigationItem.rightBarButtonItem
        }
        
        present(activityController, animated: true)
    }

    @objc func pinnedPostTapped() {
        let webViewController = WebViewController().apply {
            $0.link = pinnedPost.link
            $0.shareText = buildShareText(pinnedPost.title, pinnedPost.link)
            $0.openEventName = "actions_pinned_post_opened"
        }

        presentModal(webViewController)
    }

    @objc private func refreshActions(_ sender: Any) {
        Analytics.logEvent("actions_list_refresh", parameters: [:])
        
        store.dispatch(action: NewsRequests.FetchNews(isInitializedByUser: true, language: "locale".localized))
        store.dispatch(action: NewsRequests.FetchPinnedPost())
    }
}
