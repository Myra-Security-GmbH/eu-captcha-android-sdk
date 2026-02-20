package eu.eucaptcha.android.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import eu.eucaptcha.android.example.ui.theme.EuCaptchaExampleAppTheme
import eu.eucaptcha.android.sdk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val EU_CAPTCHA_SITEKEY = "YOUR_SITEKEY_HERE"

class MainActivity : ComponentActivity() {
    private val sdk by lazy { EuCaptchaSDK(context = this) }
    private val widget by lazy { sdk.createWidget(sitekey = EU_CAPTCHA_SITEKEY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EuCaptchaExampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginForm(
                        modifier = Modifier.padding(innerPadding),
                        onLoginClicked = { username, password, captchaResponse, setLoading, setMessage ->
                            setLoading(true)
                            setMessage("")
                            CoroutineScope(Dispatchers.Main).launch {
                                val response = doLoginRequest(username, password, captchaResponse)
                                setLoading(false)
                                if (response.success) {
                                    startActivity(Intent(this@MainActivity, SuccessActivity::class.java))
                                    finish()
                                } else {
                                    widget.reset()
                                    setMessage(response.message)
                                }
                            }
                        },
                        widget = widget,
                    )
                }
            }
        }
    }
}

@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    onLoginClicked: (String, String, String, (Boolean) -> Unit, (String) -> Unit) -> Unit,
    widget: EuCaptchaWidgetHandle,
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val captchaResponse = remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var buttonEnabled by remember { mutableStateOf(false) }

    widget.setOnStateChangeListener { event ->
        captchaResponse.value = event.response
        when (event.state) {
            "completed" -> buttonEnabled = true
            "expired"   -> buttonEnabled = false
            "error"     -> buttonEnabled = true
            else        -> {}
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(8.dp),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        AndroidView(
            factory = { _ -> widget.view },
            modifier = Modifier.fillMaxWidth().height(70.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onLoginClicked(
                    username.value,
                    password.value,
                    captchaResponse.value,
                    { isLoading = it },
                    { loginMessage = it },
                )
            },
            enabled = !isLoading && buttonEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003399)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Log in")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This is an example app. Any username and password will work.",
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(loginMessage, color = Color.Red)
    }
}
