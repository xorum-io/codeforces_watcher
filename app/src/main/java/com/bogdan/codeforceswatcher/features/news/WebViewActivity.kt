package com.bogdan.codeforceswatcher.features.news

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.bogdan.codeforceswatcher.R
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
        settings.apply {
            javaScriptEnabled = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
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

    companion object {
        private const val PAGE_TITLE_ID = "page_title_id"
        private const val PAGE_LINK_ID = "page_link_id"
        private const val PAGE_OPEN_EVENT_ID = "page_open_event_id"
        private const val PAGE_SHARE_EVENT_ID = "page_share_event_id"

        fun newIntent(context: Context, link: String, title: String, openEvent: String? = null, shareEvent: String? = null): Intent {
            return Intent(context, WebViewActivity::class.java).apply {
                putExtra(PAGE_TITLE_ID, title)
                putExtra(PAGE_LINK_ID, link)
                putExtra(PAGE_OPEN_EVENT_ID, openEvent)
                putExtra(PAGE_SHARE_EVENT_ID, shareEvent)
            }
        }
    }
}
