package com.bogdan.codeforceswatcher.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebView

/**
 * This class serves as a WebView to be used in conjunction with a com.bogdan.codeforceswatcher.components.VideoEnabledWebChromeClient.
 * It makes possible:
 * - To detect the HTML5 video ended event so that the com.bogdan.codeforceswatcher.components.VideoEnabledWebChromeClient can exit full-screen.
 *
 * Important notes:
 * - Javascript is enabled by default and must not be disabled with getSettings().setJavaScriptEnabled(false).
 * - setWebChromeClient() must be called before any loadData(), loadDataWithBaseURL() or loadUrl() method.
 *
 * @author Cristian Perez (http://cpr.name)
 */
class VideoEnabledWebView : WebView {
    inner class JavascriptInterface

    private var videoEnabledWebChromeClient: VideoEnabledWebChromeClient? = null
    private var addedJavascriptInterface: Boolean

    constructor(context: Context?) : super(context) {
        addedJavascriptInterface = false
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        addedJavascriptInterface = false
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        addedJavascriptInterface = false
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun setWebChromeClient(client: WebChromeClient) {
        settings.javaScriptEnabled = true
        if (client is VideoEnabledWebChromeClient) {
            videoEnabledWebChromeClient = client
        }
        super.setWebChromeClient(client)
    }

    override fun loadData(data: String, mimeType: String?, encoding: String?) {
        addJavascriptInterface()
        super.loadData(data, mimeType, encoding)
    }

    override fun loadDataWithBaseURL(baseUrl: String?, data: String, mimeType: String?, encoding: String?, historyUrl: String?) {
        addJavascriptInterface()
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    override fun loadUrl(url: String) {
        addJavascriptInterface()
        super.loadUrl(url)
    }

    override fun loadUrl(url: String, additionalHttpHeaders: Map<String, String>) {
        addJavascriptInterface()
        super.loadUrl(url, additionalHttpHeaders)
    }

    @SuppressLint("JavascriptInterface")
    private fun addJavascriptInterface() {
        if (!addedJavascriptInterface) {
            // Add javascript interface to be called when the video ends (must be done before page load)
            addJavascriptInterface(JavascriptInterface(), "_VideoEnabledWebView") // Must match Javascript interface name of com.bogdan.codeforceswatcher.components.VideoEnabledWebChromeClient
            addedJavascriptInterface = true
        }
    }
}