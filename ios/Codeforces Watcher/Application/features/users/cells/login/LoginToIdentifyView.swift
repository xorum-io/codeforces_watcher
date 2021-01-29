//
//  LoginToIdentifyView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/28/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class LoginToIdentifyView: UIView {
    
    private let contentView = UIView()
    
    private let loginLabel = UILabel().apply {
        $0.text = "login_to_identify".localized
        $0.textColor = Palette.darkGray
        $0.font = Font.textBody
    }
    private let promptLabel = UILabel().apply {
        $0.text = "prompt_to_loginToIdentify".localized
        $0.textColor = Palette.darkGray
        $0.font = Font.textSubheading
        $0.numberOfLines = 0
        $0.textAlignment = .center
    }
    private let loginButton = UIButton().apply {
        $0.setTitle("login_in_42_seconds".localized.uppercased(), for: .normal)
        $0.setTitleColor(Palette.white, for: .normal)
        $0.backgroundColor = Palette.colorPrimary
        $0.layer.cornerRadius = 4
    }
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    private func setupView() {
        clipsToBounds = false
        
        loginButton.titleLabel?.font = Font.textSubheading
        
        buildViewTree()
        setConstraints()
        setInteractions()
    }

    private func buildViewTree() {
        addSubview(contentView)
        [loginLabel, promptLabel, loginButton].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        loginLabel.run {
            $0.topToSuperview(offset: 38)
            $0.centerXToSuperview()
        }
        
        promptLabel.run {
            $0.topToBottom(of: loginLabel, offset: 4)
            $0.centerXToSuperview()
        }
        
        loginButton.run {
            $0.height(24)
            $0.horizontalToSuperview(insets: .horizontal(8))
            $0.bottomToSuperview(offset: -8)
        }
    }
    
    private func setInteractions() {
        loginButton.addGestureRecognizer(
            UITapGestureRecognizer(target: self, action: #selector(loginButtonTapped))
        )
    }
    
    @objc func loginButtonTapped() {
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        let signInViewController = SignInViewController()
        appDelegate.rootViewController.presentModal(signInViewController)
    }
}
