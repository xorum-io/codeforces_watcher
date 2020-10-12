//
//  UIView+Extension.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 12.10.2020.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit

extension UIView {
    func onTap(target: Any, action: Selector) {
        isUserInteractionEnabled = true
        addGestureRecognizer(UITapGestureRecognizer(target: target, action: action))
    }
}
