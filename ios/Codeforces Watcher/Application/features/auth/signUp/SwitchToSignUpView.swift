//
//  SwitchToSignUpView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/1/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class SwitchToSignUpView: UIView {
    
    private let contentView = UIView()
    
    private let hintLabel = UILabel().apply {
        $0.text = "sign_up_hint".localized
        $0.textColor = Palette.darkGray
        $0.font = Font.textHint
    }
    private let switchLabel = UILabel().apply {
        $0.text = "sign_up".localized
        $0.textColor = Palette.colorPrimary
        $0.font = Font.textHint
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
        clipsToBounds = false;
        
        buildViewTree()
        setConstraints()
        setInteractions()
    }

    private func buildViewTree() {
        addSubview(contentView)
        [hintLabel, switchLabel].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        hintLabel.edgesToSuperview(excluding: .trailing)
        
        switchLabel.run {
            $0.leadingToTrailing(of: hintLabel, offset: 2)
            $0.trailingToSuperview()
        }
    }
    
    private func setInteractions() {
        switchLabel.onTap(target: self, action: #selector(jumpLabelTapped))
    }
    
    @objc func jumpLabelTapped() {
        print("SwitchToSignUp")
    }
}
