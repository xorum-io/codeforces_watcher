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
    
    private let commonInfoView = UserAccountCommonInfoView()
    private let performanceView = UserAccountPerformanceView()
    
    private let dashedLineView = UIView()
    
    struct UIModel {
        let handle: String
        let rank: String?
    }
    
    private var uiModel: UserAccountView.UIModel? = nil
    
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

        [commonInfoView, dashedLineView, performanceView].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        commonInfoView.edgesToSuperview(excluding: .trailing)
        
        dashedLineView.run {
            $0.width(2)
            $0.leadingToTrailing(of: commonInfoView)
            $0.verticalToSuperview()
        }
        
        performanceView.run {
            $0.width(to: commonInfoView)
            $0.edgesToSuperview(excluding: .leading)
            $0.leadingToTrailing(of: dashedLineView)
        }
    }
    
    func bind(_ uiModel: UserAccountView.UIModel) {
        self.uiModel = uiModel
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
