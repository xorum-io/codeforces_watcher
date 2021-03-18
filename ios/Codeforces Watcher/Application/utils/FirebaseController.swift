//
//  FirebaseController.swift
//  Codeforces Watcher
//
//  Created by Den Matiash on 17.03.2021.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import Foundation
import FirebaseAuth
import common

class FirebaseController: IFirebaseController {
    
    private var auth: Auth { Auth.auth() }
    
    func signIn(email: String, password: String, callback: @escaping (String?, KotlinException?) -> Void) {
        auth.signIn(withEmail: email, password: password) { authResult, e in
            if let e = e {
                callback(nil, KotlinException(message: e.localizedDescription))
                return
            }
            self.fetchToken { token, e in
                if let token = token {
                    callback(token, nil)
                } else {
                    callback(nil, e)
                }
            }
        }
    }
    
    func signUp(email: String, password: String, callback: @escaping (String?, KotlinException?) -> Void) {
        auth.createUser(withEmail: email, password: password) { authResult, e in
            if let e = e {
                callback(nil, KotlinException(message: e.localizedDescription))
                return
            }
            self.fetchToken { token, e in
                if let token = token {
                    callback(token, nil)
                } else {
                    callback(nil, e)
                }
            }
        }
    }
    
    func fetchToken(callback: @escaping (String?, KotlinException?) -> Void) {
        guard let _ = auth.currentUser else {
            callback(nil, nil)
            return
        }
        
        auth.currentUser?.getIDToken { token, e in
            if let token = token {
                callback(token, nil)
            } else {
                callback(nil, KotlinException(message: e?.localizedDescription))
            }
        }
    }
    
    func logOut(callback: @escaping (KotlinException?) -> Void) {
        do {
            try auth.signOut()
            callback(nil)
        } catch let error as NSError {
            callback(KotlinException(message: error.localizedDescription))
        }
    }
}
