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
    private let dateOfLastRatingUpdateLabel = SubheadingBigLabel().apply {
        $0.lineBreakMode = .byTruncatingHead
    }

    private let ratingLabel = HeadingLabel()
    private let valueOfLastRatingUpdateLabel = SubheadingBigLabel()

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

        [userImage, handleLabel, dateOfLastRatingUpdateLabel, ratingLabel, valueOfLastRatingUpdateLabel].forEach(cardView.addSubview)
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

        dateOfLastRatingUpdateLabel.run {
            $0.topToBottom(of: handleLabel, offset: 4)
            $0.leading(to: handleLabel)
            $0.bottomToSuperview(offset: -8)
            $0.trailingToLeading(of: valueOfLastRatingUpdateLabel, offset: -8)
        }

        ratingLabel.run {
            $0.topToSuperview(offset: 8)
            $0.trailingToSuperview(offset: 8)
        }

        valueOfLastRatingUpdateLabel.run {
            $0.topToBottom(of: ratingLabel, offset: 4)
            $0.trailing(to: ratingLabel)
            $0.bottomToSuperview(offset: -8)
        }
    }

    func bind(_ user: UserItem.UserItem) {
        userImage.run {
            $0.sd_setImage(with: URL(string: user.avatar), placeholderImage: noImage)
            $0.layer.borderColor = getColorByUserRank(user.rank).cgColor
        }
        
        handleLabel.attributedText = user.handleText
        ratingLabel.attributedText = user.ratingText
        dateOfLastRatingUpdateLabel.text = user.ratingUpdateDateText
        valueOfLastRatingUpdateLabel.attributedText = user.ratingUpdateValueText
    }
}

fileprivate extension UserItem.UserItem {
    
    private var blank: NSAttributedString {
        return NSAttributedString(string: "".localized)
    }
    
    var handleText: NSAttributedString {
        return colorTextByUserRank(text: handle, rank: rank)
    }
    
    var ratingText: NSAttributedString {
        if let rating = rating {
            return colorTextByUserRank(text: "\(rating)", rank: rank)
        } else {
            return blank
        }
    }
    
    var ratingUpdateDateText: String {
        if let ratingChange = ratingChanges.last {
            return "last_rating_update".localizedFormat(args: Double(ratingChange.ratingUpdateTimeSeconds).secondsToUserUpdateDateString())
        } else {
            return "no_rating_update".localized
        }
    }
    
    var ratingUpdateValueText: NSAttributedString {
        if let ratingChange = ratingChanges.last {
            let delta = ratingChange.newRating - ratingChange.oldRating
            let isRatingIncreased = delta >= 0
            let ratingUpdateString = (isRatingIncreased ? "▲" : "▼") + " \(abs(delta))"

            return ratingUpdateString.colorString(color: isRatingIncreased ? Palette.brightGreen : Palette.red)
        } else {
            return blank
        }
    }
}
