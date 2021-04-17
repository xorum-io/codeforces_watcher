//
//  LoginTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/22/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class LoginTableViewCell: UITableViewCell {

    private let loginView = LoginView()
    
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
        contentView.addSubview(loginView)
    }

    private func setConstraints() {
        loginView.edgesToSuperview(insets: UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8))
    }
    
    func bind(onClick: @escaping () -> ()) {
        loginView.bind(onClick: onClick)
    }
}
