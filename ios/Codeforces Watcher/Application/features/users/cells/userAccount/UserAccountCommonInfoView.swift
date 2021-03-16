//
//  UserAccountCommonInfoView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 3/10/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit
import common

class UserAccountCommonInfoView: UIView {
    
    private let contentView = UIView()

    private let avatarImage = CircleImageView()
    private let rankLabel = UILabel().apply {
        $0.textColor = Palette.colorPrimary
        $0.font = Font.textSubheadingBig
    }
    private let handleLabel = UILabel().apply {
        $0.textColor = Palette.colorPrimary
        $0.font = Font.textBody
    }
    private let nameLabel = UILabel().apply {
        $0.font = Font.textSubheadingBig
    }
    private let lastUpdateLabel = UILabel().apply {
        $0.textColor = Palette.gray
        $0.font = Font.textSubheading
    }
    
    struct UIModel {
        let avatar: String
        let rank: String?
        let handle: String
        let firstName: String?
        let lastName: String?
        let lastUpdate: Int64?
    }
    
    private var uiModel: UserAccountCommonInfoView.UIModel?
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    private func setupView() {
        clipsToBounds = false
        buildViewTree()
        setConstraints()
    }

    private func buildViewTree() {
        addSubview(contentView)
        [avatarImage, rankLabel, handleLabel, nameLabel, lastUpdateLabel].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        avatarImage.run {
            $0.width(48)
            $0.height(48)
            $0.topToSuperview(offset: 16)
            $0.centerXToSuperview()
        }
        
        rankLabel.run {
            $0.topToBottom(of: avatarImage, offset: 8)
            $0.centerXToSuperview()
        }
        
        handleLabel.run {
            $0.topToBottom(of: rankLabel, offset: 4)
            $0.centerXToSuperview()
        }
        
        nameLabel.run {
            $0.topToBottom(of: handleLabel, offset: 8)
            $0.centerXToSuperview()
        }
        
        lastUpdateLabel.run {
            $0.topToBottom(of: nameLabel, offset: 12)
            $0.centerXToSuperview()
            $0.bottomToSuperview(offset: -8)
        }
    }
    
    func bind(_ uiModel: UserAccountCommonInfoView.UIModel) {
        self.uiModel = uiModel
        
        avatarImage.run {
            $0.sd_setImage(with: URL(string: uiModel.avatar), placeholderImage: noImage)
            $0.layer.borderColor = getColorByUserRank(uiModel.rank).cgColor
        }
        
        rankLabel.attributedText = uiModel.rankText
        handleLabel.attributedText = uiModel.handleText
        nameLabel.text = uiModel.nameText
        lastUpdateLabel.text = uiModel.lastUpdateText
    }
}

fileprivate extension UserAccountCommonInfoView.UIModel {
    
    private var none: NSAttributedString {
        return NSAttributedString(string: "None".localized)
    }
    
    var rankText: NSAttributedString {
        if let rank = rank {
            return colorTextByUserRank(text: rank.capitalized, rank: rank)
        } else {
            return none
        }
    }
    
    var handleText: NSAttributedString {
        return colorTextByUserRank(text: handle, rank: rank)
    }
    
    var nameText: String {
        let name = "\(firstName ?? "") \(lastName ?? "")"
        if name != "" {
            return name
        } else {
            return "None".localized
        }
    }

    var lastUpdateText: String {
        if let lastUpdate = lastUpdate {
            return "rating_updated_on".localizedFormat(args: Double(lastUpdate).secondsToUserUpdateDateString())
        } else {
            return "None".localized
        }
    }
}
