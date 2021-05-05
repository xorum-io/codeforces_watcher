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
import PKHUD

class UserViewController: ClosableViewController, ReKampStoreSubscriber {
    
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
    
    private var handle: String
    private var user: User!
    
    private let isUserAccount: Bool
    
    init(_ handle: String, isUserAccount: Bool) {
        self.handle = handle
        self.isUserAccount = isUserAccount
        
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        setupView()
        setupChart()
        store.dispatch(action: UsersRequests.FetchUser(handle: handle))
    }
    
    private func setupView() {
        view.backgroundColor = Palette.white
        
        setupRightBarButton()
        
        buildViewTree()
        setConstraints()
    }
    
    private func setupRightBarButton() {
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            image: UIImage(named: isUserAccount ? "logOutIcon" : "removeIcon"),
            style: .plain,
            target: self,
            action: isUserAccount ? #selector(showLogOutAlert) :  #selector(showDeleteUserAlert)
        )
    }
    
    @objc private func showLogOutAlert() {
        let alertController = UIAlertController(
            title: "log_out".localized,
            message: "log_out_ask".localized,
            preferredStyle: .alert
        )
        
        let okButton = UIAlertAction(title: "OK".localized, style: .cancel) { _ in
            store.dispatch(action: AuthRequests.LogOut())
            self.presentingViewController?.dismiss(animated: true)
        }
        let cancelButton = UIAlertAction(title: "Cancel".localized, style: .destructive)
        
        alertController.addAction(okButton)
        alertController.addAction(cancelButton)
        
        present(alertController, animated: true, completion: nil)
    }
    
    @objc private func showDeleteUserAlert() {
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
    }
    
    private func setupChart() {
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
        
        userImage.run {
            $0.sd_setImage(with: URL(string: user.avatar), placeholderImage: noImage)
            $0.layer.borderColor = getColorByUserRank(user.rank).cgColor
        }

        rankLabel.attributedText = user.rankText
        handleLabel.attributedText = user.handleText
        ratingLabel.attributedText = user.ratingText
        contributionLabel.attributedText = user.contributionText
        
        displayChart()
    }
    
    private func displayChart() {
        guard !user.ratingChanges.isEmpty else { return }
        
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
    
    func onNewState(state: Any) {
        let state = state as! UsersState
        switch(state.status) {
        case .pending:
            showLoading()
        case .idle:
            hideLoading()
        case .done:
            hideLoading()
            dismiss(animated: true)
        default:
            break
        }
        
        guard let user = state.currentUser else { return }
        self.user = user
        
        bind()
    }
    
    private func showLoading() {
        PKHUD.sharedHUD.userInteractionOnUnderlyingViewsEnabled = false
        HUD.show(.progress, onView: UIApplication.shared.windows.last)
    }
    
    private func hideLoading() {
        HUD.hide(afterDelay: 0)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        store.subscribe(subscriber: self) { subscription in
            subscription.skipRepeats { oldState, newState in
                return KotlinBoolean(bool: oldState.users == newState.users)
            }.select { state in
                return state.users
            }
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        store.unsubscribe(subscriber: self)
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
            
            attributedText.colored(with: colorOfContribution, range: contributionRange)
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
                attributedText.colored(with: colorCurrent, range: rangeCurrent)
            }

            let searchMaximum = "\(maxRating)"
            if let rangeMaximum = text.lastOccurrence(string: searchMaximum) {
                attributedText.colored(with: colorMaximum, range: rangeMaximum)
            }
        }
        
        return attributedText
    }
}
