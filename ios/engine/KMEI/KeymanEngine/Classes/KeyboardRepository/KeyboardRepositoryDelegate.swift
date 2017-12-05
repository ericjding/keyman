//
//  KeyboardRepositoryDelegate.swift
//  KeymanEngine
//
//  Created by Gabriel Wong on 2017-12-01.
//  Copyright © 2017 SIL International. All rights reserved.
//

import Foundation

public protocol KeyboardRepositoryDelegate: class {
  func keyboardRepositoryDidFetch(_ repository: KeyboardRepository)
  func keyboardRepository(_ repository: KeyboardRepository, didFailFetch error: Error)
}
