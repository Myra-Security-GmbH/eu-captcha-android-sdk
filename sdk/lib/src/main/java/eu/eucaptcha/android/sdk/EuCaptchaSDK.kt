/*!
 * Copyright (c) Myra Security GmbH 2024.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package eu.eucaptcha.android.sdk

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

private fun getDeviceLanguage(): String {
    val dl = Locale.getDefault()
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        dl.toLanguageTag()
    } else {
        dl.language
    }
}

private fun getConfigurationTheme(context: Context): String {
    return when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_YES -> "dark"
        Configuration.UI_MODE_NIGHT_NO -> "light"
        else -> "light"
    }
}

/**
 * The main entry point for the EU Captcha SDK.
 * Create one instance of this class and use it to create multiple widgets.
 *
 * @param context The Android context to use for the SDK.
 */
class EuCaptchaSDK(private val context: Context) {

    /**
     * Create a new EU Captcha widget.
     *
     * @param sitekey The sitekey for the widget.
     * @param theme The theme: "light", "dark", or null (uses system UI mode).
     * @param language The language code (e.g. "en", "de"). Defaults to device language.
     */
    @JvmOverloads
    fun createWidget(
        sitekey: String,
        theme: String? = null,
        language: String? = null,
    ): EuCaptchaWidgetHandle {
        val resolvedTheme = theme ?: getConfigurationTheme(context)
        val resolvedLanguage = language ?: getDeviceLanguage()

        return EuCaptchaWidgetHandle(
            context = context,
            sitekey = sitekey,
            theme = resolvedTheme,
            language = resolvedLanguage,
        )
    }
}
