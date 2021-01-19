//
//  LoginToIdentifyView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/19/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit
import TinyConstraints

class LoginToIdentifyView: UIView {
    
    let contentView = UIView().apply {
        $0.height(100)
        $0.backgroundColor = .red
    }
    
    init() {
        super.init(frame: .zero)
        
        setupView()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
        
    private func setupView() {
        
        buildViewTree()
        setConstraints()
    }
    
    private func buildViewTree() {
        addSubview(contentView)
    }
    
    private func setConstraints() {
        contentView.edgesToSuperview()
    }
}

