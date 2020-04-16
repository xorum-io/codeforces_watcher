//
//  UsersTableViewAdapter.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/14/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit
import common

class UsersTableViewAdapter: NSObject, UITableViewDelegate, UITableViewDataSource {
    var users: [User] = []

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (users.isEmpty) {
            return 1
        }

        return users.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (users.isEmpty) {
            return tableView.dequeueReusableCell(cellType: NoItemsTableViewCell.self).apply {
                $0.bind(imageName: "alienImage", explanation: "no_users_explanation")
            }
        }

        return tableView.dequeueReusableCell(cellType: UserTableViewCell.self).apply {
            $0.bind(users[indexPath.row])
        }
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if (users.isEmpty) {
            return tableView.frame.height
        } else {
            return UITableView.automaticDimension
        }
    }
}