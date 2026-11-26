package com.example.iconbrowser

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView

/**
 * "Clear history automatically" preference plus the silent wipe itself —
 * no dialog, no toast. Cookies/site data are a separate opt-in because sites
 * (e.g. Cloudflare's "Verifying..." check) keep their verification there, and
 * wiping them makes the check reappear on every launch.
 */
object HistoryPrivacy {

    private const val PREFS = "history_privacy_prefs"
    private const val KEY_AUTO_CLEAR = "auto_clear_enabled"
    private const val KEY_CLEAR_COOKIES = "auto_clear_cookies"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isAutoClearEnabled(context: Context) = prefs(context).getBoolean(KEY_AUTO_CLEAR, false)

    fun setAutoClearEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_AUTO_CLEAR, enabled).apply()
    }

    fun isClearCookiesEnabled(context: Context) = prefs(context).getBoolean(KEY_CLEAR_COOKIES, false)

    fun setClearCookiesEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_CLEAR_COOKIES, enabled).apply()
    }

    fun clearSilently(context: Context, webView: WebView) {
        webView.clearHistory()
        webView.clearFormData()
        webView.clearCache(true)

        if (isClearCookiesEnabled(context)) {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()
            WebStorage.getInstance().deleteAllData()
        }
    }
}
