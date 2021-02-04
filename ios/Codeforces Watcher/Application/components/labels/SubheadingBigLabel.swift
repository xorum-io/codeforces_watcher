//
//  SubheadingBigLabel.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/14/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit

class SubheadingBigLabel: UILabel {
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }
    
    private func setupView() {
        textColor = Palette.gray
        font = Font.textSubheadingBig
        numberOfLines = 1
    }
}
