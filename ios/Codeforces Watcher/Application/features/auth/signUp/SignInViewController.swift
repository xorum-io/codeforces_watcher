//
//  SignInViewController.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 1/29/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit
import common
import PKHUD

class SignInViewController: ClosableViewController, ReKampStoreSubscriber {
    
    private let contentView = UIView()
    
    private let emailInput = TextInputLayout(explanation: "email".localized, type: .email)
    private let passwordInput = TextInputLayout(explanation: "password".localized, type: .password)
    private let forgotPasswordLabel = UILabel().apply {
        $0.text = "forgot_password".localized
        $0.textColor = Palette.colorPrimary
        $0.font = Font.textHint
    }
    private let signInButton = UIButton().apply {
        $0.setTitle("sign_in".localized.uppercased(), for: .normal)
        $0.setTitleColor(Palette.white, for: .normal)
        $0.backgroundColor = Palette.colorPrimary
        $0.layer.cornerRadius = 4
    }
    private let signUpView = SwitchToSignUpView()
    
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
        
        switch (state.signInStatus) {
        case AuthState.Status.done:
            hideLoading()
            closeViewController()
        case AuthState.Status.pending:
            showLoading()
        case AuthState.Status.idle:
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
        
        addKeyboardListeners()
    }
    
    private func setupTextInputs() {
        emailInput.textField.setupKeyboard()
        passwordInput.textField.setupKeyboard()
    }
    
    private func setupView() {
        view.backgroundColor = .white
        
        title = "sign_in".localized
    
        signInButton.titleLabel?.font = Font.textBody
        
        buildViewTree()
        setConstraints()
        setInteractions()
    }

    private func buildViewTree() {
        view.addSubview(contentView)
        [emailInput, passwordInput, forgotPasswordLabel, signInButton, signUpView].forEach(contentView.addSubview)
    }
    
    private var signUpViewConstraint = NSLayoutConstraint()
    
    private func setConstraints() {
        contentView.edgesToSuperview(insets: UIEdgeInsets(top: 16, left: 16, bottom: 16, right: 16))
        
        emailInput.run {
            $0.topToSuperview()
            $0.horizontalToSuperview()
        }
        
        passwordInput.run {
            $0.topToBottom(of: emailInput, offset: 16)
            $0.horizontalToSuperview()
        }
        
        forgotPasswordLabel.run {
            $0.topToBottom(of: passwordInput, offset: 8)
            $0.horizontalToSuperview()
        }
        
        signInButton.run {
            $0.height(36)
            $0.topToBottom(of: forgotPasswordLabel, offset: 16)
            $0.horizontalToSuperview()
        }
        
        signUpView.run {
            signUpViewConstraint = $0.bottomToSuperview()
            $0.centerXToSuperview()
        }
    }
    
    private func setInteractions() {
        signInButton.onTap(target: self, action: #selector(didSignInClick))
    }
    
    @objc func didSignInClick() {
        let email = emailInput.textField.text ?? ""
        let password = passwordInput.textField.text ?? ""
        store.dispatch(action: AuthRequests.SignIn(email: email, password: password))
    }
    
    private func addKeyboardListeners() {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    @objc func keyboardWillShow(_ notification: Notification) {
        guard let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.height else { return }
        
        signUpViewConstraint.constant = -keyboardSize
        view.layoutIfNeeded()
    }
    
    @objc func keyboardWillHide(_ notification: Notification) {
        signUpViewConstraint.constant = 0
        view.layoutIfNeeded()
    }
}
