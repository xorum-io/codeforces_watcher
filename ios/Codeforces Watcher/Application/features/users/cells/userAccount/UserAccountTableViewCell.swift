//
//  UserAccountTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 3/10/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit
import common

class UserAccountTableViewCell: UITableViewCell {
    
    private let userAccountView = UserAccountView()
    
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
        addSubview(userAccountView)
    }

    private func setConstraints() {
        userAccountView.edgesToSuperview(insets: UIEdgeInsets(top: 8, left: 8, bottom: 4, right: 8))
    }
    
    func bind(_ user: UserItem.UserAccountItem) {
        userAccountView.bind(user)
    }
}
