//
//  PrimaryButton.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/3/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class PrimaryButton: UIButton {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupView() {
        backgroundColor = Palette.colorPrimary
        layer.cornerRadius = 4
        setTitleColor(Palette.white, for: .normal)
        titleLabel?.font = Font.textBody
    }
}
