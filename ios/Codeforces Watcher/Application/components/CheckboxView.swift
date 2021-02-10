//
//  CheckboxView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/9/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class CheckboxView: UIImageView {
    
    private let checkedImage = UIImage(named: "ic_checkbox_checked")
    private let uncheckedImage = UIImage(named: "ic_checkbox_unchecked")
    
    private var isSelected: Bool = false {
        didSet { image = isSelected ? checkedImage : uncheckedImage }
    }
    
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
        image = uncheckedImage
    }
    
    private func setInteractions() {
        onTap(target: self, action: #selector(didTap))
    }
    
    @objc private func didTap() {
        isSelected = !isSelected
        
        onTap(isSelected)
    }
}
