//
//  Constants.swift
//  KeymanEngine
//
//  Created by Gabriel Wong on 2017-10-20.
//  Copyright © 2017 SIL International. All rights reserved.
//

public enum Key {
  public static let keyboardId = "kbId"
  public static let languageId = "langId"

  public static let fontFamily = "family"
  public static let fontSource = "source"
  public static let fontFiles = "files"
  // Font filename is deprecated
  public static let fontFilename = "filename"
  public static let keyboardInfo = "keyboardInfo"

  /// Array of user keyboards info list in UserDefaults
  static let userKeyboardsList = "UserKeyboardsList"

  /// Currently/last selected keyboard info in UserDefaults
  static let userCurrentKeyboard = "UserCurrentKeyboard"

  // Internal user defaults keys
  static let engineVersion = "KeymanEngineVersion"
  static let keyboardPickerDisplayed = "KeyboardPickerDisplayed"
  static let synchronizeSWKeyboard = "KeymanSynchronizeSWKeyboard"

  // JSON keys for language REST calls
  static let options = "options"
  static let language = "language"

  // TODO: Check if it matches with the key in Keyman Cloud API
  static let keyboardCopyright = "copyright"
  static let languages = "languages"

  // Other keys
  static let update = "update"
}

public enum Defaults {
  private static let font = Font(family: "LatinWeb", source: ["DejaVuSans.ttf"], size: nil)
  public static let keyboard = InstallableKeyboard(id: "european2",
                                                   name: "EuroLatin2 Keyboard",
                                                   languageID: "eng",
                                                   languageName: "English",
                                                   version: "1.6",
                                                   isRTL: false,
                                                   font: font,
                                                   oskFont: nil,
                                                   isCustom: false)
}

public enum Resources {
  /// Keyman Web resources
  public static let bundle: Bundle = {
    let frameworkBundle =  Bundle(identifier: "org.sil.Keyman.ios.Engine")!
    return Bundle(path: frameworkBundle.path(forResource: "Keyman", ofType: "bundle")!)!
  }()

  public static let oskFontFilename = "keymanweb-osk.ttf"
  static let kmwFileName = "keyboard.html"
}

public enum Util {
  /// Is the process of a custom keyboard extension.
  public static let isSystemKeyboard: Bool = {
    let infoDict = Bundle.main.infoDictionary
    let extensionInfo = infoDict?["NSExtension"] as? [AnyHashable: Any]
    let extensionID = extensionInfo?["NSExtensionPointIdentifier"] as? String
    return extensionID == "com.apple.keyboard-service"
  }()

  /// The version of the Keyman SDK
  public static let sdkVersion: String = {
    let url = Resources.bundle.url(forResource: "KeymanEngine-Info", withExtension: "plist")!
    let info = NSDictionary(contentsOf: url)!
    return info["CFBundleVersion"] as! String
  }()
}

public enum FileExtensions {
  public static let javaScript = "js"
  public static let trueTypeFont = "ttf"
  public static let openTypeFont = "otf"
  public static let configurationProfile = "mobileconfig"
}
