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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_page)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initViews()
        analyticsController.logEvent("action_opened")
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
                analyticsController.logEvent("action_share_comment")
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

        fun newIntent(context: Context, link: String, title: String): Intent {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(PAGE_TITLE_ID, title)
            intent.putExtra(PAGE_LINK_ID, link)
            return intent
        }
    }
}
