/*!
 * Copyright (c) Myra Security GmbH 2024.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package eu.eucaptcha.android.sdk

import android.util.Log

object EuCaptchaLog {
    private const val TAG: String = "EuCaptchaSDK"

    fun d(message: String, throwable: Throwable? = null) = Log.d(TAG, message, throwable)
    fun e(message: String, throwable: Throwable? = null) = Log.e(TAG, message, throwable)
    fun i(message: String, throwable: Throwable? = null) = Log.i(TAG, message, throwable)
    fun w(message: String, throwable: Throwable? = null) = Log.w(TAG, message, throwable)

    fun webView(consoleMessage: android.webkit.ConsoleMessage?) {
        if (consoleMessage == null) return
        val level = consoleMessage.messageLevel() ?: android.webkit.ConsoleMessage.MessageLevel.LOG
        val message = consoleMessage.message() ?: ""
        val sourceId = consoleMessage.sourceId() ?: "?"
        val lineNumber = consoleMessage.lineNumber()

        when (level) {
            android.webkit.ConsoleMessage.MessageLevel.ERROR ->
                Log.e(TAG, "[WebView] $message @ $sourceId:$lineNumber")
            android.webkit.ConsoleMessage.MessageLevel.WARNING ->
                Log.w(TAG, "[WebView] $message @ $sourceId:$lineNumber")
            else ->
                Log.d(TAG, "[WebView] $message @ $sourceId:$lineNumber")
        }
    }
}
