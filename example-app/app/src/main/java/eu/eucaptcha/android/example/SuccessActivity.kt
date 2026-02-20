package eu.eucaptcha.android.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.eucaptcha.android.example.ui.theme.EuCaptchaExampleAppTheme

class SuccessActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EuCaptchaExampleAppTheme {
                SuccessPage(onTryAgain = {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                })
            }
        }
    }
}

@Composable
fun SuccessPage(modifier: Modifier = Modifier, onTryAgain: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF003399),
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = "Success",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "The EU Captcha challenge was accepted.",
            fontSize = 12.sp,
            lineHeight = 24.sp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onTryAgain() },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Log out")
        }
    }
}
