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

    var users: [UserItem] = []
    var onUserTap: ((Int) -> ())?
    var onLoginTap: () -> () = {}

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        guard !users.isEmpty else { return 1 }
        return users.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard !users.isEmpty else {
            return tableView.dequeueReusableCell(cellType: NoItemsTableViewCell.self).apply {
                $0.bind(imageName: "alienImage", explanation: "no_users_explanation")
            }
        }
        
        switch(users[indexPath.row]) {
        case .loginItem:
            return tableView.dequeueReusableCell(cellType: LoginTableViewCell.self).apply {
                $0.bind(onClick: onLoginTap)
            }
        case .userItem(let item):
            return tableView.dequeueReusableCell(cellType: UserTableViewCell.self).apply {
                $0.bind(item)
            }
        }
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard !users.isEmpty else { return }
        switch(users[indexPath.row]) {
        case .loginItem:
            break
        case .userItem(let item):
            onUserTap?(item.id)
            break
        }
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if users.isEmpty {
            return tableView.frame.height
        } else {
            return UITableView.automaticDimension
        }
    }
}
