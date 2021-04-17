//
//  FilterTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/28/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit
import common

class FilterTableViewCell: UITableViewCell {
    
    private var logoView = CircleImageView()
    private let nameLabel = HeadingLabel()

    private let switchView = UISwitch().apply {
        $0.onTintColor = Palette.colorPrimary
    }

    private var platform: Platform!

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
        [logoView, nameLabel, switchView].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        logoView.run {
            $0.leadingToSuperview(offset: 16)
            $0.height(40)
            $0.width(40)
            $0.centerYToSuperview()
        }

        nameLabel.run {
            $0.centerYToSuperview()
            $0.leadingToTrailing(of: logoView, offset: 8)
        }

        switchView.run {
            $0.centerYToSuperview()
            $0.trailingToSuperview(offset: 16)
        }
    }

    private func setInteractions() {
        switchView.addTarget(self, action: #selector(switchTrigger), for: UIControl.Event.valueChanged)
    }
    
    @objc func switchTrigger(mySwitch: UISwitch) {
        store.dispatch(action: ContestsRequests.ChangeFilterCheckStatus(platform: platform, isChecked: switchView.isOn))
    }

    func bind(_ filterItem: FilterItem) {
        logoView.image = UIImage(named: Platform.getImageNameByPlatform(filterItem.platform))
        nameLabel.text = filterItem.title
        switchView.isOn = filterItem.isOn
        platform = filterItem.platform
    }
}
