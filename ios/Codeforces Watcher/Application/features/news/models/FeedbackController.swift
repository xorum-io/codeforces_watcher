//
//  FeedbackController.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 5/6/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import StoreKit
import common

class FeedbackController: BaseFeedbackController {
    
    override var currentShowingItem: Int32 {
        get { UserDefaults.standard.object(forKey: Companion().KEY_CURRENT_SHOWING_ITEM) as? Int32 ?? 0 }
        set(value) { UserDefaults.standard.set(value, forKey: Companion().KEY_CURRENT_SHOWING_ITEM) }
    }
    
    override var countScreenOpening: Int32 {
        get { UserDefaults.standard.object(forKey: Companion().KEY_COUNT_SCREEN_OPENING) as? Int32 ?? 0 }
        set(value) { UserDefaults.standard.set(Int(value), forKey: Companion().KEY_COUNT_SCREEN_OPENING) }
    }

    override var startTimeWhenShown: Int64 {
        get { UserDefaults.standard.object(forKey: Companion().KEY_START_TIME_SHOWING) as? Int64 ?? 0 }
        set(value) { UserDefaults.standard.set(value, forKey: Companion().KEY_START_TIME_SHOWING) }
    }

    override var isNeededAtAll: Bool {
        get { UserDefaults.standard.object(forKey: Companion().KEY_IS_NEEDED_AT_ALL) as? Bool ?? true }
        set(value) { UserDefaults.standard.set(value, forKey: Companion().KEY_IS_NEEDED_AT_ALL) }
    }

    override var isLockoutPeriod: Bool {
        get { UserDefaults.standard.bool(forKey: Companion().KEY_IS_LOCKOUT_PERIOD) }
        set(value) { UserDefaults.standard.set(value, forKey: Companion().KEY_IS_LOCKOUT_PERIOD) }
    }
    
    override func buildEnjoyingItem(
        positiveButtonClick: @escaping () -> Void,
        negativeButtonClick: @escaping () -> Void,
        neutralButtonClick: @escaping () -> Void
    ) -> FeedUIModel {
        FeedUIModel(
            textPositiveButton: "yes".localized,
            textNegativeButton: "not_really".localized,
            textTitle: "rate_us_first_title".localized,
            positiveButtonClick: positiveButtonClick,
            negativeButtonClick: negativeButtonClick,
            neutralButtonClick: neutralButtonClick
        )
    }
    
    override func buildEmailItem(
        positiveButtonClick: @escaping () -> Void,
        negativeButtonClick: @escaping () -> Void,
        neutralButtonClick: @escaping () -> Void
    ) -> FeedUIModel {
        FeedUIModel(
            textPositiveButton: "yes".localized,
            textNegativeButton: "no_thanks".localized,
            textTitle: "rate_us_second_title".localized,
            positiveButtonClick: positiveButtonClick,
            negativeButtonClick: negativeButtonClick,
            neutralButtonClick: neutralButtonClick
        )
    }
    
    override func buildRateItem(
        positiveButtonClick: @escaping () -> Void,
        negativeButtonClick: @escaping () -> Void,
        neutralButtonClick: @escaping () -> Void
    ) -> FeedUIModel {
        FeedUIModel(
            textPositiveButton: "yes".localized,
            textNegativeButton: "no_thanks".localized,
            textTitle: "rate_us_third_title".localized,
            positiveButtonClick: positiveButtonClick,
            negativeButtonClick: negativeButtonClick,
            neutralButtonClick: neutralButtonClick
        )
    }

    override func showEmailApp() {
        if let url = URL(string: "mailto:support@xorum.io?subject=Feedback about Codeforces WatchR App".addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!) {
            UIApplication.shared.open(url)
        }
    }

    override func showAppStore() {
        SKStoreReviewController.requestReview()
    }

    override func currentTimeMillis() -> Int64 {
        return Int64(Date().timeIntervalSince1970 * 1000)
    }
}
