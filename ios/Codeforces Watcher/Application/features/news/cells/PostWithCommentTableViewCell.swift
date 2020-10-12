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

    private let postContainer = UIView()
    private let blogEntryTitleLabel = HeadingLabel()

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
    
    private let commentContainer = UIView()
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
    
    private let footerContainer = UIView()
    private let explanationLabel = SubheadingLabel().apply {
        $0.text = "see_all_comments".localized
    }
    private let arrowView = UIImageView(image: UIImage(named: "ic_arrow"))
    
    private var onClick: (_ link: String) -> () = { _ in }
    private var postLink: String!
    private var commentLink: String!

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
        setInteractions()
    }

    private func buildViewTree() {
        contentView.addSubview(cardView)
        
        [postContainer, horizontalLine1, commentContainer, horizontalLine2, footerContainer].forEach(cardView.addSubview)
        
        [blogEntryTitleLabel, postAuthorImage, postAuthorHandleLabel, postAgoLabel, postContentLabel].forEach(postContainer.addSubview)
        
        [commentatorImage, commentView].forEach(commentContainer.addSubview)
        
        [commentatorHandleLabel, commentAgoLabel, commentContentLabel].forEach(commentView.addSubview)
        
        [explanationLabel, arrowView].forEach(footerContainer.addSubview)
    }

    private func setConstraints() {
        cardView.edgesToSuperview(insets: UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8))
        
        postContainer.edgesToSuperview(excluding: .bottom)

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
            $0.bottomToSuperview()
        }
        
        horizontalLine1.run {
            $0.height(1)
            $0.topToBottom(of: postContainer, offset: 8)
            $0.horizontalToSuperview(insets: .horizontal(8))
        }
        
        commentContainer.run {
            $0.topToBottom(of: horizontalLine1)
            $0.horizontalToSuperview()
        }
        
        commentatorImage.run {
            $0.height(36)
            $0.width(36)
            $0.leadingToSuperview(offset: 8)
            $0.topToSuperview(offset: 8)
        }
        
        commentView.run {
            $0.top(to: commentatorImage)
            $0.leadingToTrailing(of: commentatorImage, offset: 8)
            $0.trailingToSuperview(offset: 8)
            $0.bottomToSuperview()
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
            $0.topToBottom(of: commentContainer, offset: 8)
            $0.horizontalToSuperview(insets: .horizontal(8))
        }
        
        footerContainer.run {
            $0.topToBottom(of: horizontalLine2)
            $0.horizontalToSuperview()
            $0.bottomToSuperview()
        }

        explanationLabel.run {
            $0.centerY(to: arrowView)
            $0.leadingToSuperview(offset: 12)
        }
        
        arrowView.run {
            $0.topToSuperview(offset: 12)
            $0.bottomToSuperview(offset: -12)
            $0.trailingToSuperview(offset: 12)
        }
    }
    
    private func setInteractions() {
        postContainer.onTap(target: self, action: #selector(openPost))
        commentContainer.onTap(target: self, action: #selector(openComment))
        footerContainer.onTap(target: self, action: #selector(openPost))
    }
    
    @objc private func openPost() {
        onClick(postLink)
    }
    
    @objc private func openComment() {
        onClick(commentLink)
    }

    func bind(_ news: NewsItem.PostWithCommentItem, _ onClick: @escaping (_ link: String) -> ()) {
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
        commentAgoLabel.text = news.commentAgoText
        commentContentLabel.text = news.commentContent
        
        self.onClick = onClick
        postLink = news.postLink
        commentLink = news.commentLink
    }
}
