//
//  SmallButton.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 5/6/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit
import TinyConstraints

class SmallButton: UIButton {
    
    private let color = Palette.colorPrimary

    enum Mode {
        case bordered, filled
    }

    var mode: Mode = .filled {
        didSet {
            if oldValue != mode {
                setupMode()
            }
        }
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
        titleLabel?.font = Font.textButton
        layer.cornerRadius = 12
        
        setupMode()
        setConstraints()
    }

    private func setupMode() {
        switch mode {
        case .bordered:
            layer.run {
                $0.backgroundColor = Palette.white.cgColor
                $0.borderWidth = 1
                $0.borderColor = color.cgColor
            }
            setTitleColor(color, for: .normal)
        default:
            layer.run {
                $0.backgroundColor = color.cgColor
            }
            setTitleColor(Palette.white, for: .normal)
        }
    }
    
    private func setConstraints() {
        height(30)
        width(80)
    }
}
