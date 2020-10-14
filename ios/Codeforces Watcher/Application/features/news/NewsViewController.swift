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

        [PostWithCommentTableViewCell.self, PostTableViewCell.self, NoItemsTableViewCell.self,
         PinnedPostTableViewCell.self, FeedbackTableViewCell.self].forEach(tableView.registerForReuse(cellType:))

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
        
        var items: [NewsItem] = []
        
        if (state.status == .idle) {
            refreshControl.endRefreshing()
            
            if (feedbackController.shouldShowFeedbackCell()) {
                items.append(NewsItem.feedbackItem(NewsItem.FeedbackItem(feedbackController.feedUIModel)))
                tableAdapter.callback = {
                    self.onNewState(state: state)
                }
            }
        }
        
        let news = state.news.filter { news in
            if let news = news as? News.PinnedPost {
                return SettingsKt.settings.readLastPinnedPostLink() != news.link
            } else {
                return true
            }
        }
        
        items += news.mapToItems()
        tableAdapter.news = items

        tableView.reloadData()
    }
    
    override func fabButtonTapped() {
        let activityController = UIActivityViewController(activityItems: ["share_cw_message".localized], applicationActivities: nil).apply {
            $0.popoverPresentationController?.run {
                $0.sourceView = self.view
                $0.sourceRect = CGRect(x: self.view.bounds.midX, y: self.view.bounds.height, width: 0, height: 0)
            }
        }
        
        present(activityController, animated: true)
        
        analyticsControler.logShareApp()
    }

    @objc private func refreshNews(_ sender: Any) {
        analyticsControler.logRefreshingData(refreshScreen: .news)
        store.dispatch(action: NewsRequests.FetchNews(isInitializedByUser: true, language: "locale".localized))
    }
}

fileprivate extension Array where Element == News {
    func mapToItems() -> [NewsItem] {
        compactMap { news in
            switch(news) {
            case let postWithComment as News.PostWithComment:
                return NewsItem.postWithCommentItem(NewsItem.PostWithCommentItem(postWithComment.comment, postWithComment.post))
            case let post as News.Post:
                return NewsItem.postItem(NewsItem.PostItem(post))
            case let pinnedPost as News.PinnedPost:
                return NewsItem.pinnedItem(NewsItem.PinnedItem(pinnedPost))
            default:
                return nil
            }
        }
    }
}
