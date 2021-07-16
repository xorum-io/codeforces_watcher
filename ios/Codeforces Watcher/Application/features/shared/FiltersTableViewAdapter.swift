//
//  FiltersTableViewController.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/28/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import UIKit

class FiltersTableViewAdapter: NSObject, UITableViewDelegate, UITableViewDataSource {
    
    var filterItems: [FilterTableViewCell.UIModel] = []

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return filterItems.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView.dequeueReusableCell(cellType: FilterTableViewCell.self).apply {
            $0.bind(filterItems[indexPath.row])
        }
    }
}
