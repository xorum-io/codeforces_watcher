//
//  NewsTableViewAdapter.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/2/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import UIKit
import common

class NewsTableViewAdapter: NSObject, UITableViewDelegate, UITableViewDataSource {
    var news: [NewsItem] = []
    var callback: () -> () = { }

    var onNewsClick: (
        _ link: String,
        _ shareText: String,
        _ onOpenEvent: String,
        _ onShareEvent: String
    ) -> () = { _, _, _, _ in }

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (news.isEmpty) {
            return 1
        }

        return news.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (news.isEmpty) {
            return tableView.dequeueReusableCell(cellType: NoItemsTableViewCell.self).apply {
                $0.bind(imageName: "noItemsImage", explanation: "news_explanation")
            }
        }
        
        switch(news[indexPath.row]) {
        case .pinnedItem(let item):
            return tableView.dequeueReusableCell(cellType: PinnedPostTableViewCell.self).apply {
                $0.bind(item)
            }
        case .postWithCommentItem(let item):
            return tableView.dequeueReusableCell(cellType: PostWithCommentTableViewCell.self).apply {
                $0.bind(item) { link in
                    let shareText = buildShareText(item.blogTitle, link)
                    let onOpenEvent = "action_opened"
                    let onShareEvent = "action_share_comment"
                    
                    self.onNewsClick(link, shareText, onOpenEvent, onShareEvent)
                }
            }
        case .postItem(let item):
            return tableView.dequeueReusableCell(cellType: PostTableViewCell.self).apply {
                $0.bind(item)
            }
        case .feedbackItem(let item):
            return tableView.dequeueReusableCell(cellType: FeedbackTableViewCell.self).apply {
                $0.bind(item) {
                    self.callback()
                }
            }
        case .videoItem(let item):
            return tableView.dequeueReusableCell(cellType: VideoTableViewCell.self).apply {
                $0.bind(item)
            }
        }
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if (news.isEmpty) {
            return
        }
        
        switch(news[indexPath.row]) {
        case .pinnedItem(let news):
            let shareText = buildShareText(news.title, news.link)
            let onOpenEvent = "actions_pinned_post_opened"
            let onShareEvent = "action_share_comment"
            
            onNewsClick(news.link, shareText, onOpenEvent, onShareEvent)
        case .postItem(let news):
            let shareText = buildShareText(news.blogTitle, news.link)
            let onOpenEvent = "action_opened"
            let onShareEvent = "action_share_comment"
            
            onNewsClick(news.link, shareText, onOpenEvent, onShareEvent)
        case .postWithCommentItem(let news):
            let shareText = buildShareText(news.blogTitle, news.commentLink)
            let onOpen = "action_opened"
            let onShare = "action_share_comment"
            
            onNewsClick(news.commentLink, shareText, onOpen, onShare)
        default:
            return
        }
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if (news.isEmpty) {
            return tableView.frame.height
        } else {
            return UITableView.automaticDimension
        }
    }

    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return 122
    }
}
