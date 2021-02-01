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
    private let forgotPasswordLabel = UILabel().apply {
        $0.text = "forgot_password".localized
        $0.textColor = Palette.colorPrimary
        $0.font = Font.textHint
    }
    private let signInButton = UIButton().apply {
        $0.setTitle("sign_in".localized.uppercased(), for: .normal)
        $0.setTitleColor(Palette.white, for: .normal)
        $0.backgroundColor = Palette.colorPrimary
        $0.layer.cornerRadius = 4
    }
    private let signUpView = SwitchToSignUpView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupView()
    }
    
    private func setupView() {
        view.backgroundColor = .white
        
        title = "sign_in".localized
    
        signInButton.titleLabel?.font = Font.textBody
        
        buildViewTree()
        setConstraints()
        setInteractions()
    }

    private func buildViewTree() {
        view.addSubview(contentView)
        [emailInput, passwordInput, forgotPasswordLabel, signInButton, signUpView].forEach(contentView.addSubview)
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
        
        forgotPasswordLabel.run {
            $0.topToBottom(of: passwordInput, offset: 8)
            $0.horizontalToSuperview()
        }
        
        signInButton.run {
            $0.height(36)
            $0.topToBottom(of: forgotPasswordLabel, offset: 16)
            $0.horizontalToSuperview()
        }
        
        signUpView.run {
            $0.bottomToSuperview()
            $0.centerXToSuperview()
        }
    }
    
    private func setInteractions() {
        
    }
}
