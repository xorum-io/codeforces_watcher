//
//  VerifyInstructionLabel.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/16/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class VerifyInstructionLabel: UILabel {
    
    private var instructionText = NSMutableAttributedString()
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    init(text: String) {
        instructionText = text.attributed

        super.init(frame: .zero)

        setupView()
    }
    
    private func setupView() {
        boldSubstring()
        
        numberOfLines = 0
        
        font = Font.textHint
        textColor = Palette.darkGray
        
        textAlignment = .left
        attributedText = instructionText
    }
    
    private func boldSubstring() {
        let tag = "bold"
        
        let range = instructionText.getRangeAndRemoveTag(tag: tag)
        
        instructionText.run {
            $0.colored(with: Palette.black, range: range)
            $0.changeFont(with: Font.textHintBold, range: range)
        }
    }
}
