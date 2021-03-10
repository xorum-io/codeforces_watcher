//
//  UserAccountView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 3/10/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class UserAccountView: UIView {

    private let contentView = CardView()
    
    private let smth = UILabel()
    
    private let galacticMasterView = GalacticMasterView()
    private let verifyToIdentifyView = VerifyToIdentifyView()
    
    private let dashedLineView = UIView()
    
    struct UIModel {
        let handle: String
        let rank: String?
    }
    
    private var uiModel: UserAccountView.UIModel? = nil
    
//    init(_ uiModel: UserAccountView.UIModel) {
//        self.uiModel = uiModel
//
//        super.init(frame: CGRect.zero)
//
//        setupView()
//    }
//
//    required init?(coder: NSCoder) {
//        fatalError("init(coder:) has not been implemented")
//    }
    
    public override init(frame: CGRect) {

        super.init(frame: frame)
        setupView()
    }

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    private func setupView() {
        buildViewTree()
        setConstraints()
    }

    private func buildViewTree() {
        addSubview(contentView)

        [smth, galacticMasterView, dashedLineView, verifyToIdentifyView].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        smth.edgesToSuperview(excluding: .bottom)
        
        galacticMasterView.edgesToSuperview(excluding: .trailing)
        
        dashedLineView.run {
            $0.width(2)
            $0.leadingToTrailing(of: galacticMasterView)
            $0.verticalToSuperview()
        }
        
        verifyToIdentifyView.run {
            $0.width(to: galacticMasterView)
            $0.edgesToSuperview(excluding: .leading)
            $0.leadingToTrailing(of: dashedLineView)
        }
    }
    
    func bind(_ uiModel: UserAccountView.UIModel) {
        self.uiModel = uiModel
        
        smth.text = uiModel.handle
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        
        dashedLineView.addDashedBorder(height: frame.height)
    }
}

private extension UIView {

    func addDashedBorder(height: CGFloat) {
        
        let shapeLayer = CAShapeLayer().apply {
            $0.lineWidth = 2
            $0.strokeColor = Palette.dividerGray.cgColor
            
            $0.lineDashPattern = [2,3]
        }

        let path = CGMutablePath()
        path.addLines(between: [CGPoint(x: 0, y: 8),
                                CGPoint(x: 0, y: height - 8)])
        shapeLayer.path = path
        layer.addSublayer(shapeLayer)
    }
}
