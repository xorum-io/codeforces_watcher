//
//  ProblemsTableViewAdapter.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/17/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit
import common

class ProblemsTableViewAdapter: NSObject, UITableViewDelegate, UITableViewDataSource {
    
    var problems: [Problem] = []

    var onProblemClick: ((String, String) -> ())?

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    var noProblemsExplanation: String = ""

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (problems.isEmpty) {
            return 1
        }
        
        return problems.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (problems.isEmpty) {
            return tableView.dequeueReusableCell(cellType: NoItemsTableViewCell.self).apply {
                $0.bind(imageName: "noItemsImage", explanation: noProblemsExplanation)
            }
        }
        
        return tableView.dequeueReusableCell(cellType: ProblemTableViewCell.self).apply {
            $0.bind(problems[indexPath.row])
        }
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if (problems.isEmpty) {
            return
        }

        let problem = problems[indexPath.row]
        onProblemClick?(problem.link, problem.fullName)
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if (problems.isEmpty) {
            return tableView.frame.height - 2 * tableView.tableHeaderView!.frame.height
        } else {
            return 63
        }
    }
}
