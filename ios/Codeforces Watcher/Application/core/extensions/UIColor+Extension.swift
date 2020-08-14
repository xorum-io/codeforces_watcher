//
// Created by Yevhenii Kanivets on 11/01/2020.
// Copyright (c) 2020 xorum.io. All rights reserved.
//

import Foundation
import UIKit

extension UIColor {

    convenience init(red: Int, green: Int, blue: Int) {
        assert(red >= 0 && red <= 255, "Invalid red component")
        assert(green >= 0 && green <= 255, "Invalid green component")
        assert(blue >= 0 && blue <= 255, "Invalid blue component")

        self.init(red: CGFloat(red) / 255.0, green: CGFloat(green) / 255.0, blue: CGFloat(blue) / 255.0, alpha: 1.0)
    }

    convenience init(rgb: Int) {
        self.init(
            red: (rgb >> 16) & 0xFF,
            green: (rgb >> 8) & 0xFF,
            blue: rgb & 0xFF
        )
    }
}

func getColorByUserRank(rank: String?) -> UIColor {
    var color = UIColor()
    
    switch (rank) {
    case nil:
        color = Palette.black
    case "newbie", "новичок":
        color = Palette.gray
    case "pupil", "ученик":
        color = Palette.green
    case "specialist", "специалист":
        color = Palette.blueGreen
    case "expert", "эксперт":
        color = Palette.blue
    case "candidate master", "кандидат в мастера":
        color = Palette.purple
    case "master", "мастер", "international master", "международный мастер":
        color = Palette.orange
    case "grandmaster", "international grandmaster", "legendary grandmaster",
         "гроссмейстер", "международный гроссмейстер", "легендарный гроссмейстер":
        color = Palette.red
    default:
        color = Palette.gray
    }
    
    return color
}

func colorTextByUserRank(text: String, rank: String?) -> NSMutableAttributedString {
    let color = getColorByUserRank(rank: rank)

    let attributedText = NSMutableAttributedString(string: text).apply {
        $0.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSRange(location: 0, length: text.count))
    }

    if ["legendary grandmaster", "легендарный гроссмейстер"].contains(rank) {
        attributedText.addAttribute(NSAttributedString.Key.foregroundColor, value: Palette.black, range: NSRange(location: 0, length: 1))
    }

    return attributedText
}

func ratingViewByUserRank(text: String, currentRank: String?, maxRank: String?) -> NSMutableAttributedString {
    let attributedText = NSMutableAttributedString(string: text)
    
    let colorByCurrentUserRank = getColorByUserRank(rank: currentRank)
    let colorByMaximumUserRank = getColorByUserRank(rank: maxRank)

    enum paintColor {
        case withoutColor, currentColor, maximumColor
    }
    
    var colorStatus = paintColor.withoutColor
    
    for (index, character) in text.enumerated() {
        if (character >= "0" && character <= "9") || character == "-" {
            colorStatus = (colorStatus == .withoutColor ? .currentColor : colorStatus)
            if (colorStatus == .currentColor) {
                attributedText.addAttribute(NSAttributedString.Key.foregroundColor, value: colorByCurrentUserRank, range: NSRange(location: index, length: 1))
            }
            if (colorStatus == .maximumColor) {
                attributedText.addAttribute(NSAttributedString.Key.foregroundColor, value: colorByMaximumUserRank, range: NSRange(location: index, length: 1))
            }
        } else {
            colorStatus = (colorStatus == .currentColor ? .maximumColor : colorStatus)
        }
    }
    
    return attributedText
}

func contributionView(text: String) -> NSAttributedString {
    var colorOfContribution = UIColor()
    
    let positionOfContribution = 14
    
    let indexOfText = text.index(text.startIndex, offsetBy: positionOfContribution)
    
    var textAfterUpdate: String
    
    if text[indexOfText] == "-" {
        textAfterUpdate = text
        
        colorOfContribution = Palette.red
    } else {
        if text[indexOfText] == "0" {
            textAfterUpdate = text
            
            colorOfContribution = Palette.gray
        } else {
            let prefixText = text[..<indexOfText]
            let suffixText = text[indexOfText...]
            textAfterUpdate = "\(prefixText)+\(suffixText)"
            
            colorOfContribution = Palette.brightGreen
        }
    }
    
    let attributedText = NSMutableAttributedString(string: textAfterUpdate).apply {
        $0.addAttribute(NSAttributedString.Key.foregroundColor, value: colorOfContribution, range: NSRange(location: positionOfContribution, length: textAfterUpdate.count - positionOfContribution))
    }

    return attributedText
}
