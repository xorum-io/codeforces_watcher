//
// Created by Yevhenii Kanivets on 11/01/2020.
// Copyright (c) 2020 xorum.io. All rights reserved.
//

import Foundation

extension TimeInterval {

    var socialDate: String? {
        return DateComponentsFormatter().apply {
            $0.allowedUnits = [.year, .month, .weekday, .day, .hour, .minute, .second]
            $0.unitsStyle = .full
            $0.maximumUnitCount = 1
        }.string(from: self)
    }
}
