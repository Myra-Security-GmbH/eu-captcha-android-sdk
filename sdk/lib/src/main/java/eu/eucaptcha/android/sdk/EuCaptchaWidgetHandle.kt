/*!
 * Copyright (c) Myra Security GmbH 2026.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package eu.eucaptcha.android.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import java.net.URLEncoder

/**
 * A handle to an EU Captcha widget.
 * Use [EuCaptchaSDK.createWidget] to create instances.
 */
class EuCaptchaWidgetHandle(
    context: Context,
    sitekey: String,
    theme: String = "light",
    language: String = "en",
) {
    private var _response: String = ".UNINITIALIZED"
    private var _state: String = EuCaptchaWidgetState.INIT

    val response: String get() = _response
    val state: String get() = _state

    private var isReady = false

    // The widget always autostarts. We use "eucaptcha-android" as the synthetic host
    // for the challenge/solved-challenge API domain parameter.
    private val url: String =
        "file:///android_asset/eucaptcha/euc_index.html" +
            "#host=eucaptcha-android" +
            "&sitekey=${URLEncoder.encode(sitekey, "UTF-8")}" +
            "&protocol=https:" +
            "&theme=${URLEncoder.encode(theme, "UTF-8")}" +
            "&autostart=true"

    private var onComplete: ((EuCaptchaWidgetCompleteEvent) -> Unit)? = null
    private var onError: ((EuCaptchaWidgetErrorEvent) -> Unit)? = null
    private var onExpire: ((EuCaptchaWidgetExpireEvent) -> Unit)? = null
    private var onStateChange: ((EuCaptchaWidgetStateChangeEvent) -> Unit)? = null

    fun setOnCompleteListener(listener: (EuCaptchaWidgetCompleteEvent) -> Unit) {
        onComplete = listener
    }

    fun setOnErrorListener(listener: (EuCaptchaWidgetErrorEvent) -> Unit) {
        onError = listener
    }

    fun setOnExpireListener(listener: (EuCaptchaWidgetExpireEvent) -> Unit) {
        onExpire = listener
    }

    fun setOnStateChangeListener(listener: (EuCaptchaWidgetStateChangeEvent) -> Unit) {
        onStateChange = listener
    }

    @SuppressLint("SetJavaScriptEnabled")
    private val webView: WebView = WebView(context).apply {
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                val urlStr =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        request?.url.toString()
                    } else {
                        request?.toString()
                    }
                urlStr?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    if (context !is android.app.Activity) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    return true
                }
                return false
            }
        }
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        webChromeClient =
            object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                    EuCaptchaLog.webView(consoleMessage)
                    return true
                }
            }
        settings.userAgentString =
            BuildConfig.SDK_NAME + "/" + BuildConfig.SDK_VERSION + " " + settings.userAgentString
        loadUrl(this@EuCaptchaWidgetHandle.url)
        layoutParams =
            ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
    }

    val view: android.view.View

    init {
        val maxHeightPx =
            kotlin.math.ceil(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    70f,
                    context.resources.displayMetrics,
                ),
            ).toInt()
        val maxWidthPx =
            kotlin.math.ceil(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    420f,
                    context.resources.displayMetrics,
                ),
            ).toInt()
        val borderRadius =
            kotlin.math.ceil(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4f,
                    context.resources.displayMetrics,
                ),
            )

        val innerLayout =
            ConstraintLayout(context).apply {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                maxHeight = maxHeightPx
                maxWidth = maxWidthPx
                addView(webView)
            }

        val cardView =
            CardView(context).apply {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                radius = borderRadius
                cardElevation = 0.0f
                addView(innerLayout)
            }

        val outerLayout =
            ConstraintLayout(context).apply {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                maxHeight = maxHeightPx
                maxWidth = maxWidthPx
                addView(cardView)
            }

        view = outerLayout
    }

    @SuppressLint("AddJavascriptInterface")
    private val bridge: EuCaptchaJSBridge =
        EuCaptchaJSBridge({ json ->
            handleMessage(json)
        }, webView).apply {
            webView.addJavascriptInterface(this, "Android")
        }

    /**
     * The widget autostarts. This method is a no-op provided for API compatibility.
     */
    fun start() {
        // EU Captcha widget autostarts. No manual trigger needed.
    }

    private fun handleMessage(json: JSONObject) {
        when (json.getString("type")) {
            "ready" -> {
                isReady = true
            }
            EuCaptchaWidgetEvent.WIDGET_COMPLETE -> {
                val event = EuCaptchaWidgetCompleteEvent.fromJson(json)
                _response = event.response
                _state = EuCaptchaWidgetState.COMPLETED
                onComplete?.invoke(event)
            }
            EuCaptchaWidgetEvent.WIDGET_ERROR -> {
                val event = EuCaptchaWidgetErrorEvent.fromJson(json)
                _response = event.response
                _state = EuCaptchaWidgetState.ERROR
                onError?.invoke(event)
            }
            EuCaptchaWidgetEvent.WIDGET_EXPIRE -> {
                val event = EuCaptchaWidgetExpireEvent.fromJson(json)
                _response = event.response
                _state = EuCaptchaWidgetState.EXPIRED
                onExpire?.invoke(event)
            }
            EuCaptchaWidgetEvent.WIDGET_STATECHANGE -> {
                val event = EuCaptchaWidgetStateChangeEvent.fromJson(json)
                _response = event.response
                _state = event.state
                onStateChange?.invoke(event)
            }
            else -> {
                EuCaptchaLog.e("Unknown event type: ${json.getString("type")}")
            }
        }
    }

    /**
     * Reset the widget. This reloads the captcha so a new proof-of-work is performed.
     */
    fun reset() {
        if (isReady) {
            (webView.context as? android.app.Activity)?.runOnUiThread {
                webView.loadUrl(url)
            }
            _response = ".UNINITIALIZED"
            _state = EuCaptchaWidgetState.INIT
        }
    }

    /**
     * Destroy the widget and release resources.
     */
    fun destroy() {
        view.visibility = android.view.View.GONE
        webView.destroy()

        _response = ".DESTROYED"
        _state = EuCaptchaWidgetState.DESTROYED

        onStateChange?.invoke(
            EuCaptchaWidgetStateChangeEvent(
                state = EuCaptchaWidgetState.DESTROYED,
                response = _response,
            ),
        )

        onComplete = null
        onError = null
        onExpire = null
        onStateChange = null
    }
}
