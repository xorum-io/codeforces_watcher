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
import FirebaseMessaging

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
        setBackendLink()

        AppStoreKt.databaseController.onAppCreated()
        AppStoreKt.persistenceController.onAppCreated()

        initFirebase()
        initFirebaseController()
        registerForPushNotifications(application)

        initAppStyle()
        initGetLang()
        
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
    
    private func initFirebase() {
        FirebaseApp.configure()
    }
    
    private func initFirebaseController() {
        AppStoreKt.firebaseController = FirebaseController()
    }
    
    private func setBackendLink() {
        #if DEBUG
        HttpClientFactoryKt.backendLink = HttpClientFactoryKt.BACKEND_STAGING_LINK
        #else
        HttpClientFactoryKt.backendLink = HttpClientFactoryKt.BACKEND_PROD_LINK
        #endif
    }

    private func fetchData() {
        store.dispatch(action: FetchOnStartData())
    }
    
    private func initGetLang() {
        AppStoreKt.getLang = { "locale".localized }
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
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        UIApplication.shared.applicationIconBadgeNumber = 0
    }
    
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler( [.alert, .badge, .sound])
    }
    
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        completionHandler()
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate, MessagingDelegate {

    private func registerForPushNotifications(_ application: UIApplication) {
        UNUserNotificationCenter.current().delegate = self

        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { _, _ in })

        application.registerForRemoteNotifications()

        Messaging.messaging().delegate = self
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let fcmToken = fcmToken else { return }
        AppStoreKt.pushToken = fcmToken
        if store.state.users.userAccount != nil {
            store.dispatch(action: NotificationsRequests.AddPushToken())
        }
    }
}
