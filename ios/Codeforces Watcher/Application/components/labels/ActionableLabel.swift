//
//  ActionableLabel.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/3/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class ActionableLabel: UILabel {
    
    private var hintText = NSMutableAttributedString()
    private var linkText = NSMutableAttributedString()
    
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
        hintText.colored(with: Palette.darkGray)
        linkText.colored(with: Palette.colorPrimary)
        
        attributedText = hintText + " ".attributed + linkText
        
        font = Font.textHint
    }
}
