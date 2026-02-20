/*!
 * Copyright (c) Myra Security GmbH 2024.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package eu.eucaptcha.android.sdk

interface OnWidgetCompleteListener {
    fun onComplete(event: EuCaptchaWidgetCompleteEvent)
}

interface OnWidgetErrorListener {
    fun onError(event: EuCaptchaWidgetErrorEvent)
}

interface OnWidgetExpireListener {
    fun onExpire(event: EuCaptchaWidgetExpireEvent)
}

interface OnWidgetStateChangeListener {
    fun onStateChange(event: EuCaptchaWidgetStateChangeEvent)
}
