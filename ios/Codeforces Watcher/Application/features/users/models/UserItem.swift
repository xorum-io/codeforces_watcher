//
//  UserItem.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/19/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import common
import UIKit

enum UsersItem {
    
    struct LoginToIdentifyItem {
        let user: User
        
        init(_ user: User) {
            self.user = user
        }
    }
    
    struct UserItem {
        let user: User
        
        init(_ user: User) {
            self.user = user
        }
    }
    
    case loginToIdentifyItem(LoginToIdentifyItem)
    case userItem(UserItem)
}
