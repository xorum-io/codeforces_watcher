//
//  ContestTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/15/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import UIKit
import EventKit
import common

class ContestTableViewCell: UITableViewCell {
    
    private var onCalendarTap: (() -> ())?
    private let cardView = CardView()

    private var logoView = CircleImageView()
    private let nameLabel = HeadingLabel()

    private let timeLabel = SubheadingBigLabel()

    private let calendarAddIcon = UIImageView(image: UIImage(named: "calendarAddIcon"))

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupView()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    @objc func calendarIconTapped(recognizer: UITapGestureRecognizer) {
        onCalendarTap?()
    }

    private func setupView() {
        self.selectionStyle = .none

        calendarAddIcon.run {
            $0.isUserInteractionEnabled = true
            $0.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(calendarIconTapped)))
        }

        buildViewTree()
        setConstraints()
    }

    private func buildViewTree() {
        contentView.addSubview(cardView)

        [logoView, nameLabel, timeLabel, calendarAddIcon].forEach(cardView.addSubview)
    }

    private func setConstraints() {
        cardView.edgesToSuperview(insets: UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8))

        logoView.run {
            $0.leadingToSuperview(offset: 8)
            $0.height(36)
            $0.width(36)
            $0.centerYToSuperview()
        }

        calendarAddIcon.run {
            $0.trailingToSuperview(offset: 10)
            $0.centerYToSuperview()
        }

        nameLabel.run {
            $0.topToSuperview(offset: 8)
            $0.leadingToTrailing(of: logoView, offset: 8)
            $0.trailingToSuperview(offset: 48)
        }

        timeLabel.run {
            $0.topToBottom(of: nameLabel, offset: 4)
            $0.leadingToTrailing(of: logoView, offset: 8)
        }
    }

    func bind(_ contest: Contest, completion: @escaping (() -> ())) {
        nameLabel.text = contest.title
        timeLabel.text = Double(contest.startDateInMillis / 1000).secondsToContestDateString()
        logoView.image = UIImage(named: Contest.Platform.getImageNameByPlatform(contest.platform))

        onCalendarTap = completion
    }
}
