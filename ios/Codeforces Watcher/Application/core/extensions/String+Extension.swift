//
// Created by Yevhenii Kanivets on 11/01/2020.
// Copyright (c) 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit

extension String {

    var localized: String {
        let string = NSLocalizedString(self, comment: "")

        if
            string == self, //the translation was not found
            let baseLanguagePath = Bundle.main.path(forResource: "Base", ofType: "lproj"),
            let baseLangBundle = Bundle(path: baseLanguagePath) {

            return NSLocalizedString(self, bundle: baseLangBundle, comment: "")
        } else {
            return string
        }
    }
    
    func localizedFormat(args: CVarArg...) -> String {
        let format = self.localized
        return String(format: format, arguments: args)
    }
    
    var attributed: NSMutableAttributedString { return NSMutableAttributedString(string: self) }
    
    func colorString(color: UIColor) -> NSAttributedString {
        return self.attributed.apply {
            $0.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSRange(location: 0, length: self.count))
        }
    }
}

func buildShareText(_ title: String, _ link: String) -> String {
    return "share_text".localizedFormat(args: title, link)
}
