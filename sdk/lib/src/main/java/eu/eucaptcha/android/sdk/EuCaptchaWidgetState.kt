/*!
 * Copyright (c) Myra Security GmbH 2024.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package eu.eucaptcha.android.sdk

/**
 * Constants for the different states of the EU Captcha widget.
 * One of "init" | "checking" | "completed" | "expired" | "error" | "destroyed".
 */
class EuCaptchaWidgetState {
    companion object {
        @JvmField val INIT = "init"
        @JvmField val CHECKING = "checking"
        @JvmField val COMPLETED = "completed"
        @JvmField val EXPIRED = "expired"
        @JvmField val ERROR = "error"
        @JvmField val DESTROYED = "destroyed"
    }
}
