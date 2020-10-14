//
//  NoItemsTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/15/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import UIKit

class NoItemsTableViewCell: UITableViewCell {

    private let noItemsImage = UIImageView()
    private let explanationLabel = HeadingLabel().apply {
        $0.numberOfLines = 0
    }

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupView()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    private func setupView() {
        self.selectionStyle = .none

        buildViewTree()
        setConstraints()
    }

    private func buildViewTree() {
        [noItemsImage, explanationLabel].forEach(addSubview)
    }

    private func setConstraints() {
        noItemsImage.run {
            $0.centerXToSuperview()
            $0.centerYToSuperview(offset: -(16 + explanationLabel.frame.height))
        }

        explanationLabel.run {
            $0.topToBottom(of: noItemsImage, offset: 16)
            $0.textAlignment = .center
            $0.leadingToSuperview(offset: 16)
            $0.trailingToSuperview(offset: 16)
        }
    }
    
    func bind(imageName: String, explanation: String) {
        noItemsImage.image = UIImage(named: imageName)
        explanationLabel.text = explanation.localized
    }
}
