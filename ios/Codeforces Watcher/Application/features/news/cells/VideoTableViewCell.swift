//
//  VideoTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Den Matiash on 09.11.2020.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import common
import UIKit

class VideoTableViewCell: UITableViewCell {
    
    private let cardView = CardView()

    private let blogEntryTitleLabel = HeadingLabel()
    private let userImage = CircleImageView().apply {
        $0.image = noImage
    }
    private let userHandleLabel = SubheadingLabel()
    private let someTimeAgoLabel = SubheadingLabel()

    private let thumbnailImage = UIImageView(image: UIImage(named: "video_placeholder")).apply {
        $0.contentMode = .scaleAspectFill
        $0.clipsToBounds = true
    }
    
    private let horizontalLine = UIView().apply {
        $0.backgroundColor = Palette.dividerGray
    }
    private let explanationLabel = SubheadingLabel().apply {
        $0.text = "watch_video".localized
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
        backgroundColor = Palette.white

        buildViewTree()
        setInteractions()
    }
    
    private func buildViewTree() {
        contentView.addSubview(cardView)

        [blogEntryTitleLabel, userImage, userHandleLabel, someTimeAgoLabel, thumbnailImage, horizontalLine, explanationLabel, arrowView].forEach(cardView.addSubview)
    }
    
    private func setInteractions() {
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

        thumbnailImage.run {
            $0.heightToWidth(of: thumbnailImage, multiplier: 9 / 16.0)
            $0.topToBottom(of: userImage, offset: 14)
            $0.horizontalToSuperview(insets: .horizontal(8))
        }
        
        horizontalLine.run {
            $0.height(1)
            $0.topToBottom(of: thumbnailImage, offset: 8)
            $0.horizontalToSuperview(insets: .horizontal(8))
        }
        
        explanationLabel.run {
            $0.centerY(to: arrowView)
            $0.leadingToSuperview(offset: 12)
        }
        
        arrowView.run {
            $0.topToBottom(of: horizontalLine, offset: 12)
            $0.bottomToSuperview(offset: -12)
            $0.trailingToSuperview(offset: 12)
        }
    }
    
    func bind(_ video: NewsItem.VideoItem) {
        blogEntryTitleLabel.text = video.title
        userHandleLabel.attributedText = video.authorHandle
        someTimeAgoLabel.text = video.agoText
        thumbnailImage.sd_setImage(with: URL(string: video.thumbnailLink), placeholderImage: UIImage(named: "video_placeholder"))

        userImage.run {
            $0.sd_setImage(with: URL(string: video.authorAvatar), placeholderImage: noImage)
            $0.layer.borderColor = video.rankColor
        }
    }
}
