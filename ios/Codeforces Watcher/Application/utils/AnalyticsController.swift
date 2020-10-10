//
//  AnalyticsController.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 07.09.2020.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import common
import FirebaseAnalytics
import FirebaseCrashlytics

class AnalyticsController: IAnalyticsController {
    
    func logError(message: String) {
        Crashlytics.crashlytics().record(error: CrashlyticsError(message: message))
    }
    
    func logFetchNews() {
        Analytics.logEvent("news_fetch", parameters: [:])
    }
    
    func logFetchNewsFailure() {
        Analytics.logEvent("news_fetch_failure", parameters: [:])
    }
    
    func logFetchNewsSuccess() {
        Analytics.logEvent("news_fetch_success", parameters: [:])
    }
    
    func logActionOpened() {
        Analytics.logEvent("action_opened", parameters: [:])
    }
    
    func logAddContestToCalendarEvent(contestName: String, platform: Platform) {
        Analytics.logEvent("add_contest_to_google_calendar", parameters: ["contest_platform": platform, "contest_name": contestName])
    }
    
    func logAppShared() {
        Analytics.logEvent("actions_app_shared", parameters: [:])
    }
    
    func logPinnedPostClosed() {
        Analytics.logEvent("actions_pinned_post_closed", parameters: [:])
    }
    
    func logPinnedPostOpened() {
        Analytics.logEvent("actions_pinned_post_opened", parameters: [:])
    }
    
    func logProblemOpened() {
        Analytics.logEvent("problem_opened", parameters: [:])
    }
    
    func logRefreshingData(refreshScreen: RefreshScreen) {
        var eventName = ""
        switch(refreshScreen) {
        case .news:
            eventName = "actions_list_refresh"
        case .contests:
            eventName = "contests_list_refresh"
        case .problems:
            eventName = "problems_list_refresh"
        case .users:
            eventName = "users_list_refresh"
        default:
            break
        }
        Analytics.logEvent(eventName, parameters: [:])
    }
    
    func logShareApp() {
        Analytics.logEvent("actions_share_app", parameters: [:])
    }
    
    func logShareComment() {
        Analytics.logEvent("action_share_comment", parameters: [:])
    }
    
    func logShareProblem() {
        Analytics.logEvent("problem_shared", parameters: [:])
    }
    
    func logUserAdded() {
        Analytics.logEvent("user_added", parameters: [:])
    }
    
    func logContestOpened() {
        Analytics.logEvent("contest_opened", parameters: [:])
    }
    
    func logContestShared() {
        Analytics.logEvent("contest_shared", parameters: [:])
    }
}
