//
//  AppDelegate.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 12/30/19.
//  Copyright © 2019 xorum.io. All rights reserved.
//

import UIKit
import Firebase
import common

let store = AppStoreKt.store

let feedbackController = FeedbackController()
let analyticsControler = AppStoreKt.analyticsController

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    let rootViewController = MainViewController()

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        initDatabase()
        initSettings()
        initToastHandler()
        initAnalyticsController()

        AppStoreKt.databaseController.onAppCreated()
        AppStoreKt.persistenceController.onAppCreated()

        FirebaseApp.configure()

        initAppStyle()
        fetchData()

        return true
    }

    private func initDatabase() {
        DatabaseKt.doInitDatabase()
    }

    private func initSettings() {
        SettingsKt.settings = Prefs()
    }

    private func initToastHandler() {
        ToastMiddlewareKt.toastHandlers.add(IOSToastHandler())
    }
    
    private func initAnalyticsController() {
        AppStoreKt.analyticsController = AnalyticsController()
    }

    private func fetchData() {
        store.dispatch(action: NewsRequests.FetchNews(isInitializedByUser: false, language: "locale".localized))
        store.dispatch(action: ContestsRequests.FetchContests(isInitiatedByUser: false, language: "locale".localized))
        store.dispatch(action: UsersRequests.FetchUsers(source: Source.background, language: "locale".localized))
        store.dispatch(action: ProblemsRequests.FetchProblems(isInitializedByUser: false))
    }

    private func initAppStyle() {
        UINavigationBar.appearance().run {
            $0.isTranslucent = false
            $0.barTintColor = Palette.colorPrimary
            $0.tintColor = Palette.white
            $0.titleTextAttributes = [
                NSAttributedString.Key.foregroundColor: Palette.white,
                NSAttributedString.Key.font: Font.textPageTitle
            ]
        }

        UITabBar.appearance().run {
            $0.isTranslucent = false
            $0.itemPositioning = .centered
        }

        window = UIWindow(frame: UIScreen.main.bounds)
        window!.rootViewController = rootViewController
        window!.makeKeyAndVisible()

        if #available(iOS 13.0, *) {
            window?.overrideUserInterfaceStyle = .light
        }
    }
}
