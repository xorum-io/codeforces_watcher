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
                callback(nil, e.toKotlinException())
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
                callback(nil, e.toKotlinException())
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
                callback(nil, e?.toKotlinException())
            }
        }
    }
    
    func logOut(callback: @escaping (KotlinException?) -> Void) {
        do {
            try auth.signOut()
            callback(nil)
        } catch let e as NSError {
            callback(e.toKotlinException())
        }
    }
    
    func sendPasswordReset(email: String, callback: @escaping (KotlinException?) -> Void) {
        auth.sendPasswordReset(withEmail: email) { e in
            if let e = e {
                callback(e.toKotlinException())
            } else {
                callback(nil)
            }
        }
    }
}

fileprivate extension Error {
    
    func toKotlinException() -> KotlinException {
        KotlinException(message: self.localizedDescription)
    }
}
