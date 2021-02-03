//
// Created by Yevhenii Kanivets on 11/01/2020.
// Copyright (c) 2020 xorum.io. All rights reserved.
//

import Foundation

public protocol ScopeFunc {
}

extension ScopeFunc {
    
    @inline(__always) public func apply(block: (Self) -> ()) -> Self {
        block(self)
        return self
    }

    @inline(__always) public func run<R>(block: (Self) -> R) -> R {
        return block(self)
    }
}

extension NSObject: ScopeFunc {
}
