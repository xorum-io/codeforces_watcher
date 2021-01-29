//
//  SignInViewController.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/29/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit
import common

class SignInViewController: ClosableViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupView()
    }
    
    private func setupView() {
        title = "Sign In"
        
        view.backgroundColor = .white
        
        buildViewTree()
        setConstraints()
        setInteractions()
    }
    
    private func buildViewTree() {
        
    }
    
    private func setConstraints() {
        
    }
    
    private func setInteractions() {
        
    }
}
