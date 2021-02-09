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
    
    func colorString(color: UIColor) -> () {
        self.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSRange(location: 0, length: self.length))
    }
    
    func addLink(url: String, range: NSRange) {
        addAttribute(.link, value: url, range: range)
    }
    
    func addUnderline(range: NSRange) {
        addAttribute(.underlineStyle, value: 1, range: range)
    }
}

func + (left: NSMutableAttributedString, right: NSMutableAttributedString) -> NSMutableAttributedString {
    let result = NSMutableAttributedString()
    result.append(left)
    result.append(right)
    return result
}
