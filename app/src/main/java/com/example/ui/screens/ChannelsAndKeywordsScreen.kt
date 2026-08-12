package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.KeywordFilter
import com.example.data.MonitoredChannel
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
fun ChannelsAndKeywordsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val channels by viewModel.allChannels.collectAsStateWithLifecycle()
    val keywords by viewModel.allKeywords.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddChannelDialog by remember { mutableStateOf(false) }
    var showAddKeywordDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = CyberSurface,
            contentColor = CyberPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = CyberPrimary
                )
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("القنوات والمجموعات (${channels.size})", fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FilterAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("كلمات التصفية (${keywords.size})", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (selectedTabIndex == 0) {
                // Channels Tab
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "قنوات تلغرام المرصودة للوظائف",
                            style = MaterialTheme.typography.titleMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                        )

                        Button(
                            onClick = { showAddChannelDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إضافة قناة", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(channels, key = { it.id }) { channel ->
                    ChannelCard(
                        channel = channel,
                        onToggle = { viewModel.toggleChannel(channel) },
                        onDelete = { viewModel.deleteChannel(channel.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                // Keywords Tab
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الكلمات المفتاحية المستهدفة",
                            style = MaterialTheme.typography.titleMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                        )

                        Button(
                            onClick = { showAddKeywordDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إضافة كلمة", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "يتم تصفية رسائل القنوات تلقائياً للتأكد من احتوائها على إحدى هذه الكلمات قبل إرسالها لـ AI",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(keywords, key = { it.id }) { keyword ->
                    KeywordCard(
                        keyword = keyword,
                        onToggle = { viewModel.toggleKeyword(keyword) },
                        onDelete = { viewModel.deleteKeyword(keyword.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showAddChannelDialog) {
        AddChannelDialog(
            onDismiss = { showAddChannelDialog = false },
            onConfirm = { name, username ->
                viewModel.addChannel(name, username)
                showAddChannelDialog = false
            }
        )
    }

    if (showAddKeywordDialog) {
        AddKeywordDialog(
            onDismiss = { showAddKeywordDialog = false },
            onConfirm = { word ->
                viewModel.addKeyword(word)
                showAddKeywordDialog = false
            }
        )
    }
}

@Composable
private fun ChannelCard(
    channel: MonitoredChannel,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = CyberSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = channel.channelName,
                        style = MaterialTheme.typography.titleSmall.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${channel.usernameOrId} • ${channel.memberCount}",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary, fontSize = 12.sp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = channel.isListening,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = CyberTertiary,
                        uncheckedThumbColor = CyberTextSecondary,
                        uncheckedTrackColor = CyberSurfaceVariant
                    )
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف القناة",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeywordCard(
    keyword: KeywordFilter,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = null,
                    tint = CyberGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = keyword.keyword,
                    style = MaterialTheme.typography.titleSmall.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = keyword.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = CyberGold,
                        uncheckedThumbColor = CyberTextSecondary,
                        uncheckedTrackColor = CyberSurfaceVariant
                    )
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddChannelDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var channelName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة قناة تلغرام جديدة للرصد", color = CyberTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = channelName,
                    onValueChange = { channelName = it },
                    label = { Text("اسم القناة (مثل: وظائف البرمجة)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("معرف القناة أو الرابط (مثل @my_jobs_channel)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(channelName, username) },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
            ) {
                Text("إضافة", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = CyberTextSecondary)
            }
        },
        containerColor = CyberSurface
    )
}

@Composable
private fun AddKeywordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var word by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة كلمة مفتاحية جديدة", color = CyberTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = word,
                onValueChange = { word = it },
                label = { Text("الكلمة المستهدفة (مثل: مطلوب، $، remote)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(word) },
                colors = ButtonDefaults.buttonColors(containerColor = CyberGold)
            ) {
                Text("إضافة", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = CyberTextSecondary)
            }
        },
        containerColor = CyberSurface
    )
}
