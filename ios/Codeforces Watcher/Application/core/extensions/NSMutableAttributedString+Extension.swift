//
//  NSMutableAttributedString+Extension.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 9/10/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//
import Foundation
import UIKit

extension NSMutableAttributedString {
    func colorSubstring(color: UIColor, range: NSRange) -> () {
        self.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: range)
    }
}
