//
//  VerifyViewController.swift
//  Codeforces Watcher
//
//  Created by Ivan Karavaiev on 2/15/21.
//  Copyright © 2021 xorum.io. All rights reserved.
//

import UIKit
import common
import PKHUD

class VerifyViewController: ClosableViewController, ReKampStoreSubscriber {
    
    private let contentView = UIView()
    
    private let handleInput = TextInputLayout(hint: "codeforces_handle".localized, type: .email)
    private let verifyButton = PrimaryButton().apply {
        $0.setTitle("verify".localized.uppercased(), for: .normal)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        store.subscribe(subscriber: self) { subscription in
            subscription.skipRepeats { oldState, newState in
                return KotlinBoolean(bool: oldState.verification == newState.verification)
            }.select { state in
                return state.verification
            }
        }
    }
    
    func onNewState(state: Any) {
        let state = state as! VerificationState
        
        switch (state.status) {
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
        self.presentingViewController?.dismiss(animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupTextInputs()
        setupView()
    }
    
    private func setupTextInputs() {
        handleInput.textField.setupKeyboard()
    }
    
    private func setupView() {
        view.backgroundColor = .white
        
        title = "verify".localized
        
        buildViewTree()
        setConstraints()
        setInteractions()
    }

    private func buildViewTree() {
        view.addSubview(contentView)
        [handleInput, verifyButton].forEach(contentView.addSubview)
    }
    
    private func setConstraints() {
        contentView.edgesToSuperview(insets: .uniform(16))
        
        handleInput.run {
            $0.topToSuperview()
            $0.horizontalToSuperview()
        }
        
        verifyButton.run {
            $0.height(36)
            $0.topToBottom(of: handleInput, offset: 16)
            $0.horizontalToSuperview()
        }
    }
    
    private func setInteractions() {
        verifyButton.onTap(target: self, action: #selector(didVerifyClick))
    }
    
    @objc func didVerifyClick() {
        let handle = handleInput.textField.text ?? ""
        store.dispatch(action: VerificationRequests.Verify(handle: handle))
    }
}
