//
//  AgreementTextView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/9/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class AgreementTextView: UITextView, UITextViewDelegate {
    
    private var agreementText =  "agreement_terms_and_privacy".localized.attributed
    
    override init(frame: CGRect, textContainer: NSTextContainer?) {
        super.init(frame: CGRect.zero, textContainer: nil)
        setupView()
    }
    
    public required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupView()
    }
    
    private func setupView() {
        isEditable = false
        
        addTermsProperty()
        addPrivacyProperty()
        
        font = Font.textSubheadingBig
        textColor = Palette.black
        
        textAlignment = .left
        attributedText = agreementText
    }
    
    private func addTermsProperty() {
        let tag = "terms"
        let link = "https://docs.google.com/document/d/1hvL5NJ-wEJTEj_lurHkGy0kbtBAwd0w0Q1RIb4fzClg/edit?usp=sharing"
        
        let range = agreementText.getRangeAndRemoveTag(tag: tag)
        
        agreementText.run {
            $0.addLink(url: link, range: range)
            $0.addUnderline(range: range)
        }
    }
    
    private func addPrivacyProperty() {
        let tag = "privacy"
        let link = "https://docs.google.com/document/d/17mkrB4fKU9lS8QBbgo0AzdpkfoFdoPftCtql_dD8iZI/edit?usp=sharing"
        
        let range = agreementText.getRangeAndRemoveTag(tag: tag)
        
        agreementText.run {
            $0.addLink(url: link, range: range)
            $0.addUnderline(range: range)
        }
    }
    
    func textView(_ textView: UITextView, shouldInteractWith URL: URL, in characterRange: NSRange, interaction: UITextItemInteraction) -> Bool {
        UIApplication.shared.open(URL)
        return false
    }
}

fileprivate extension NSMutableAttributedString {
    
    func getRangeAndRemoveTag(tag: String) -> NSRange {
        let openTag = "<\(tag)>"
        let closeTag = "</\(tag)>"
        
        guard let rangeOpenTag = string.range(of: openTag) else { fatalError() }
        guard let rangeCloseTag = string.range(of: closeTag) else { fatalError() }
        
        let indexFirstEntry = rangeOpenTag.upperBound.utf16Offset(in: string) - openTag.count
        let indexLastEntry = rangeCloseTag.lowerBound.utf16Offset(in: string) - 1 - openTag.count
        
        deleteCharacters(in: NSRange(range: rangeCloseTag, in: string))
        deleteCharacters(in: NSRange(range: rangeOpenTag, in: string))
        
        return NSRange(location: indexFirstEntry, length: indexLastEntry - indexFirstEntry + 1)
    }
}

fileprivate extension NSRange {
    
    private init(string: String, lowerBound: String.Index, upperBound: String.Index) {
        let utf16 = string.utf16

        let lowerBound = lowerBound.samePosition(in: utf16)
        let location = utf16.distance(from: utf16.startIndex, to: lowerBound!)
        let length = utf16.distance(from: lowerBound!, to: upperBound.samePosition(in: utf16)!)

        self.init(location: location, length: length)
    }
    
    init(range: Range<String.Index>, in string: String) {
        self.init(string: string, lowerBound: range.lowerBound, upperBound: range.upperBound)
    }
}
