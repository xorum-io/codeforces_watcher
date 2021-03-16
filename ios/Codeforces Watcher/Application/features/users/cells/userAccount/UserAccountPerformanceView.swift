//
//  UserAccountPerformanceView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 3/10/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit
import common
import Charts

class UserAccountPerformanceView: UIView {
    
    private let contentView = UIView()

    private let ratingView = UIView()
    private let ratingIcon = UIImageView(image: UIImage(named: "ratingIcon"))
    private let ratingLabel = UILabel().apply {
        $0.font = Font.textSubheadingBig
        $0.textColor = Palette.darkGray
    }
    
    private let maxRatingView = UIView()
    private let maxRatingIcon = UIImageView(image: UIImage(named: "maxRatingIcon"))
    private let maxRatingLabel = UILabel().apply {
        $0.font = Font.textSubheadingBig
        $0.textColor = Palette.darkGray
    }
    
    private let contributionView = UIView()
    private let contributionIcon = UIImageView(image: UIImage(named: "starIcon"))
    private let contributionLabel = UILabel().apply {
        $0.font = Font.textSubheadingBig
        $0.textColor = Palette.darkGray
    }
    
    private let lineChartView = LineChartView().apply {
        $0.isUserInteractionEnabled = false
    }
    
    struct UIModel {
        let rating: Int?
        let maxRating: Int?
        let contribution: Int?
        let ratingChanges: [RatingChange]
        let rank: String?
        let maxRank: String?
    }
    
    private var uiModel: UserAccountPerformanceView.UIModel?
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    private func setupView() {
        setupLineChart()
        
        buildViewTree()
        setConstraints()
    }
    
    private func setupLineChart() {
        lineChartView.run {
            $0.scaleXEnabled = false
            $0.scaleYEnabled = false
            $0.dragEnabled = false
            $0.legend.enabled = false
            
            $0.leftAxis.drawLabelsEnabled = false
            $0.rightAxis.run {
                $0.drawLabelsEnabled = false
                $0.drawAxisLineEnabled = false
            }
            $0.xAxis.run {
                $0.drawLabelsEnabled = false
                $0.drawAxisLineEnabled = false
                $0.labelCount = 5
            }
            
            $0.minOffset = 3
        }
    }
    
    private func buildViewTree() {
        addSubview(contentView)
        [ratingView, maxRatingView, contributionView, lineChartView].forEach(contentView.addSubview)
        [ratingIcon, ratingLabel].forEach(ratingView.addSubview)
        [maxRatingIcon, maxRatingLabel].forEach(maxRatingView.addSubview)
        [contributionIcon, contributionLabel].forEach(contributionView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview(insets: UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8))
        
        ratingView.edgesToSuperview(excluding: .bottom)
        
        ratingIcon.run {
            $0.width(15)
            $0.height(15)
            $0.edgesToSuperview(excluding: .trailing)
        }
        
        ratingLabel.run {
            $0.verticalToSuperview()
            $0.leadingToTrailing(of: ratingIcon, offset: 4)
        }
        
        maxRatingView.run {
            $0.topToBottom(of: ratingView, offset: 8)
            $0.leadingToSuperview(offset: 2.5)
            $0.trailingToSuperview()
        }
        
        maxRatingIcon.run {
            $0.width(11)
            $0.height(11)
            $0.edgesToSuperview(excluding: .trailing)
        }
        
        maxRatingLabel.run {
            $0.verticalToSuperview()
            $0.leadingToTrailing(of: ratingIcon, offset: 3.5)
        }
        
        contributionView.run {
            $0.topToBottom(of: maxRatingView, offset: 5)
            $0.horizontalToSuperview()
        }
        
        contributionIcon.run {
            $0.width(16)
            $0.height(16)
            $0.edgesToSuperview(excluding: .trailing)
        }
        
        contributionLabel.run {
            $0.topToSuperview(offset: 3)
            $0.bottomToSuperview()
            $0.leadingToTrailing(of: ratingIcon, offset: 4)
        }
        
        lineChartView.run {
            $0.topToBottom(of: contributionIcon, offset: 4)
            $0.edgesToSuperview(excluding: .top)
        }
    }
    
    func bind(_ uiModel: UserAccountPerformanceView.UIModel) {
        self.uiModel = uiModel
        
        ratingLabel.attributedText = uiModel.ratingText
        maxRatingLabel.attributedText = uiModel.maxRatingText
        contributionLabel.attributedText = uiModel.contributionText
        
        setChartData()
    }
    
    private func setChartData() {
        guard let ratingChanges = uiModel?.ratingChanges else { return }
        
        let dataEntries = ratingChanges.map {
            ChartDataEntry(
                x: Double($0.ratingUpdateTimeSeconds),
                y: Double($0.newRating),
                data: $0.toChartItem()
            )
        }
        
        let dataSet = LineChartDataSet(entries: dataEntries).apply {
            $0.lineWidth = 1.5
            $0.circleRadius = 2.5
            $0.circleHoleRadius = 1.5
            $0.circleHoleColor = Palette.white
        }
        
        let data = LineChartData(dataSet: dataSet).apply {
            $0.setDrawValues(false)
        }
        
        lineChartView.data = data
    }
}

fileprivate extension UserAccountPerformanceView.UIModel {
    
    private var none: NSAttributedString {
        return NSAttributedString(string: "None".localized)
    }
    
    var ratingText: NSAttributedString {
        if let rating = rating {
            return colorRating(text: "rating".localizedFormat(args: "\(rating)"), rating: rating, rank: rank)
        } else {
            return none
        }
    }
    
    var maxRatingText: NSAttributedString {
        if let rating = maxRating {
            return colorRating(text: "max_rating".localizedFormat(args: "\(rating)"), rating: maxRating, rank: maxRank)
        } else {
            return none
        }
    }
    
    private func colorRating(text: String, rating: Int?, rank: String?) -> NSAttributedString {
        let attributedText = NSMutableAttributedString(string: text)

        let color = getColorByUserRank(rank)
        
        if let rating = rating {
            let tag = "\(rating)"
            if let range = text.firstOccurrence(string: tag) {
                attributedText.colored(with: color, range: range)
            }
        }
        
        return attributedText
    }
    
    var contributionText: NSAttributedString {
        if let contribution = contribution {
            let contributionSubstring = (contribution <= 0 ? "\(contribution)" : "+\(contribution)")
            return colorContribution(text: "Contribution".localizedFormat(args: contributionSubstring), contributionSubstring)
        } else {
            return none
        }
    }
    
    private func colorContribution(text: String, _ contributionSubstring: String) -> NSAttributedString {
        let attributedText = NSMutableAttributedString(string: text)
       
        if let range = text.firstOccurrence(string: contributionSubstring), let contribution = contribution {
            let colorOfContribution = (contribution >= 0 ? Palette.brightGreen : Palette.red)
            
            attributedText.colored(with: colorOfContribution, range: range)
        }

        return attributedText
    }
}
