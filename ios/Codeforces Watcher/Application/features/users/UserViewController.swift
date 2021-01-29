//
//  UserViewController.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/16/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import UIKit
import common
import Charts

class UserViewController: ClosableViewController {
    
    private let userImage = CircleImageView()
    private let rankLabel = BodyLabel()
    private let handleLabel = HeadingLabel()
    
    private let ratingIcon = UIImageView(image: UIImage(named: "ratingIcon"))
    private let ratingLabel = SubheadingBigLabel().apply {
        $0.textColor = Palette.darkGray
    }
    private let starIcon = UIImageView(image: UIImage(named: "starIcon"))
    private let contributionLabel = SubheadingBigLabel().apply {
        $0.textColor = Palette.darkGray
    }
    private let ratingChangesLabel = BodyLabel().apply {
        $0.text = "Rating Changes".localized
        $0.textColor = Palette.black
    }
    private let lineChartView = LineChartView()
    
    private let user: User
    
    init(_ userId: Int) {
        guard let user = store.state.users.users.first(where: {$0.id == userId}) else { fatalError() }
        self.user = user
        
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        setupView()
        setupChart()
    }
    
    private func setupView() {
        view.backgroundColor = Palette.white
        
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            image: UIImage(named: "removeIcon"),
            style: .plain,
            target: self,
            action: #selector(showDeleteUserAlert)
        )
        
        buildViewTree()
        setConstraints()
        bind()
    }
    
    @objc func showDeleteUserAlert() {
        let alertController = UIAlertController(
            title: "Delete user".localized,
            message: "delete_user_explanation".localizedFormat(args: user.handle),
            preferredStyle: .alert
        )
        
        let okButton = UIAlertAction(title: "OK".localized, style: .cancel) { _ in
            self.removeTapped()
        }
        let cancelButton = UIAlertAction(title: "Cancel".localized, style: .destructive)
        
        alertController.addAction(okButton)
        alertController.addAction(cancelButton)
        
        present(alertController, animated: true, completion: nil)
    }
    
    private func removeTapped() {
        store.dispatch(action: UsersRequests.DeleteUser(user: user))
        dismiss(animated: true)
    }
    
    private func setupChart() {
        guard !user.ratingChanges.isEmpty else { return }
        
        let markerView = ChartMarker().apply {
            $0.chartView = lineChartView
        }
        
        lineChartView.run {
            $0.rightAxis.enabled = false
            $0.legend.enabled = false
            $0.xAxis.run {
                $0.labelPosition = .bottom
                $0.valueFormatter = xAxisFormatter()
                $0.labelCount = 3
            }
            $0.drawMarkers = true
            $0.marker = markerView
        }
        
        let dataEntries = user.ratingChanges.map {
            ChartDataEntry(
                x: Double($0.ratingUpdateTimeSeconds),
                y: Double($0.newRating),
                data: $0.toChartItem()
            )
        }
        
        let dataSet = LineChartDataSet(entries: dataEntries).apply {
            $0.lineWidth = 1.3
            $0.circleRadius = 3.5
            $0.circleHoleRadius = 2
            $0.circleHoleColor = Palette.white
        }
        
        let data = LineChartData(dataSet: dataSet).apply {
            $0.setDrawValues(false)
        }
        
        lineChartView.data = data
    }
    
    private func buildViewTree() {
        [userImage, handleLabel, rankLabel, ratingIcon, ratingLabel, starIcon, contributionLabel, ratingChangesLabel, lineChartView].forEach(view.addSubview)
    }
    
    private func setConstraints() {
        userImage.run {
            $0.topToSuperview(offset: 16)
            $0.leadingToSuperview(offset: 16)
            $0.width(80)
            $0.height(80)
        }
        
        rankLabel.run {
            $0.leadingToTrailing(of: userImage, offset: 16)
            $0.top(to: userImage, offset: 0)
        }
        
        handleLabel.run {
            $0.topToBottom(of: rankLabel, offset: 0)
            $0.leading(to: rankLabel)
        }
        
        ratingIcon.run {
            $0.width(16)
            $0.height(16)
            $0.bottomToTop(of: starIcon)
            $0.leading(to: rankLabel)
        }
  
        ratingLabel.run {
            $0.centerY(to: ratingIcon)
            $0.leadingToTrailing(of: ratingIcon, offset: 4)
        }
        
        starIcon.run {
            $0.width(16)
            $0.height(16)
            $0.bottom(to: userImage)
            $0.leading(to: rankLabel)
        }
        
        contributionLabel.run {
            $0.centerY(to: starIcon, offset: 1)
            $0.leadingToTrailing(of: starIcon, offset: 4)
        }
        
        ratingChangesLabel.run {
            $0.leading(to: userImage)
            $0.topToBottom(of: userImage, offset: 16)
        }
        
        lineChartView.run {
            $0.leadingToSuperview(offset: 16)
            $0.trailingToSuperview(offset: 16)
            $0.topToBottom(of: ratingChangesLabel, offset: 16)
            $0.bottomToSuperview(offset: -16)
        }
    }
    
    private func bind() {
        title = user.titleText
        
        let avatar = LinkValidatorKt.avatar(avatarLink: user.avatar)
        userImage.run {
            $0.sd_setImage(with: URL(string: avatar), placeholderImage: noImage)
            $0.layer.borderColor = getColorByUserRank(user.rank).cgColor
        }

        rankLabel.attributedText = user.rankText
        handleLabel.attributedText = user.handleText
        ratingLabel.attributedText = user.ratingText
        contributionLabel.attributedText = user.contributionText
    }
}

