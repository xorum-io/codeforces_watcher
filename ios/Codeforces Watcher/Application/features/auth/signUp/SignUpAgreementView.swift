//
//  SignUpAgreementView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/8/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class SignUpAgreementView: UIView {
    
    private let contentView = UIView()
    
    private let checkBox = UIButton().apply {
        $0.height(18)
        $0.width(18)
        $0.layer.run {
            $0.borderColor = Palette.black.cgColor
            $0.borderWidth = 2
        }
    }
    private let agreementLabel = AgreementTextView().apply {
        $0.height(40)
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
        [checkBox, agreementLabel].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        checkBox.run {
            $0.leadingToSuperview()
            $0.centerYToSuperview()
        }
        
        agreementLabel.run {
            $0.leadingToTrailing(of: checkBox, offset: 12)
            $0.trailingToSuperview()
            $0.verticalToSuperview()
        }
    }
    
    private func setInteractions() {
        
    }
}
