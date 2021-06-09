//
//  CommonTextField.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/15/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit

class CommonTextField: UITextField {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupView() {
        font = Font.textBody
        borderStyle = .none
        tintColor = Palette.colorPrimary
        autocorrectionType = .no
        spellCheckingType = .no
        autocapitalizationType = .none
        
        layer.run {
            $0.backgroundColor = Palette.white.cgColor
            $0.masksToBounds = false
            $0.shadowColor = Palette.colorPrimary.cgColor
            $0.shadowOffset = CGSize(width: 0.0, height: 1.0)
            $0.shadowOpacity = 1.0
            $0.shadowRadius = 0.0
        }
    }
}
