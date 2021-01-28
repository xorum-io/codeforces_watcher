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

    private let galacticMasterCardView = GalacticMasterView()
    private let loginToIdentifyCardView = LoginToIdentifyView()
    
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
        clipsToBounds = false
        buildViewTree()
        setConstraints()
    }

    private func buildViewTree() {
        addSubview(contentView)

        [galacticMasterCardView, dashedLineView, loginToIdentifyCardView].forEach(contentView.addSubview)
    }

    private func setConstraints() {
        contentView.edgesToSuperview()
        
        galacticMasterCardView.edgesToSuperview(excluding: .trailing)
        
        dashedLineView.run {
            $0.width(2)
            $0.leadingToTrailing(of: galacticMasterCardView)
            $0.verticalToSuperview()
        }
        
        loginToIdentifyCardView.run {
            $0.width(to: galacticMasterCardView)
            $0.edgesToSuperview(excluding: .leading)
            $0.leadingToTrailing(of: dashedLineView)
        }
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

        print(self.frame.height)
        
        let path = CGMutablePath()
        path.addLines(between: [CGPoint(x: 0, y: 8),
                                CGPoint(x: 0, y: height - 8)])
        shapeLayer.path = path
        layer.addSublayer(shapeLayer)
    }
}
