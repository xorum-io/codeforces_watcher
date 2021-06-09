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
    private let confirmInput = TextInputLayout(hint: "confirm_password".localized, type: .password)
    private let signUpAgreement = SignUpAgreementView()
    private let signUpButton = PrimaryButton().apply {
        $0.setTitle("sign_up".localized.uppercased(), for: .normal)
        $0.isEnabled = false
        $0.alpha = 0.5
    }
    private let signInLabel = ActionableLabel(hintText: "sign_in_hint".localized, linkText: "sign_in".localized)
    
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
        PKHUD.sharedHUD.userInteractionOnUnderlyingViewsEnabled = false
        HUD.show(.progress, onView: UIApplication.shared.windows.last)
    }
    
    private func hideLoading() {
        HUD.hide(afterDelay: 0)
    }
    
    private func closeViewController() {
        self.presentingViewController?.dismiss(animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupTextInputs()
        setupSignUpAgreement()
        setupView()
        
        addKeyboardListeners()
    }
    
    private func setupTextInputs() {
        emailInput.textField.setupKeyboard()
        passwordInput.textField.setupKeyboard()
        confirmInput.textField.setupKeyboard()
    }
    
    private func setupSignUpAgreement() {
        signUpAgreement.onCheckboxTap = { isSelected in
            self.signUpButton.run {
                $0.isEnabled = isSelected
                $0.alpha = (isSelected ? 1.0 : 0.5)
            }
        }
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
        [emailInput, passwordInput, confirmInput, signUpAgreement, signUpButton, signInLabel].forEach(contentView.addSubview)
    }
    
    private var signInViewConstraint = NSLayoutConstraint()
    
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
        
        signUpAgreement.run {
            $0.topToBottom(of: confirmInput, offset: 16)
            $0.horizontalToSuperview()
        }
        
        signUpButton.run {
            $0.height(36)
            $0.topToBottom(of: signUpAgreement, offset: 16)
            $0.horizontalToSuperview()
        }
        
        signInLabel.run {
            signInViewConstraint = $0.bottomToSuperview(usingSafeArea: true)
            $0.centerXToSuperview()
        }
    }
    
    private func setInteractions() {
        signUpButton.onTap(target: self, action: #selector(didSignUpClick))
        signInLabel.onTap(target: self, action: #selector(didSignInClick))
    }
    
    @objc func didSignUpClick() {
        let email = emailInput.textField.text ?? ""
        let password = passwordInput.textField.text ?? ""
        let confirmPassword = confirmInput.textField.text ?? ""
        
        if (password == confirmPassword) {
            store.dispatch(action: AuthRequests.SignUp(email: email, password: password))
        } else {
            store.dispatch(action: AuthRequests.SignUpFailure(message: ToastActionKt.toMessage("passwords_do_not_match".localized)))
        }
    }
    
    @objc func didSignInClick() {
        dismiss(animated: true)
    }
    
    private func addKeyboardListeners() {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    @objc func keyboardWillShow(_ notification: Notification) {
        guard let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.height else { return }
        
        signInViewConstraint.constant = -keyboardSize
        view.layoutIfNeeded()
    }
    
    @objc func keyboardWillHide(_ notification: Notification) {
        signInViewConstraint.constant = 0
        view.layoutIfNeeded()
    }
}
