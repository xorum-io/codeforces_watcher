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
    
    private let stackView = UIStackView().apply {
        $0.axis = .horizontal
        $0.spacing = 8
    }
    
    private var logoView = CircleImageView()
    private let nameLabel = HeadingLabel()

    private let switchView = UISwitch().apply {
        $0.onTintColor = Palette.colorPrimary
    }
    
    private var onSwitchTap: (_ isOn: Bool) -> () = { _ in }

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
        [stackView, switchView].forEach(contentView.addSubview)
        [logoView, nameLabel].forEach(stackView.addArrangedSubview)
    }

    private func setConstraints() {
        logoView.run {
            $0.height(40)
            $0.width(40)
            $0.centerYToSuperview()
        }
        
        stackView.run {
            $0.leadingToSuperview(offset: 16)
            $0.centerYToSuperview()
        }

        nameLabel.centerYToSuperview()

        switchView.run {
            $0.centerYToSuperview()
            $0.trailingToSuperview(offset: 16)
        }
    }

    private func setInteractions() {
        switchView.addTarget(self, action: #selector(switchTrigger), for: UIControl.Event.valueChanged)
    }
    
    @objc func switchTrigger(mySwitch: UISwitch) {
        onSwitchTap(switchView.isOn)
    }

    func bind(_ filterItem: FilterItem) {
        if let platform = filterItem.platform {
            logoView.image = UIImage(named: Contest.Platform.getImageNameByPlatform(platform))
        } else {
            logoView.isHidden = true
        }
        nameLabel.text = filterItem.title
        switchView.isOn = filterItem.isOn
        onSwitchTap = filterItem.onSwitchTap
    }
}
