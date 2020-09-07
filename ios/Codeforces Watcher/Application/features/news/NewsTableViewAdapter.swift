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
                $0.bind(imageName: "noItemsImage", explanation: "actions_explanation")
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
            return tableView.dequeueReusableCell(cellType: BlogEntryTableViewCell.self).apply {
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
        
        var link = "", title = "", onOpen: () -> (), onShare: () -> () = { }
        
        switch(news[indexPath.row]) {
        case let news as News.PinnedPost:
            (link, title, onOpen) = (news.link, news.title, { analyticsControler.logPinnedPostOpened() })
        case let news as News.Comment:
            (link, title, onOpen, onShare) = (
                news.link,
                news.title,
                { analyticsControler.logActionOpened() },
                { analyticsControler.logShareComment() }
            )
        case let news as News.Post:
            (link, title, onOpen, onShare) = (
                news.link,
                news.title,
                { analyticsControler.logActionOpened() },
                { analyticsControler.logShareComment() }
            )
        default:
            return
        }

        let shareText = buildShareText(title.beautify(), link)

        onNewsClick(link, shareText, onOpen, onShare)
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
