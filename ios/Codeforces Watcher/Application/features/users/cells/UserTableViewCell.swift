//
//  UserTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/14/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import UIKit
import common

class UserTableViewCell: UITableViewCell {

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
        selectionStyle = .none

        buildViewTree()
        setConstraints()
    }

    private func buildViewTree() {
        addSubview(cardView)

        [userImage, handleLabel, ratingUpdateDateLabel, ratingLabel, ratingUpdateLabel].forEach(cardView.addSubview)
    }

    private func setConstraints() {
        cardView.edgesToSuperview(insets: UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8))

        userImage.run {
            $0.leadingToSuperview(offset: 8)
            $0.height(36)
            $0.width(36)
            $0.centerYToSuperview()
        }

        handleLabel.run {
            $0.topToSuperview(offset: 8)
            $0.leadingToTrailing(of: userImage, offset: 8)
            $0.trailingToLeading(of: ratingLabel, relation: .equalOrLess)
        }

        ratingUpdateDateLabel.run {
            $0.topToBottom(of: handleLabel, offset: 4)
            $0.leading(to: handleLabel)
            $0.bottomToSuperview(offset: -8)
            $0.trailingToLeading(of: ratingUpdateLabel, offset: -8)
        }

        ratingLabel.run {
            $0.topToSuperview(offset: 8)
            $0.trailingToSuperview(offset: 8)
        }

        ratingUpdateLabel.run {
            $0.topToBottom(of: ratingLabel, offset: 4)
            $0.trailing(to: ratingLabel)
            $0.bottomToSuperview(offset: -8)
        }
    }

    func bind(_ user: User) {
        ratingLabel.text = ""
        ratingUpdateLabel.text = ""

        let avatar = LinkValidatorKt.avatar(avatarLink: user.avatar)
        userImage.sd_setImage(with: URL(string: avatar), placeholderImage: noImage)
        userImage.layer.borderColor = getColorByUserRank(rank: user.rank).cgColor

        handleLabel.attributedText = colorTextByUserRank(text: user.handle, rank: user.rank)

        if let rating = user.ratingChanges.last?.newRating {
            ratingLabel.attributedText = colorTextByUserRank(text: String(rating), rank: user.rank)
        }

        if let ratingChange = user.ratingChanges.last {
            let delta = ratingChange.newRating - ratingChange.oldRating
            let isRatingIncreased = delta >= 0
            let ratingUpdateString = (isRatingIncreased ? "▲" : "▼") + " \(abs(delta))"

            ratingUpdateLabel.attributedText = ratingUpdateString.colorString(color: isRatingIncreased ? Palette.brightGreen : Palette.red)
            ratingUpdateDateLabel.text = "Last rating update".localizedFormat(args: Double(ratingChange.ratingUpdateTimeSeconds).secondsToUserUpdateDateString())
        } else {
            ratingUpdateDateLabel.text = "No rating update".localized
        }
    }
}
