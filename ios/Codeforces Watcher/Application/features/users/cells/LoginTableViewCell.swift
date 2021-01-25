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
    private let loginToIdentifyCardView = LoginToIdentifyCardView()
    
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

        [galacticMasterCardView, loginToIdentifyCardView].forEach(cardView.addSubview)
    }

    private func setConstraints() {
        cardView.edgesToSuperview(insets: UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8))
        
        galacticMasterCardView.edgesToSuperview(excluding: .trailing)
        
        loginToIdentifyCardView.run {
            $0.width(to: galacticMasterCardView)
            $0.edgesToSuperview(excluding: .leading)
            $0.leadingToTrailing(of: galacticMasterCardView)
        }
    }
    
    func bind() {
        
    }
}
