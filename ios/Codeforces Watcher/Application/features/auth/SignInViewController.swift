//
//  SignInViewController.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/29/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit
import common
import PKHUD

class SignInViewController: ClosableViewController, UITextFieldDelegate {
    
    private let contentView = UIView()
    
    private let emailInput = TextInputLayout(explanation: "email".localized, type: .email).apply {
        $0.tag = 0
    }

    private let passwordInput = TextInputLayout(explanation: "password".localized, type: .password).apply {
        $0.tag = 1
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupView()
    }
    
    private func setupView() {
        view.backgroundColor = .white
        
        title = "sign_in".localized
        
        buildViewTree()
        setConstraints()
        setInteractions()
    }

    private func buildViewTree() {
        view.addSubview(contentView)
        [emailInput, passwordInput].forEach(contentView.addSubview)
    }
    
    private func setConstraints() {
        contentView.edgesToSuperview(insets: UIEdgeInsets(top: 16, left: 16, bottom: 16, right: 16))
        
        emailInput.run {
            $0.topToSuperview()
            $0.horizontalToSuperview()
        }
        
        passwordInput.run {
            $0.topToBottom(of: emailInput, offset: 16)
            $0.horizontalToSuperview()
        }
    }
    
    private func setInteractions() {
        
    }
}
