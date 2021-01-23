//
//  LoginTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/22/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit
import common

class LoginTableViewCell: UITableViewCell {

    private let cardView = CardView()

    private let userImage = CircleImageView().apply {
        $0.image = noImage
    }
    private let handleLabel = HeadingLabel()
    private let ratingUpdateDateLabel = SubheadingBigLabel().apply {
        $0.lineBreakMode = .byTruncatingHead
    }

    private let ratingLabel = HeadingLabel()
    private let ratingUpdateLabel = SubheadingBigLabel()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupView()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    private func setupView() {
        
    }

    private func buildViewTree() {
        
    }

    private func setConstraints() {
        
    }

    func bind() {
        
    }
}

