//
//  VerifyTableViewCell.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/12/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class VerifyTableViewCell: UITableViewCell {

    private let verifyView = VerifyView()
    
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
        addSubview(verifyView)
    }

    private func setConstraints() {
        verifyView.edgesToSuperview(insets: UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8))
    }
    
    func bind(onClick: @escaping () -> ()) {
        verifyView.bind(onClick: onClick)
    }
}
