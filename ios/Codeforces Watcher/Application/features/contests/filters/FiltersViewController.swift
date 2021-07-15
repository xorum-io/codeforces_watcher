//
//  FilterViewController.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/28/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit
import common

class FiltersViewController: ClosableViewController, ReKampStoreSubscriber {
    
    private let tableView = UITableView()
    private let tableAdapter = FiltersTableViewAdapter()
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        store.subscribe(subscriber: self) { subscription in
            subscription.skipRepeats() { oldState, newState in
                return KotlinBoolean(bool: oldState.contests == newState.contests)
            }.select { state in
                return state.contests
            }
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        store.unsubscribe(subscriber: self)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        setupView()
        setupTableView()
    }

    private func setupView() {
        view.backgroundColor = Palette.white
        title = "Filter".localized

        buildViewTree()
        setConstraints()
    }

    private func setupTableView() {
        tableView.run {
            $0.delegate = tableAdapter
            $0.dataSource = tableAdapter
            $0.separatorStyle = .none
            $0.rowHeight = 58
        }

        tableView.registerForReuse(cellType: FilterTableViewCell.self)
    }

    private func buildViewTree() {
        view.addSubview(tableView)
    }

    private func setConstraints() {
        tableView.edgesToSuperview()
    }
    
    func onNewState(state: Any) {
        let state = state as! ContestsState
        
        let filters = state.filters

        tableAdapter.filterItems = [
            FilterItem(title: "Codeforces", platform: .codeforces, isOn: filters.contains(.codeforces)),
            FilterItem(title: "Codeforces Gym", platform: .codeforcesGym, isOn: filters.contains(.codeforcesGym)),
            FilterItem(title: "AtCoder", platform: .atcoder, isOn: filters.contains(.atcoder)),
            FilterItem(title: "LeetCode", platform: .leetcode, isOn: filters.contains(.leetcode)),
            FilterItem(title: "TopCoder", platform: .topcoder, isOn: filters.contains(.topcoder)),
            FilterItem(title: "CS Academy", platform: .csAcademy, isOn: filters.contains(.csAcademy)),
            FilterItem(title: "CodeChef", platform: .codechef, isOn: filters.contains(.codechef)),
            FilterItem(title: "HackerRank", platform: .hackerrank, isOn: filters.contains(.hackerrank)),
            FilterItem(title: "HackerEarth", platform: .hackerearth, isOn: filters.contains(.hackerearth)),
            FilterItem(title: "Kick Start", platform: .kickStart, isOn: filters.contains(.kickStart)),
            FilterItem(title: "Toph", platform: .toph, isOn: filters.contains(.toph))
        ]

        tableView.reloadData()
    }
}
