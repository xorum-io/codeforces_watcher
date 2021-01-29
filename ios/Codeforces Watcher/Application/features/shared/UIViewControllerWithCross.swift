//
//  ClosableViewController.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/17/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit

class ClosableViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            image: UIImage(named: "crossIcon"), 
            style: .plain, 
            target: self, 
            action: #selector(crossTapped)
        )
    }
    
    @objc func crossTapped() {
        dismiss(animated: true)
    }
}
