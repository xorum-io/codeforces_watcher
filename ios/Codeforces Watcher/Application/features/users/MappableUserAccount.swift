//
//  MappableUserAccount.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/11/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import common
import ObjectMapper

class MappableUserAccount: Mappable {
    
    var codeforcesUser: User?
    var email: String = ""
    var token: String = ""

    init(codeforcesUser: User?, email: String, token: String) {
        self.codeforcesUser = codeforcesUser
        self.email = email
        self.token = token
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        codeforcesUser <- map["codeforcesUser"]
        email          <- map["email"]
        token          <- map["token"]
    }
}
