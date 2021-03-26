package com.bogdan.codeforceswatcher.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.KeyEvent.KEYCODE_BACK
import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.components.VideoEnabledWebChromeClient.ToggledFullscreenCallback
import io.xorum.codeforceswatcher.redux.analyticsController
import kotlinx.android.synthetic.main.activity_web_page.*

class WebViewActivity : AppCompatActivity() {

    private val pageTitle: String
        get() = intent.getStringExtra(PAGE_TITLE_ID).orEmpty()

    private val link: String
        get() = intent.getStringExtra(PAGE_LINK_ID).orEmpty()

    private val openEvent: String?
        get() = intent.getStringExtra(PAGE_OPEN_EVENT_ID)

    private val shareEvent: String?
        get() = intent.getStringExtra(PAGE_SHARE_EVENT_ID)

    private var onFileGotCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_page)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initViews()

        openEvent?.let {
            analyticsController.logEvent(it)
        }
    }

    private fun initViews() {
        title = pageTitle
        setupWebView()
        swipeRefreshLayout.setOnRefreshListener { webView.reload() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_web_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                share()
                shareEvent?.let {
                    analyticsController.logEvent(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun share() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        val shareText = "$pageTitle - $link\n\n${getString(R.string.shared_through_cw)}"
        share.putExtra(Intent.EXTRA_TEXT, shareText)
        startActivity(Intent.createChooser(share, getString(R.string.share_with_friends)))
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() = with(webView) {
        loadUrl(link)

        val webChromeClient = object : VideoEnabledWebChromeClient(swipeRefreshLayout, videoLayout, null, webView) {

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                onFileGotCallback = filePathCallback
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                startActivityForResult(intent, FILECHOOSER_RESULTCODE)
                return true
            }
        }

        webChromeClient.setOnToggledFullscreen(object : ToggledFullscreenCallback {
            override fun toggledFullscreen(fullscreen: Boolean) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    val attrs = window.attributes

                    attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    window.attributes = attrs
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE

                    supportActionBar?.hide()
                } else {
                    val attrs = window.attributes
                    attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                    attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
                    window.attributes = attrs
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

                    supportActionBar?.show()
                }
            }
        })
        webView.setWebChromeClient(webChromeClient)

        settings.apply {
            javaScriptEnabled = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            allowFileAccess = true
            allowContentAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            domStorageEnabled = true
        }
        webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                progressBarHorizontal?.visibility = View.GONE
                webView?.visibility = View.VISIBLE
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KEYCODE_BACK -> {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILECHOOSER_RESULTCODE && resultCode == RESULT_OK) {
            data?.data?.let {
                onFileGotCallback?.onReceiveValue(arrayOf(it))
                onFileGotCallback = null
            }
        }
    }

    companion object {
        private const val PAGE_TITLE_ID = "page_title_id"
        private const val PAGE_LINK_ID = "page_link_id"
        private const val PAGE_OPEN_EVENT_ID = "page_open_event_id"
        private const val PAGE_SHARE_EVENT_ID = "page_share_event_id"

        private const val FILECHOOSER_RESULTCODE = 1

        fun newIntent(
                context: Context,
                link: String,
                title: String,
                openEvent: String? = null,
                shareEvent: String? = null
        ) = Intent(context, WebViewActivity::class.java).apply {
            putExtra(PAGE_TITLE_ID, title)
            putExtra(PAGE_LINK_ID, link)
            putExtra(PAGE_OPEN_EVENT_ID, openEvent)
            putExtra(PAGE_SHARE_EVENT_ID, shareEvent)
        }
    }
}
