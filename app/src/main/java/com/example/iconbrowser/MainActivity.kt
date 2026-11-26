package com.example.iconbrowser

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.iconbrowser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val homeUrl = "https://www.google.com"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settings = binding.webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        // Keep the device's real Chromium version but drop the WebView markers
        // (";wv" and "Version/4.0"). A made-up Chrome version can itself
        // trigger bot checks.
        settings.userAgentString = settings.userAgentString
            .replace("; wv", "")
            .replace("Version/4.0 ", "")

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webView, true)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                binding.progressBar.visibility = View.VISIBLE
                url?.let { binding.urlInput.setText(it) }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progressBar.visibility = View.GONE
                CookieManager.getInstance().flush()
            }
        }

        if (HistoryPrivacy.isAutoClearEnabled(this)) {
            HistoryPrivacy.clearSilently(this, binding.webView)
        }

        binding.webView.loadUrl(homeUrl)

        binding.urlInput.setOnEditorActionListener { _, _, _ ->
            loadFromInput()
            true
        }

        binding.settingsButton.setOnClickListener {
            startActivity(android.content.Intent(this, SettingsActivity::class.java))
        }
    }

    private fun loadFromInput() {
        val raw = binding.urlInput.text.toString().trim()
        if (raw.isEmpty()) return

        val looksLikeUrl = raw.contains(".") && !raw.contains(" ")
        val target = when {
            raw.startsWith("http://") || raw.startsWith("https://") -> raw
            looksLikeUrl -> "https://$raw"
            else -> "https://www.google.com/search?q=${android.net.Uri.encode(raw)}"
        }
        binding.webView.loadUrl(target)
    }

    override fun onStop() {
        super.onStop()
        if (HistoryPrivacy.isAutoClearEnabled(this)) {
            HistoryPrivacy.clearSilently(this, binding.webView)
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
