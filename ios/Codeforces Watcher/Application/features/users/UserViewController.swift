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

class UserViewController: UIViewControllerWithCross {
    
    private let userImage = CircleImageView()
    private let rankLabel = BodyLabel().apply {
        $0.font = Font.textBody
    }
    private let handleLabel = BodyLabel().apply {
        $0.font = Font.textHeading
    }
    private let ratingLabel = BodyLabel().apply {
        $0.textColor = Palette.darkGray
        $0.font = Font.textSubheadingBig
    }
    private let contributionLabel = BodyLabel().apply {
        $0.textColor = Palette.darkGray
        $0.font = Font.textSubheadingBig
    }
    private let ratingChangesLabel = HeadingLabel().apply {
        $0.text = "Rating Changes".localized
        $0.textColor = Palette.black
        $0.font = Font.textBody
    }
    private let lineChartView = LineChartView()
    
    private let user: User
    
    init(_ user: User) {
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
        title = "\(user.firstName ?? "") \(user.lastName ?? "None".localized)"
        view.backgroundColor = Palette.white
        
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            image: UIImage(named: "removeIcon"),
            style: .plain, 
            target: self, 
            action: #selector(removeTapped)
        )
        
        buildViewTree()
        setConstraints()
        bind()
    }
    
    @objc func removeTapped() {
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
        [userImage, handleLabel, rankLabel, ratingLabel, contributionLabel, ratingChangesLabel, lineChartView].forEach(view.addSubview)
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
        
        ratingLabel.run {
            $0.topToBottom(of: handleLabel, offset: 11)
            $0.leading(to: rankLabel)
        }
        
        contributionLabel.run {
            $0.topToBottom(of: ratingLabel, offset: 4)
            $0.leading(to: rankLabel)
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
        let avatar = LinkValidatorKt.avatar(avatarLink: user.avatar)
        userImage.sd_setImage(with: URL(string: avatar), placeholderImage: noImage)
        
        let none = "None".localized
        
        rankLabel.attributedText = colorTextByUserRank(text: user.rank?.capitalized ?? none, rank: user.rank)
        handleLabel.attributedText = colorTextByUserRank(text: user.handle, rank: user.rank)
        ratingLabel.attributedText = ratingViewByUserRank(text: "Rating".localizedFormat(args: user.rating ?? none, user.maxRating ?? none), currentRank: user.rank, maxRank: user.maxRank)
        contributionLabel.attributedText = contributionView(text: "Contribution".localizedFormat(args: user.contribution ?? none))
    }
}
