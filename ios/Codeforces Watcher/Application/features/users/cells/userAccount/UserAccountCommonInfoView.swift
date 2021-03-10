//
//  UserAccountCommonInfoView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 3/10/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class UserAccountCommonInfoView: UIView {
    
    private let smth = UILabel()
    
    private let contentView = UIView()

    private let galacticIcon = UIImageView(image: UIImage(named: "galacticIcon"))
    private let galacticMasterLabel = UILabel().apply {
        $0.text = "galactic_master".localized
        $0.textColor = Palette.colorPrimary
        $0.font = Font.textSubheadingBig
    }
    private let formOfLifeLabel = UILabel().apply {
        $0.text = "form_of_life".localized
        $0.textColor = Palette.colorPrimary
        $0.font = Font.textBody
    }
    private let nameLabel = UILabel().apply {
        $0.text = "unknown".localized
        $0.font = Font.textSubheadingBig
    }
    private let updatingLabel = UILabel().apply {
        $0.text = "never_updated".localized
        $0.textColor = Palette.gray
        $0.font = Font.textSubheading
    }
    
    struct UIModel {
        let handle: String
        let rank: String?
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
        [smth, galacticIcon, galacticMasterLabel, formOfLifeLabel, nameLabel, updatingLabel].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        smth.edgesToSuperview(excluding: .bottom)
        
        galacticIcon.run {
            $0.topToSuperview(offset: 16)
            $0.centerXToSuperview()
        }
        
        galacticMasterLabel.run {
            $0.topToBottom(of: galacticIcon, offset: 8)
            $0.centerXToSuperview()
        }
        
        formOfLifeLabel.run {
            $0.topToBottom(of: galacticMasterLabel, offset: 4)
            $0.centerXToSuperview()
        }
        
        nameLabel.run {
            $0.topToBottom(of: formOfLifeLabel, offset: 8)
            $0.centerXToSuperview()
        }
        
        updatingLabel.run {
            $0.topToBottom(of: nameLabel, offset: 12)
            $0.centerXToSuperview()
            $0.bottomToSuperview(offset: -8)
        }
    }
    
    func bind(_ uiModel: UserAccountCommonInfoView.UIModel) {
        self.uiModel = uiModel
        
        smth.text = uiModel.handle
    }
}
