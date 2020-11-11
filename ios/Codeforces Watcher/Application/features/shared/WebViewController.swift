//
//  WebViewController.swift
//  Codeforces Watcher
//
//  Created by Den Matyash on 1/29/20.
//  Copyright © 2020 xorum.io. All rights reserved.
//

import Foundation
import WebKit
import FirebaseAnalytics
import PKHUD

class WebViewController: UIViewControllerWithCross, WKUIDelegate, WKNavigationDelegate {

    private lazy var webView = WKWebView(frame: .zero, configuration: WKWebViewConfiguration()).apply {
        $0.uiDelegate = self
        $0.allowsBackForwardNavigationGestures = true
        $0.navigationDelegate = self
    }

    var link: String!
    var shareText: String!

    var onOpenEvent: String? = nil
    var onShareEvent: String? = nil

    override func viewDidLoad() {
        super.viewDidLoad()
        view = webView
        navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(named: "shareImage"), style: .plain, target: self, action: #selector(shareTapped))
        openWebPage()
        
        sendOnOpenEvent()
    }
    
    private func sendOnOpenEvent() {
        if let onOpenEvent = onOpenEvent {
            analyticsControler.logEvent(eventName: onOpenEvent, params: [:])
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        showLoading()
    }
    
    private func showLoading() {
        PKHUD.sharedHUD.userInteractionOnUnderlyingViewsEnabled = true
        HUD.show(.progress, onView: navigationController?.view)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        hideLoading()
    }
    
    private func hideLoading() {
        HUD.hide(afterDelay: 0)
    }

    @objc func shareTapped() {
        let activityController = UIActivityViewController(activityItems: [shareText!], applicationActivities: nil).apply {
            $0.popoverPresentationController?.barButtonItem = navigationItem.rightBarButtonItem
        }

        present(activityController, animated: true)

        sendOnShareEvent()
    }
    
    private func sendOnShareEvent() {
        if let onShareEvent = onShareEvent {
            analyticsControler.logEvent(eventName: onShareEvent, params: [:])
        }
    }

    private func openWebPage() {
        if let url = URL(string: link) {
            webView.load(URLRequest(url: url))
        }
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        hideLoading()
    }

    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        hideLoading()
    }
}
