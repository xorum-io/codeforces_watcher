//
//  CheckboxView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/9/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class CheckboxView: UIImageView {
    
    private enum Status {
        case selected
        case unselected
    }
    
    private var status: Status = .unselected
    
    var onTap: (Bool) -> () = {_ in }
    
    convenience init() {
        self.init(frame: CGRect.zero)
    }
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupView()
        setInteractions()
    }

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        
        setupView()
        setInteractions()
    }
    
    private func setupView() {
        if let image = UIImage(named: "ic_checkbox_unchecked") {
            self.image = image
        }
    }
    
    private func setInteractions() {
        onTap(target: self, action: #selector(didTap))
    }
    
    @objc private func didTap() {
        switch status {
        case .unselected:
            if let image = UIImage(named: "ic_checkbox_checked") {
                self.image = image
            }
            status = .selected
        case .selected:
            if let image = UIImage(named: "ic_checkbox_unchecked") {
                self.image = image
            }
            status = .unselected
        }
        
        onTap(status == .selected)
    }
}
