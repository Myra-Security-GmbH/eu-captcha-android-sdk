/*!
 * Copyright (c) Myra Security GmbH 2026.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package eu.eucaptcha.android.sdk

import org.json.JSONObject

open class EuCaptchaWidgetEvent(
    val response: String,
) {
    companion object {
        const val WIDGET_COMPLETE = "euc:widget.complete"
        const val WIDGET_ERROR = "euc:widget.error"
        const val WIDGET_EXPIRE = "euc:widget.expire"
        const val WIDGET_STATECHANGE = "euc:widget.statechange"
    }
}

class EuCaptchaWidgetCompleteEvent(
    response: String,
) : EuCaptchaWidgetEvent(response) {
    companion object {
        fun fromJson(json: JSONObject): EuCaptchaWidgetCompleteEvent =
            EuCaptchaWidgetCompleteEvent(response = json.optString("response", ""))
    }
}

class EuCaptchaWidgetErrorEvent(
    response: String,
) : EuCaptchaWidgetEvent(response) {
    companion object {
        fun fromJson(json: JSONObject): EuCaptchaWidgetErrorEvent =
            EuCaptchaWidgetErrorEvent(response = json.optString("response", ".ERROR"))
    }
}

class EuCaptchaWidgetExpireEvent(
    response: String,
) : EuCaptchaWidgetEvent(response) {
    companion object {
        fun fromJson(json: JSONObject): EuCaptchaWidgetExpireEvent =
            EuCaptchaWidgetExpireEvent(response = json.optString("response", ".EXPIRED"))
    }
}

class EuCaptchaWidgetStateChangeEvent(
    val state: String,
    response: String,
) : EuCaptchaWidgetEvent(response) {
    companion object {
        fun fromJson(json: JSONObject): EuCaptchaWidgetStateChangeEvent =
            EuCaptchaWidgetStateChangeEvent(
                state = json.optString("state", EuCaptchaWidgetState.INIT),
                response = json.optString("response", ""),
            )
    }
}
