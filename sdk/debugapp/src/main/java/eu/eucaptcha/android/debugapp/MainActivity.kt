package eu.eucaptcha.android.debugapp

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import eu.eucaptcha.android.sdk.EuCaptchaSDK
import eu.eucaptcha.android.sdk.EuCaptchaWidgetHandle

class MainActivity : AppCompatActivity() {

    private lateinit var stateTextView: TextView
    private lateinit var responseTextView: TextView
    private lateinit var widgetHandle: EuCaptchaWidgetHandle
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mount = findViewById<FrameLayout>(R.id.mount)
        stateTextView = findViewById(R.id.stateTextView)
        responseTextView = findViewById(R.id.responseTextView)
        submitButton = findViewById(R.id.submitButton)

        val sdk = EuCaptchaSDK(context = this)
        val sitekey = "YOUR_SITEKEY_HERE"

        widgetHandle = sdk.createWidget(sitekey)
        submitButton.isEnabled = false

        widgetHandle.setOnStateChangeListener { event ->
            runOnUiThread {
                stateTextView.text = event.state
                responseTextView.text = event.response
            }
        }

        widgetHandle.setOnCompleteListener {
            runOnUiThread { submitButton.isEnabled = true }
        }

        widgetHandle.setOnErrorListener {
            runOnUiThread { submitButton.isEnabled = true }
        }

        widgetHandle.setOnExpireListener {
            runOnUiThread { submitButton.isEnabled = false }
        }

        mount.addView(widgetHandle.view)

        submitButton.setOnClickListener {
            widgetHandle.reset()
            submitButton.isEnabled = false
        }
    }
}
