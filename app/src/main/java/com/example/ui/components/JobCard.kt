package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JobOffer
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTertiary
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JobCard(
    job: JobOffer,
    onToggleFavorite: (JobOffer) -> Unit,
    onDeleteJob: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    val formattedDate = remember(job.timestamp) {
        val sdf = SimpleDateFormat("dd MMM - hh:mm a", Locale("ar"))
        sdf.format(Date(job.timestamp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Category badge & Actions (Favorite, Share, Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CyberPrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CyberPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = job.category,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = CyberPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onToggleFavorite(job) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (job.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "مفضلة",
                            tint = if (job.isFavorite) CyberGold else CyberTextSecondary
                        )
                    }

                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "💼 ${job.jobTitle}\n\n💰 الميزانية: ${job.CompensationAmount}\n\n📍 المصدر: ${job.sourceGroupName}\n🔗 للتواصل: ${job.employerContactLink}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "مشاركة تفاصيل الوظيفة"))
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = CyberTextSecondary
                        )
                    }

                    IconButton(
                        onClick = { onDeleteJob(job.id) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "حذف",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Job Title
            Text(
                text = job.jobTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = CyberTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Compensation & Source Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Compensation Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = job.CompensationAmount,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = CyberGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Source Channel Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = CyberSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = job.sourceGroupName,
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberTextSecondary),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact Employer Action Button & Expand Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        try {
                            val uri = Uri.parse(
                                if (job.employerContactLink.startsWith("http") || job.employerContactLink.startsWith("tg:")) {
                                    job.employerContactLink
                                } else {
                                    "https://t.me/${job.employerContactLink.replace("@", "")}"
                                }
                            )
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("التواصل عبر تلغرام", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpanded) "إخفاء التفاصيل" else "كل التفاصيل",
                        style = MaterialTheme.typography.labelMedium.copy(color = CyberPrimary)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = CyberPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Expandable details block
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    HorizontalDivider(color = CyberSurfaceVariant, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Workflow Methodology Section
                    DetailSection(
                        title = "طريقة وخطوات التنفيذ (Methodology):",
                        content = job.workflowMethodology,
                        icon = Icons.Default.WorkOutline,
                        iconColor = CyberPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Delivery & Payment Instructions Section
                    DetailSection(
                        title = "تعليمات التسليم وصرف المستحقات:",
                        content = job.deliveryAndPaymentInstructions,
                        icon = Icons.Default.AttachMoney,
                        iconColor = CyberGold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Full Arabic Translation
                    DetailSection(
                        title = "الترجمة العربية للوظيفة:",
                        content = job.translatedArabicText,
                        icon = Icons.Default.Language,
                        iconColor = CyberTertiary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Raw Telegram Text
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CyberSurfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "المنشور الأصلي كما وصل من تلغرام:",
                                style = MaterialTheme.typography.labelMedium.copy(color = CyberTextSecondary, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = job.originalLanguageText,
                                style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary, fontSize = 12.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "تاريخ الرصد: $formattedDate",
                        style = MaterialTheme.typography.labelSmall.copy(color = CyberTextSecondary.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(color = CyberTextSecondary, lineHeight = 20.sp),
            modifier = Modifier.padding(start = 22.dp)
        )
    }
}
