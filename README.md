# EU Captcha Android SDK

The EU Captcha Android SDK allows you to easily integrate [EU Captcha](https://eu-captcha.eu) into your Android applications.

- **Dashboard:** [app.eu-captcha.eu](https://app.eu-captcha.eu)
- **Documentation:** [docs.eu-captcha.eu](https://docs.eu-captcha.eu)

## Installation

Add the following to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "eu.eucaptcha.android:eu-captcha-android:1.0.0"
    // Or for `build.gradle.kts`
    // implementation("eu.eucaptcha.android:eu-captcha-android:1.0.0")
}
```

## Supported Platforms

The SDK supports **Android 4.1 Jelly Bean** (API level 16) and above.

This SDK is written in Kotlin and is compatible with both Kotlin and Java.

## Usage

### Kotlin (Compose)

```kotlin
import eu.eucaptcha.android.sdk.*

const val EU_CAPTCHA_SITEKEY = "YOUR_SITEKEY"

class MainActivity : ComponentActivity() {
    private val sdk by lazy {
        EuCaptchaSDK(context = this)
    }

    private val widget by lazy {
        sdk.createWidget(sitekey = EU_CAPTCHA_SITEKEY)
    }
}
```

Then in your UI:

```kotlin
val captchaResponse = remember { mutableStateOf("") }
var buttonEnabled by remember { mutableStateOf(false) }

widget.setOnStateChangeListener { event ->
    captchaResponse.value = event.response
    when (event.state) {
        "completed" -> buttonEnabled = true
        "expired"   -> buttonEnabled = false
        "error"     -> buttonEnabled = true
    }
}

AndroidView(factory = { _ -> widget.view })

Button(onClick = { widget.reset() }, enabled = buttonEnabled) {
    Text("Submit")
}
```

## License

Mozilla Public License Version 2.0