fileprivate extension User {
    
    private var none: NSAttributedString {
        return NSAttributedString(string: "None".localized)
    }
    
    var rankText: NSAttributedString {
        if let rank = rank {
            return colorTextByUserRank(text: rank.capitalized, rank: rank)
        } else {
            return none
        }
    }
    
    var titleText: String {
        if let firstName = firstName, let lastName = lastName {
            return "\(firstName) \(lastName)"
        } else if let firstName = firstName {
            return firstName
        } else if let lastName = lastName {
            return lastName
        } else {
            return handle
        }
    }
    
    var handleText: NSAttributedString {
        return colorTextByUserRank(text: handle, rank: rank)
    }
    
    var ratingText: NSAttributedString {
        if let rating = rating, let maxRating = maxRating {
            return colorRating(text: "Rating".localizedFormat(args: rating, maxRating))
        } else {
            return none
        }
    }
    
    var contributionText: NSAttributedString {
        if let contribution = contribution {
            let contributionSubstring = (contribution.intValue <= 0 ? contribution.stringValue : "+\(contribution)")
            return colorContribution(text: "Contribution".localizedFormat(args: contributionSubstring), contributionSubstring)
        } else {
            return none
        }
    }
    
    private func colorContribution(text: String, _ contributionSubstring: String) -> NSAttributedString {
        let attributedText = NSMutableAttributedString(string: text)
       
        if let contributionRange = text.firstOccurrence(string: contributionSubstring), let contribution = contribution {
            let colorOfContribution = (contribution.intValue >= 0 ? Palette.brightGreen : Palette.red)
            
            attributedText.colorSubstring(color: colorOfContribution, range: contributionRange)
        }

        return attributedText
    }
    
    private func colorRating(text: String) -> NSAttributedString {
        let attributedText = NSMutableAttributedString(string: text)

        let colorCurrent = getColorByUserRank(rank)
        let colorMaximum = getColorByUserRank(maxRank)
        
        if let rating = rating, let maxRating = maxRating {
            let searchCurrent = "\(rating)"
            if let rangeCurrent = text.firstOccurrence(string: searchCurrent) {
                attributedText.colorSubstring(color: colorCurrent, range: rangeCurrent)
            }

            let searchMaximum = "\(maxRating)"
            if let rangeMaximum = text.lastOccurrence(string: searchMaximum) {
                attributedText.colorSubstring(color: colorMaximum, range: rangeMaximum)
            }
        }
        
        return attributedText
    }
}
