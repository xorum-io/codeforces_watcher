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
    
    private let galacticMasterCardView = GalacticMasterCardView()
    
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

        [galacticMasterCardView].forEach(cardView.addSubview)
    }

    private func setConstraints() {
        cardView.edgesToSuperview()
        galacticMasterCardView.edgesToSuperview()
    }
    
    func bind() {
        
    }
}

