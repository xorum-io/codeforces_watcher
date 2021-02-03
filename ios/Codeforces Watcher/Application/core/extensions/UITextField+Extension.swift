//
//  UITextField+Extension.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/1/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

extension UITextField {
    
    func setupKeyboard() {
        let bar = UIToolbar()
        
        let doneButton = UIBarButtonItem(title: "Done", style: .plain, target: self, action: #selector(dismissKeyboard))
        
        let flexibleSpace = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        
        bar.run {
            $0.items = [flexibleSpace, doneButton]
            $0.sizeToFit()
        }
        
        inputAccessoryView = bar
    }
    
    @objc func dismissKeyboard() {
        endEditing(true)
    }
}
