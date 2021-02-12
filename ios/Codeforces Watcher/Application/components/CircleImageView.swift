//
//  CircleImageView.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/13/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit

class CircleImageView: UIImageView {
    
    convenience init() {
        self.init(frame: CGRect.zero)
    }
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()

        layer.cornerRadius = bounds.size.width / 2.0
    }
    
    private func setupView() {
        layer.run {
            $0.masksToBounds = true
            $0.borderWidth = 1
            $0.borderColor = Palette.colorPrimary.cgColor
        }
    }
}
