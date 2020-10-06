//
//  PostWithCommentTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/8/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import UIKit
import common

class PostWithCommentTableViewCell: UITableViewCell {

    private let cardView = CardView()

    private let blogEntryTitleLabel = HeadingLabel().apply {
        $0.numberOfLines = 1
    }

    private let postAuthorImage = CircleImageView().apply {
        $0.image = noImage
    }
    private let postAuthorHandleLabel = SubheadingLabel()
    private let postAgoLabel = SubheadingLabel()
    private let postContentLabel = BodyLabel().apply {
        $0.numberOfLines = 3
        $0.textColor = UIColor.black
    }
    
    private let horizontalLine1 = UIView().apply {
        $0.backgroundColor = Palette.dividerGray
    }
    
    private let commentatorImage = CircleImageView().apply {
        $0.image = noImage
    }
    private let commentView = UIView().apply {
        $0.backgroundColor = Palette.gray6
        $0.layer.cornerRadius = 4
    }
    private let commentatorHandleLabel = SubheadingLabel()
    private let commentAgoLabel = SubheadingLabel()
    private let commentContentLabel = BodyLabel().apply {
        $0.numberOfLines = 3
    }
    
    private let horizontalLine2 = UIView().apply {
        $0.backgroundColor = Palette.dividerGray
    }
    private let explanationLabel = SubheadingLabel().apply {
        $0.text = "see_all_comments".localized
    }
    private let arrowView = UIImageView(image: UIImage(named: "ic_arrow"))

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

        [blogEntryTitleLabel, postAuthorImage, postAuthorHandleLabel, postAgoLabel, postContentLabel, horizontalLine1, commentatorImage, commentView, horizontalLine2, explanationLabel, arrowView].forEach(cardView.addSubview)
        
        [commentatorHandleLabel, commentAgoLabel, commentContentLabel].forEach(commentView.addSubview)
    }

    private func setConstraints() {
        cardView.edgesToSuperview(insets: UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8))

        postAuthorImage.run {
            $0.leadingToSuperview(offset: 8)
            $0.topToSuperview(offset: 8)
            $0.height(36)
            $0.width(36)
        }

        blogEntryTitleLabel.run {
            $0.topToSuperview(offset: 8)
            $0.trailingToSuperview(offset: 8)
            $0.leadingToTrailing(of: postAuthorImage, offset: 8)
        }

        postAuthorHandleLabel.run {
            $0.leading(to: blogEntryTitleLabel)
            $0.trailingToLeading(of: postAgoLabel)
            $0.topToBottom(of: blogEntryTitleLabel, offset: 4)
            $0.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        }

        postAgoLabel.run {
            $0.topToBottom(of: blogEntryTitleLabel, offset: 4)
            $0.leadingToTrailing(of: postAuthorHandleLabel)
            $0.trailingToSuperview(offset: 8)
        }

        postContentLabel.run {
            $0.topToBottom(of: postAuthorImage, offset: 14)
            $0.horizontalToSuperview(insets: .horizontal(8))
        }
        
        horizontalLine1.run {
            $0.height(1)
            $0.topToBottom(of: postContentLabel, offset: 8)
            $0.horizontalToSuperview(insets: .horizontal(8))
        }
        
        commentatorImage.run {
            $0.height(36)
            $0.width(36)
            $0.leadingToSuperview(offset: 8)
            $0.topToBottom(of: horizontalLine1, offset: 8)
        }
        
        commentView.run {
            $0.top(to: commentatorImage)
            $0.leadingToTrailing(of: commentatorImage, offset: 8)
            $0.trailingToSuperview(offset: 8)
        }
        
        commentatorHandleLabel.run {
            $0.topToSuperview(offset: 6)
            $0.leadingToSuperview(offset: 8)
            $0.trailingToLeading(of: commentAgoLabel)
            $0.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        }
        
        commentAgoLabel.run {
            $0.top(to: commentatorHandleLabel)
            $0.leadingToTrailing(of: commentatorHandleLabel)
            $0.trailingToSuperview(offset: 8)
        }
        
        commentContentLabel.run {
            $0.topToBottom(of: commentatorHandleLabel, offset: 4)
            $0.horizontalToSuperview(insets: .horizontal(8))
            $0.bottomToSuperview(offset: -8)
        }
        
        horizontalLine2.run {
            $0.height(1)
            $0.topToBottom(of: commentView, offset: 8)
            $0.horizontalToSuperview(insets: .horizontal(8))
        }

        explanationLabel.run {
            $0.centerY(to: arrowView)
            $0.leadingToSuperview(offset: 12)
        }
        
        arrowView.run {
            $0.topToBottom(of: horizontalLine2, offset: 12)
            $0.bottomToSuperview(offset: -12)
            $0.trailingToSuperview(offset: 12)
        }
    }

    func bind(_ news: NewsItem.PostWithCommentItem) {
        blogEntryTitleLabel.text = news.blogTitle
        
        postAuthorImage.run {
            $0.sd_setImage(with: URL(string: news.postAuthorAvatar), placeholderImage: noImage)
            $0.layer.borderColor = news.postAuthorRankColor
        }
        postAuthorHandleLabel.attributedText = news.postAuthorHandle
        postAgoLabel.text = news.postAgoText
        postContentLabel.text = news.postContent
        
        commentatorImage.run {
            $0.sd_setImage(with: URL(string: news.commentatorAvatar), placeholderImage: noImage)
            $0.layer.borderColor = news.commentatorRankColor
        }
        commentatorHandleLabel.attributedText = news.commentatorHandle
        commentAgoLabel.text = news.commentatorAgoText
        commentContentLabel.text = news.commentatorContent
    }
}
