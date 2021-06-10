//
//  DoActionToIdentifyView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/12/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class DoActionToIdentifyView: UIView {
    
    private var onButtonTap: () -> () = {}
    
    private let contentView = UIView()
    
    private let titleLabel = UILabel().apply {
        $0.textColor = Palette.darkGray
        $0.font = Font.textBody
        $0.textAlignment = .center
        $0.numberOfLines = 0
    }
    
    private let subtitleLabel = UILabel().apply {
        $0.textColor = Palette.darkGray
        $0.font = Font.textSubheading
        $0.numberOfLines = 0
        $0.textAlignment = .center
    }
    
    private let button = PrimaryButton().apply {
        $0.titleLabel?.font = Font.textSubheading
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
        
        
        buildViewTree()
        setConstraints()
        setInteractions()
    }

    private func buildViewTree() {
        addSubview(contentView)
        [titleLabel, subtitleLabel, button].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        titleLabel.run {
            $0.topToSuperview(offset: 38)
            $0.horizontalToSuperview(insets: .horizontal(8))
        }
        
        subtitleLabel.run {
            $0.topToBottom(of: titleLabel, offset: 4)
            $0.horizontalToSuperview(insets: .horizontal(8))
        }
        
        button.run {
            $0.height(24)
            $0.horizontalToSuperview(insets: .horizontal(8))
            $0.bottomToSuperview(offset: -8)
        }
    }
    
    private func setInteractions() {
        button.addGestureRecognizer(
            UITapGestureRecognizer(target: self, action: #selector(loginButtonTapped))
        )
    }
    
    @objc func loginButtonTapped() {
        onButtonTap()
    }
    
    struct UIModel {
        let title: String
        let subtitle: String
        let buttonText: String
        let onButtonTap: () -> ()
    }

    func bind(_ uiModel: UIModel) {
        titleLabel.text = uiModel.title
        subtitleLabel.text = uiModel.subtitle
        button.setTitle(uiModel.buttonText.uppercased(), for: .normal)
        onButtonTap = uiModel.onButtonTap
    }
}
