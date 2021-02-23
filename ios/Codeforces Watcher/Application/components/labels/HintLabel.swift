//
//  HintLabel.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/16/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class HintLabel: UILabel {
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }
    
    private func setupView() {
        numberOfLines = 0
        
        textColor = Palette.darkGray
        font = Font.textHint
        
        textAlignment = .left
    }
}

