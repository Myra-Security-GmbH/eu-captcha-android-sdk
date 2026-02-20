/*!
 * Copyright (c) Myra Security GmbH 2024.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package eu.eucaptcha.android.sdk

import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONObject

/**
 * Bridge between the WebView (JS) and the Android native layer.
 */
internal class EuCaptchaJSBridge(
    private val listener: (JSONObject) -> Unit,
    private val webView: WebView,
) {
    @JavascriptInterface
    fun receiveMessage(jsonData: String) {
        EuCaptchaLog.d("Received message from WebView: $jsonData")
        try {
            val data = JSONObject(jsonData)
            listener(data)
        } catch (ex: Exception) {
            EuCaptchaLog.e("Failed to parse JSON from WebView: $jsonData", ex)
        }
    }
}
