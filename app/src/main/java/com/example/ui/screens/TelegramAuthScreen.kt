package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.telegram.TDLibClientState
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTertiary
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun TelegramAuthScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val savedApiId by viewModel.telegramApiId.collectAsStateWithLifecycle()
    val savedApiHash by viewModel.telegramApiHash.collectAsStateWithLifecycle()
    val savedPhone by viewModel.telegramPhone.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isTelegramLoggedIn.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val savedGeminiKey by viewModel.geminiApiKey.collectAsStateWithLifecycle()

    var apiIdInput by remember(savedApiId) { mutableStateOf(savedApiId) }
    var apiHashInput by remember(savedApiHash) { mutableStateOf(savedApiHash) }
    var phoneInput by remember(savedPhone) { mutableStateOf(savedPhone) }
    var otpInput by remember { mutableStateOf("") }
    var geminiKeyInput by remember(savedGeminiKey) { mutableStateOf(savedGeminiKey) }

    var isKeySavedSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "إعدادات الربط مع Telegram و Gemini AI",
                style = MaterialTheme.typography.titleLarge.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "قم بضبط بيانات حساب تلغرام ومفتاح الذكاء الاصطناعي لتمكين الاستخراج التلقائي",
                style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Connection Status Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isLoggedIn) Icons.Default.CheckCircle else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isLoggedIn) CyberTertiary else CyberGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isLoggedIn) "متصل بنجاح بحساب Telegram" else "في انتظار إكمال البيانات",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = if (isLoggedIn) CyberTertiary else CyberGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = statusMessage,
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Telegram API Credentials Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = CyberSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "بيانات اعتماد Telegram Native Client (TDLib)",
                            style = MaterialTheme.typography.titleMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apiIdInput,
                        onValueChange = { apiIdInput = it },
                        label = { Text("API ID (من my.telegram.org)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = CyberPrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = apiHashInput,
                        onValueChange = { apiHashInput = it },
                        label = { Text("API Hash") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = CyberPrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("رقم الهاتف بصيغة دولية (مثل +201012345678)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = CyberPrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.saveTelegramCredentials(apiIdInput.trim(), apiHashInput.trim(), phoneInput.trim())
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary)
                    ) {
                        Text("حفظ وإرسال كود التحقق OTP", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // OTP Verification Card (Visible when code is expected)
        if (connectionState == TDLibClientState.WAIT_CODE || connectionState == TDLibClientState.CONNECTED) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "إدخال كود التحقق OTP المكون من 5 أرقام:",
                            style = MaterialTheme.typography.titleMedium.copy(color = CyberGold, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it },
                            label = { Text("رمز التحقيق من تطبيق تلغرام") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.submitOtpCode(otpInput.trim())
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberTertiary)
                        ) {
                            Text("تأكيد كود التحقق والدخول", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Gemini AI API Key Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = CyberGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مفتاح الذكاء الاصطناعي Google Gemini API",
                            style = MaterialTheme.typography.titleMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "يُستخدم نموذج Gemini 3.5 Flash لتحليل نصوص الوظائف، استخراج الميزانية، والترجمة للعربية",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = geminiKeyInput,
                        onValueChange = { geminiKeyInput = it },
                        label = { Text("Gemini API Key (اختياري / مدمج بالـ Build)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.saveGeminiApiKey(geminiKeyInput.trim())
                            isKeySavedSuccess = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGold)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ واختبار مفتاح AI", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    if (isKeySavedSuccess) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = CyberTertiary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✅ تم حفظ واختبار مفتاح Gemini API بنجاح!",
                                style = MaterialTheme.typography.bodySmall.copy(color = CyberTertiary, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CyberSurfaceVariant.copy(alpha = 0.4f),
    unfocusedContainerColor = CyberSurfaceVariant.copy(alpha = 0.4f),
    focusedBorderColor = CyberPrimary,
    unfocusedBorderColor = CyberSurfaceVariant,
    focusedTextColor = CyberTextPrimary,
    unfocusedTextColor = CyberTextPrimary
)
