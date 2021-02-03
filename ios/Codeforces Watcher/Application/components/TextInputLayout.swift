//
//  TextInputLayout.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/29/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class TextInputLayout: UIView {
    
    private let hintLabel = UILabel().apply {
        $0.font = Font.textHint
        $0.textColor = Palette.darkGray
    }
    let textField = CommonTextField()
    
    enum TypeOfField {
        case email
        case password
        case text
    }
    
    init(hint: String, type: TypeOfField = .text) {
        hintLabel.text = hint
        
        switch(type) {
        case .email:
            textField.keyboardType = .emailAddress
        case .password:
            textField.isSecureTextEntry = true
        default:
            break
        }
        
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
        [hintLabel, textField].forEach(addSubview)
    }
    
    private func setConstraints() {
        hintLabel.edgesToSuperview(excluding: .bottom)
        
        textField.run {
            $0.topToBottom(of: hintLabel, offset: 8)
            $0.edgesToSuperview(excluding: .top)
        }
    }
}
