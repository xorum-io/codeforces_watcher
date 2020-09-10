//
//  StringOccurrence.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 9/10/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//
import Foundation

extension String {

    func firstIndex(_ string: String) -> Index? {
        return range(of: string)?.lowerBound;
    }

    func firstPosition(_ string: String) -> Int? {
        if let index = firstIndex(string) {
            return distance(from: string.startIndex, to: index)
        } else {
            return nil
        }
    }

    func firstOccurrence(string: String) -> NSRange? {
        if let location = firstPosition(string) {
            return NSRange(location: location, length: string.count)
        } else {
            return nil
        }
    }

    func lastIndex(_ string: String) -> Index? {
        return range(of: string, options: NSString.CompareOptions.backwards)?.lowerBound
    }

    func lastPosition(_ string: String) -> Int? {
        if let index = lastIndex(string) {
            return distance(from: string.startIndex, to: index)
        } else {
            return nil
        }
    }

    func lastOccurrence(string: String) -> NSRange? {
        if let location = lastPosition(string) {
            return NSRange(location: location, length: string.count)
        } else {
            return nil
        }
    }
}
