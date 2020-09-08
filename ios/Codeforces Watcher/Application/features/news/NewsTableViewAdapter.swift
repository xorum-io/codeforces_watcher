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
    var news: [News] = []

    var onNewsClick: (
        _ link: String,
        _ shareText: String,
        _ onOpen: @escaping () -> (),
        _ onShare: @escaping () -> ()
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
        case let news as News.PinnedPost:
            return tableView.dequeueReusableCell(cellType: PinnedPostTableViewCell.self).apply {
                $0.bind(news)
            }
        case let news as News.Comment:
            return tableView.dequeueReusableCell(cellType: CommentTableViewCell.self).apply {
                $0.bind(news)
            }
        case let news as News.Post:
            return tableView.dequeueReusableCell(cellType: PostTableViewCell.self).apply {
                $0.bind(news)
            }
        default:
            return tableView.dequeueReusableCell(cellType: CommentTableViewCell.self)
        }
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if (news.isEmpty) {
            return
        }
        
        switch(news[indexPath.row]) {
        case let news as News.PinnedPost:
            let shareText = buildShareText(news.title.beautify(), news.link)
            let onOpen = { analyticsControler.logPinnedPostOpened() }
            let onShare = { analyticsControler.logShareComment() }
            
            onNewsClick(news.link, shareText, onOpen, onShare)
        case let news as News.Comment:
            let shareText = buildShareText(news.title.beautify(), news.link)
            let onOpen = { analyticsControler.logActionOpened() }
            let onShare = { analyticsControler.logShareComment() }
            
            onNewsClick(news.link, shareText, onOpen, onShare)
        case let news as News.Post:
            let shareText = buildShareText(news.title.beautify(), news.link)
            let onOpen = { analyticsControler.logActionOpened() }
            let onShare = { analyticsControler.logShareComment() }
            
            onNewsClick(news.link, shareText, onOpen, onShare)
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
