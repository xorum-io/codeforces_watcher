//
//  ContestsTableViewAdapter.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/15/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit
import common

class ContestsTableViewAdapter: NSObject, UITableViewDelegate, UITableViewDataSource {
    
    var contests: [Contest] = []

    var onCalendarTap: ((Contest) -> ())?
    var onContestClick: ((Contest) -> ())?

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (contests.isEmpty) {
            return 1
        }

        return contests.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (contests.isEmpty) {
            return tableView.dequeueReusableCell(cellType: NoItemsTableViewCell.self).apply {
                $0.bind(imageName: "noItemsImage", explanation: "contests_explanation")
            }
        }

        return tableView.dequeueReusableCell(cellType: ContestTableViewCell.self).apply {
            $0.bind(contests[indexPath.row]) {
                self.onCalendarTap?(self.contests[indexPath.row])
            }
        }
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if contests.isEmpty { return }
        onContestClick?(contests[indexPath.row])
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if (contests.isEmpty) {
            return tableView.frame.height - 2 * tableView.tableHeaderView!.frame.height
        } else {
            return 63
        }
    }
}
