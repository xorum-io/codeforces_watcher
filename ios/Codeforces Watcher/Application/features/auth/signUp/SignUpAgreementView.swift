//
//  SignUpAgreementView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/8/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class SignUpAgreementView: UIView {
    
    var onCheckboxTap: (Bool) -> () = { _ in }
    
    private let checkbox = CheckboxView()
    private let agreementLabel = AgreementTextView()
    
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
        
        checkbox.onTap = { isSelected in
            self.onCheckboxTap(isSelected)
        }
        
        buildViewTree()
        setConstraints()
    }

    private func buildViewTree() {
        [checkbox, agreementLabel].forEach(addSubview)
    }

    private func setConstraints() {
        checkbox.run {
            $0.height(18)
            $0.width(18)
            $0.leadingToSuperview()
            $0.centerYToSuperview()
        }
        
        agreementLabel.run {
            $0.leadingToTrailing(of: checkbox, offset: 8)
            $0.trailingToSuperview()
            $0.verticalToSuperview()
        }
    }
}
