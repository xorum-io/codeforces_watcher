//
//  UIViewControllerWithFab.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/13/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit
import MaterialComponents.MDCButton

class UIViewControllerWithFab: UIViewController {
    
    let fabButton = MDCFloatingButton().apply {
        $0.backgroundColor = Palette.colorAccent
        $0.tintColor = Palette.white
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        fabButton.isHidden = false
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        fabButton.isHidden = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tabBarController?.tabBar.addSubview(fabButton)
        
        fabButton.run {
            $0.topToSuperview(offset: -22)
            $0.centerXToSuperview()
        }
        
        fabButton.addTarget(self, action: #selector(fabButtonTapped), for: .touchUpInside)
    }
    
    @objc func fabButtonTapped() {
        
    }
    
    func setFabImage(named: String) {
        fabButton.setImage(UIImage(named: named)?.withRenderingMode(.alwaysTemplate), for: .normal)
    }
}
