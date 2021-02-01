//
//  UIViewController+Extension.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/17/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit

extension UIViewController {
    
    func presentModal(_ viewController: UIViewController) {
        present(UINavigationController(rootViewController: viewController).apply() {
            $0.modalPresentationStyle = .fullScreen
            $0.modalTransitionStyle = .crossDissolve
        }, animated: true)
    }
}
