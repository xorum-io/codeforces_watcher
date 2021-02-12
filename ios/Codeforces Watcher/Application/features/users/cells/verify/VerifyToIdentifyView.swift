//
//  VerifyToIdentifyView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/12/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class VerifyToIdentifyView: UIView {
    
    private var onVerifyTap: () -> () = {}
    
    private let contentView = UIView()
    
    private let verifyLabel = UILabel().apply {
        $0.text = "verify_to_identify".localized
        $0.textColor = Palette.darkGray
        $0.font = Font.textBody
    }
    private let promptLabel = UILabel().apply {
        $0.text = "prompt_to_verifyToIdentify".localized
        $0.textColor = Palette.darkGray
        $0.font = Font.textSubheading
        $0.numberOfLines = 0
        $0.textAlignment = .center
    }
    private let verifyButton = PrimaryButton().apply {
        $0.setTitle("verify_in_42_seconds".localized.uppercased(), for: .normal)
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
        
        verifyButton.titleLabel?.font = Font.textSubheading
        
        buildViewTree()
        setConstraints()
        setInteractions()
    }

    private func buildViewTree() {
        addSubview(contentView)
        [verifyLabel, promptLabel, verifyButton].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        verifyLabel.run {
            $0.topToSuperview(offset: 38)
            $0.centerXToSuperview()
        }
        
        promptLabel.run {
            $0.topToBottom(of: verifyLabel, offset: 4)
            $0.centerXToSuperview()
        }
        
        verifyButton.run {
            $0.height(24)
            $0.horizontalToSuperview(insets: .horizontal(8))
            $0.bottomToSuperview(offset: -8)
        }
    }
    
    private func setInteractions() {
        verifyButton.addGestureRecognizer(
            UITapGestureRecognizer(target: self, action: #selector(loginButtonTapped))
        )
    }
    
    @objc func loginButtonTapped() {
        onVerifyTap()
    }

    func bind(onClick: @escaping () -> ()) {
        onVerifyTap = onClick
    }
}

