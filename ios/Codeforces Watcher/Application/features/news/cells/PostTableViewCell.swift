//
//  PostTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/8/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import UIKit
import SDWebImage
import common

class PostTableViewCell: UITableViewCell {
    
    private let cardView = CardView()

    private let blogEntryTitleLabel = HeadingLabel().apply {
        $0.numberOfLines = 1
    }
    private let userImage = CircleImageView().apply {
        $0.image = noImage
    }
    private let userHandleLabel = SubheadingLabel()
    private let someTimeAgoLabel = SubheadingLabel()

    private let detailsLabel = BodyLabel().apply {
        $0.numberOfLines = 3
    }

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupView()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    private func setupView() {
        selectionStyle = .none

        buildViewTree()
        setConstraints()
    }

    private func buildViewTree() {
        contentView.addSubview(cardView)

        [blogEntryTitleLabel, userImage, userHandleLabel, someTimeAgoLabel, detailsLabel].forEach(cardView.addSubview)
    }

    private func setConstraints() {
        cardView.edgesToSuperview(insets: UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8))

        userImage.run {
            $0.leadingToSuperview(offset: 8)
            $0.topToSuperview(offset: 8)
            $0.height(36)
            $0.width(36)
        }

        blogEntryTitleLabel.run {
            $0.topToSuperview(offset: 8)
            $0.trailingToSuperview(offset: 8)
            $0.leadingToTrailing(of: userImage, offset: 8)
        }

        userHandleLabel.run {
            $0.leadingToTrailing(of: userImage, offset: 8)
            $0.trailingToLeading(of: someTimeAgoLabel)
            $0.topToBottom(of: blogEntryTitleLabel, offset: 4)
            $0.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        }

        someTimeAgoLabel.run {
            $0.topToBottom(of: blogEntryTitleLabel, offset: 4)
            $0.leadingToTrailing(of: userHandleLabel)
            $0.trailingToSuperview(offset: 8)
        }

        detailsLabel.run {
            $0.topToBottom(of: userImage, offset: 14)
            $0.horizontalToSuperview(insets: .horizontal(8))
            $0.bottomToSuperview(offset: -8)
        }
    }

    func bind(_ post: NewsItem.PostItem) {
        blogEntryTitleLabel.text = post.blogTitle
        userHandleLabel.attributedText = post.authorHandle
        someTimeAgoLabel.text = post.agoText
        detailsLabel.text = post.content

        userImage.run {
            $0.sd_setImage(with: URL(string: post.authorAvatar), placeholderImage: noImage)
            $0.layer.borderColor = post.rankColor
        }
    }
}
