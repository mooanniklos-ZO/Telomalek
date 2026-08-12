package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTertiary
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isServiceActive by viewModel.isServiceActive.collectAsStateWithLifecycle()
    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsStateWithLifecycle()

    var showClearDialog by remember { mutableStateOf(false) }
    var isTutorialExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "إعدادات النظام والخدمة الخلفية",
                style = MaterialTheme.typography.titleLarge.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "التحكم في تشغيل الرصد المتواصل 24/7 والإشعارات والصيانة",
                style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Foreground Service Toggle Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.SettingsSuggest, contentDescription = null, tint = CyberPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "خدمة الرصد بالخلفية (Foreground Service)",
                                    style = MaterialTheme.typography.titleMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "العمل المتواصل بدون إغلاق النظام للتطبيقات",
                                    style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary, fontSize = 12.sp)
                                )
                            }
                        }

                        Switch(
                            checked = isServiceActive,
                            onCheckedChange = { viewModel.toggleServiceActive(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyberPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = CyberSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Notifications Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = CyberGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "إشعارات الوظائف الجديدة (System Push)",
                                style = MaterialTheme.typography.titleSmall.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                            )
                        }

                        Switch(
                            checked = isNotificationsEnabled,
                            onCheckedChange = { viewModel.toggleNotifications(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyberGold
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Battery Optimization Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.BatteryFull, contentDescription = null, tint = CyberTertiary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "تحسين استهلاك البطارية والنظام",
                            style = MaterialTheme.typography.titleMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "لضمان عدم إيقاف الخدمة بواسطة نظام Android عند إغلاق الشاشة، يرجى استثناء التطبيق من تحسينات البطارية.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("فتح إعدادات استثناء البطارية", color = CyberTertiary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Step-by-Step Tutorial Card: How to get Telegram API ID & Hash
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isTutorialExpanded = !isTutorialExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = CyberGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "كيف تحصل على API ID و API Hash من تلغرام؟",
                                style = MaterialTheme.typography.titleMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                            )
                        }

                        Icon(
                            imageVector = if (isTutorialExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = CyberGold
                        )
                    }

                    AnimatedVisibility(visible = isTutorialExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(color = CyberSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))

                            TutorialStep(stepNumber = "1", title = "افتح موقع Telegram الرسمي", desc = "انتقل إلى my.telegram.org عبر المتصفح")
                            TutorialStep(stepNumber = "2", title = "سجل الدخول برقم هاتفك", desc = "أدخل رقم هاتفك واكتب كود التحقق الذي يصلك داخل تطبيق تلغرام")
                            TutorialStep(stepNumber = "3", title = "اختر API development tools", desc = "اضغط على خيار أدوات تطوير API")
                            TutorialStep(stepNumber = "4", title = "أنشئ تطبيقاً جديداً", desc = "اكتب أي اسم عشوائي للتطبيق (مثلاً TeleJob App) واحفظ البيانات")
                            TutorialStep(stepNumber = "5", title = "انسخ App api_id و App api_hash", desc = "ضع القيمتين في شاشة ربط تلغرام داخل هذا التطبيق")

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://my.telegram.org"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberGold)
                            ) {
                                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("الانتقال إلى my.telegram.org", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Database & Maintenance Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "إدارة البيانات والصيانة",
                            style = MaterialTheme.typography.titleMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "مسح جميع الوظائف المستخرجة المحفوظة في قاعدة بيانات Room المحلية.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
                    ) {
                        Text("مسح جميع الوظائف المحفوظة", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("تأكيد مسح قاعدة البيانات", color = CyberTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت تأكد من رغبتك في مسح كل الوظائف المستخرجة؟ لا يمكن التراجع عن هذا الإجراء.", color = CyberTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllJobs()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("مسح الكل", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("إلغاء", color = CyberTextSecondary)
                }
            },
            containerColor = CyberSurface
        )
    }
}

@Composable
private fun TutorialStep(stepNumber: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberGold.copy(alpha = 0.2f),
            modifier = Modifier.size(24.dp)
        ) {
            Text(
                text = stepNumber,
                style = MaterialTheme.typography.labelMedium.copy(color = CyberGold, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(4.dp),
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.labelLarge.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold))
            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary, fontSize = 12.sp))
        }
    }
}
