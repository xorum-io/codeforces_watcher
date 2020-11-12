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
    
    let link: String
    let titleName: String

    let onOpenEvent: String?
    let onShareEvent: String?
    
    init(_ link: String, _ titleName: String, _ onOpenEvent: String? = nil, _ onShareEvent: String? = nil) {
        self.link = link
        self.titleName = titleName
        self.onOpenEvent = onOpenEvent
        self.onShareEvent = onShareEvent
        
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupView()
        openWebPage()
        sendOnOpenEvent()
    }
    
    private func setupView() {
        view = webView
        title = titleName
        navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(named: "shareImage"), style: .plain, target: self, action: #selector(shareTapped))
    }

    @objc func shareTapped() {
        let shareText = buildShareText(titleName, link)
        let activityController = UIActivityViewController(activityItems: [shareText], applicationActivities: nil).apply {
            $0.popoverPresentationController?.barButtonItem = navigationItem.rightBarButtonItem
        }

        present(activityController, animated: true)

        sendOnShareEvent()
    }
    
    private func sendOnOpenEvent() {
        guard let onOpenEvent = onOpenEvent else { return }
        analyticsControler.logEvent(eventName: onOpenEvent, params: [:])
    }
    
    private func sendOnShareEvent() {
        guard let onShareEvent = onShareEvent else { return }
        analyticsControler.logEvent(eventName: onShareEvent, params: [:])
    }

    private func openWebPage() {
        if let url = URL(string: link) {
            webView.load(URLRequest(url: url))
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        showLoading()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        hideLoading()
    }
    
    private func showLoading() {
        PKHUD.sharedHUD.userInteractionOnUnderlyingViewsEnabled = true
        HUD.show(.progress, onView: navigationController?.view)
    }
    
    private func hideLoading() {
        HUD.hide(afterDelay: 0)
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        hideLoading()
    }

    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        hideLoading()
    }
}
