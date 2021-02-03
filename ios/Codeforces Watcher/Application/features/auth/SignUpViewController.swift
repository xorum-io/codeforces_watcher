//
//  SignUpViewController.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/3/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit
import common
import PKHUD

class SignUpViewController: ClosableViewController, ReKampStoreSubscriber {
    
    private let contentView = UIView()
    
    private let emailInput = TextInputLayout(hint: "email".localized, type: .email)
    private let passwordInput = TextInputLayout(hint: "password".localized, type: .password)
    private let confirmInput = TextInputLayout(hint: "password".localized, type: .password)
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        store.subscribe(subscriber: self) { subscription in
            subscription.skipRepeats { oldState, newState in
                return KotlinBoolean(bool: oldState.auth == newState.auth)
            }.select { state in
                return state.auth
            }
        }
    }
    
    func onNewState(state: Any) {
        let state = state as! AuthState
        
        switch (state.signUpStatus) {
        case .done:
            hideLoading()
            closeViewController()
        case .pending:
            showLoading()
        case .idle:
            hideLoading()
        default:
            return
        }
    }
    
    private func showLoading() {
        HUD.show(.progress, onView: UIApplication.shared.windows.last)
    }
    
    private func hideLoading() {
        HUD.hide(afterDelay: 0)
    }
    
    private func closeViewController() {
        self.presentingViewController?.presentingViewController?.dismiss(animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupTextInputs()
        setupView()
    }
    
    private func setupTextInputs() {
        emailInput.textField.setupKeyboard()
        passwordInput.textField.setupKeyboard()
    }
    
    private func setupView() {
        view.backgroundColor = .white
        
        title = "sign_up".localized
    
        buildViewTree()
        setConstraints()
        setInteractions()
    }

    private func buildViewTree() {
        view.addSubview(contentView)
        [emailInput, passwordInput, confirmInput].forEach(contentView.addSubview)
    }
    
    private var signUpViewConstraint = NSLayoutConstraint()
    
    private func setConstraints() {
        contentView.edgesToSuperview(insets: .uniform(16))
        
        emailInput.run {
            $0.topToSuperview()
            $0.horizontalToSuperview()
        }
        
        passwordInput.run {
            $0.topToBottom(of: emailInput, offset: 16)
            $0.horizontalToSuperview()
        }
        
        confirmInput.run {
            $0.topToBottom(of: passwordInput, offset: 16)
            $0.horizontalToSuperview()
        }
    }
    
    private func setInteractions() {
        
    }
}
