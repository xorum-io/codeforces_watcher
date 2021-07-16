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

class ContestsFiltersViewController: ClosableViewController, ReKampStoreSubscriber {
    
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
        title = "filters".localized

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
            .init(
                title: "Codeforces",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.codeforces)),
                isOn: filters.contains(.codeforces),
                onSwitchTap: { isOn in self.onSwitchTap(.codeforces, isOn) }
            ),
            .init(
                title: "Codeforces Gym",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.codeforcesGym)),
                isOn: filters.contains(.codeforcesGym),
                onSwitchTap: { isOn in self.onSwitchTap(.codeforcesGym, isOn) }
            ),
            .init(
                title: "AtCoder",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.atcoder)),
                isOn: filters.contains(.atcoder),
                onSwitchTap: { isOn in self.onSwitchTap(.atcoder, isOn) }
            ),
            .init(
                title: "LeetCode",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.leetcode)),
                isOn: filters.contains(.leetcode),
                onSwitchTap: { isOn in self.onSwitchTap(.leetcode, isOn) }
            ),
            .init(
                title: "TopCoder",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.topcoder)),
                isOn: filters.contains(.topcoder),
                onSwitchTap: { isOn in self.onSwitchTap(.topcoder, isOn) }
            ),
            .init(
                title: "CS Academy",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.csAcademy)),
                isOn: filters.contains(.csAcademy),
                onSwitchTap: { isOn in self.onSwitchTap(.csAcademy, isOn) }
            ),
            .init(
                title: "CodeChef",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.codechef)),
                isOn: filters.contains(.codechef),
                onSwitchTap: { isOn in self.onSwitchTap(.codechef, isOn) }
            ),
            .init(
                title: "HackerRank",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.hackerrank)),
                isOn: filters.contains(.hackerrank),
                onSwitchTap: { isOn in self.onSwitchTap(.hackerrank, isOn) }
            ),
            .init(
                title: "HackerEarth",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.hackerearth)),
                isOn: filters.contains(.hackerearth),
                onSwitchTap: { isOn in self.onSwitchTap(.hackerearth, isOn) }
            ),
            .init(
                title: "Kick Start",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.kickStart)),
                isOn: filters.contains(.kickStart),
                onSwitchTap: { isOn in self.onSwitchTap(.kickStart, isOn) }
            ),
            .init(
                title: "Toph",
                image: UIImage(named: Contest.Platform.getImageNameByPlatform(.toph)),
                isOn: filters.contains(.toph),
                onSwitchTap: { isOn in self.onSwitchTap(.toph, isOn) }
            )
        ]

        tableView.reloadData()
    }
    
    private func onSwitchTap (_ platform: Contest.Platform, _ isOn: Bool) {
        store.dispatch(action: ContestsRequests.ChangeFilterCheckStatus(platform: platform, isChecked: isOn))
    }
}
