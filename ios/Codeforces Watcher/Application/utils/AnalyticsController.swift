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
    
    func logEvent(eventName: String, params: [String: String] = [:]) {
        Analytics.logEvent(eventName, parameters: params)
    }
}

