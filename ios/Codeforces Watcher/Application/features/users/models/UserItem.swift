//
//  UserItem.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/19/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import common
import UIKit

enum UserItem {
    
    struct UserItem {
        let id: Int
        let avatar: String
        let rank: String?
        let handle: String
        let rating: Int?
        let maxRating: Int?
        let firstName: String?
        let lastName: String?
        let maxRank: String?
        let ratingChanges: [RatingChange]
        let contribution: Int?
        
        init(_ user: User) {
            id = Int(user.id)
            avatar = user.avatar
            rank = user.rank
            handle = user.handle
            rating = user.rating as? Int
            maxRating = user.maxRating as? Int
            firstName = user.firstName
            lastName = user.lastName
            maxRank = user.maxRank
            ratingChanges = user.ratingChanges
            contribution = user.contribution as? Int
        }
    }
    
    struct UserAccountItem {
        let id: Int
        let avatar: String
        let rank: String?
        let handle: String
        let rating: Int?
        let maxRating: Int?
        let firstName: String?
        let lastName: String?
        let maxRank: String?
        let ratingChanges: [RatingChange]
        let contribution: Int?
        
        init(_ user: User) {
            id = Int(user.id)
            avatar = user.avatar
            rank = user.rank
            handle = user.handle
            rating = user.rating as? Int
            maxRating = user.maxRating as? Int
            firstName = user.firstName
            lastName = user.lastName
            maxRank = user.maxRank
            ratingChanges = user.ratingChanges
            contribution = user.contribution as? Int
        }
    }
    
    case loginItem
    case verifyItem
    case userItem(UserItem)
    case userAccount(UserAccountItem)
}
