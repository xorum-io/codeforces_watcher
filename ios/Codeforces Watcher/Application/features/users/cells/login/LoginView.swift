//
//  LoginView.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/28/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit

class LoginView: UIView {

    private let contentView = CardView()

    private let galacticMasterView = GalacticMasterView()
    private let loginToIdentifyView = DoActionToIdentifyView()
    
    private let dashedLineView = UIView()
    
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

        [galacticMasterView, dashedLineView, loginToIdentifyView].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        galacticMasterView.edgesToSuperview(excluding: .trailing)
        
        dashedLineView.run {
            $0.width(2)
            $0.leadingToTrailing(of: galacticMasterView)
            $0.verticalToSuperview()
        }
        
        loginToIdentifyView.run {
            $0.width(to: galacticMasterView)
            $0.edgesToSuperview(excluding: .leading)
            $0.leadingToTrailing(of: dashedLineView)
        }
    }
    
    func bind(onClick: @escaping () -> ()) {
        loginToIdentifyView.bind(
            .init(
                title: "login_to_identify".localized,
                subtitle: "prompt_to_loginToIdentify".localized,
                buttonText: "login_in_42_seconds".localized,
                onButtonTap: onClick
            )
        )
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
