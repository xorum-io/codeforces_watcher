//
//  IOSToastHandler.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 4/2/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import common
import Toast_Swift

class IOSToastHandler: ToastHandler {
    
    func handle(message: Message) {
        var text: String?

        switch (message) {
        case _ as Message.NoConnection:
            text = "check_connection".localized
        case _ as Message.UserAlreadyAdded:
            text = "User already added".localized
        case _ as Message.FailedToFetchUser:
            text = "failed_to_fetch_users".localized
        case _ as Message.None:
            text = nil
        case let message as Message.Custom:
            text = message.message
        default:
            break
        }

        if let text = text {
            UIApplication.shared.windows.last?.makeToast(text)
        }
    }
}
