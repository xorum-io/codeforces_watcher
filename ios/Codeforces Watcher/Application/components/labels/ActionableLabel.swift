//
//  ActionableLabel.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/3/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class ActionableLabel: UILabel {
    
    var hintText = NSMutableAttributedString()
    var linkText = NSMutableAttributedString()
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    init(hintText: String, linkText: String) {
        self.hintText = hintText.attributed
        self.linkText = linkText.attributed

        super.init(frame: .zero)

        setupView()
    }
    
    private func setupView() {
        hintText.colorString(color: Palette.darkGray)
        linkText.colorString(color: Palette.colorPrimary)
        
        attributedText = hintText + " ".attributed + linkText
        
        font = Font.textHint
    }
}
