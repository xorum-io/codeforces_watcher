//
//  PinnedPostTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 07.09.2020.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import common
import UIKit
import FirebaseAnalytics

class PinnedPostTableViewCell: UITableViewCell {
    
    private let cardView = CardView()
    private let infoImage = CircleImageView().apply {
        $0.image = UIImage(named: "infoIcon")?.withRenderingMode(.alwaysTemplate)
        $0.tintColor = Palette.colorPrimary
    }
    
    private let headingLabel = HeadingLabel().apply {
        $0.textColor = Palette.blue
    }
    
    private let subheadingLabel = SubheadingLabel().apply {
        $0.text = "Click to see details".localized
    }
    
    private let crossImage = UIImageView(image: UIImage(named: "crossIcon")?.withRenderingMode(.alwaysTemplate)).apply {
        $0.tintColor = Palette.gray
    }
    
    private var pinnedPost: News.PinnedPost!
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupView()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    private func setupView() {
        buildViewTree()
        setConstraints()
        setInteractions()
    }

    private func buildViewTree() {
        addSubview(cardView)

        [infoImage, headingLabel, subheadingLabel, crossImage].forEach(cardView.addSubview)
    }
    
    private func setConstraints() {
        cardView.edgesToSuperview(insets: UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8))
        
        infoImage.run {
            $0.leadingToSuperview(offset: 8)
            $0.topToSuperview(offset: 8)
            $0.height(36)
            $0.width(36)
            $0.bottomToSuperview(offset: -8)
        }
        
        headingLabel.run {
            $0.topToSuperview(offset: 8)
            $0.trailingToLeading(of: crossImage, relation: .equalOrLess)
            $0.leadingToTrailing(of: infoImage, offset: 8)
        }
        
        subheadingLabel.run {
            $0.leadingToTrailing(of: infoImage, offset: 8)
            $0.trailingToSuperview(offset: 8)
            $0.topToBottom(of: headingLabel, offset: 4)
        }
        
        crossImage.run {
            $0.trailingToSuperview(offset: 8)
            $0.topToSuperview(offset: 8)
            $0.width(20)
            $0.height(20)
        }
    }
    
    private func setInteractions() {
        crossImage.run {
            $0.isUserInteractionEnabled = true
            $0.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(crossTapped)))
        }
    }
    
    @objc func crossTapped() {
        store.dispatch(action: NewsRequests.RemovePinnedPost(link: pinnedPost.link))
        Analytics.logEvent("actions_pinned_post_closed", parameters: [:])
    }
    
    func bind(_ pinnedPost: News.PinnedPost) {
        self.pinnedPost = pinnedPost
        headingLabel.text = pinnedPost.title
    }
}

