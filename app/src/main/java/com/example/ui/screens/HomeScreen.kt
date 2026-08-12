package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.components.JobCard
import com.example.ui.components.StatusBanner
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val statusText by viewModel.statusMessage.collectAsStateWithLifecycle()
    val channelCount by viewModel.allChannels.collectAsStateWithLifecycle()
    val totalJobCount by viewModel.jobCount.collectAsStateWithLifecycle()
    val jobs by viewModel.filteredJobs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val categories = listOf("الكل", "المفضلات", "برمجة وتطوير", "تصميم وUI/UX", "تسويق وكتابة", "عن بُعد / عام")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Hero Header & App Branding Card
        item {
            CardHeader()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Live Status Banner
        item {
            StatusBanner(
                connectionState = connectionState,
                statusText = statusText,
                channelCount = channelCount.size,
                jobCount = totalJobCount,
                onToggleScraper = { viewModel.toggleScraperState() },
                onTriggerSample = { viewModel.triggerSampleMessage() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Search Bar & Filter Section
        item {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("ابحث في الوظائف، الميزانية، المهارات المطلوب...", color = CyberTextSecondary) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyberPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح", tint = CyberTextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberSurfaceVariant,
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Categories horizontal scroll
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedCategory(category) },
                            label = {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberPrimary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = CyberSurface,
                                labelColor = CyberTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = CyberSurfaceVariant,
                                selectedBorderColor = CyberPrimary
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "عروض الوظائف المتاحة (${jobs.size})",
                        style = MaterialTheme.typography.titleMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                    )

                    if (jobs.isNotEmpty()) {
                        Text(
                            text = "محللة بالذكاء الاصطناعي ✨",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberGold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Job Offers List or Empty State
        if (jobs.isEmpty()) {
            item {
                EmptyJobsView(
                    isSearchActive = searchQuery.isNotBlank() || selectedCategory != "الكل",
                    onClearFilter = {
                        viewModel.setSearchQuery("")
                        viewModel.setSelectedCategory("الكل")
                    },
                    onTriggerSample = { viewModel.triggerSampleMessage() }
                )
            }
        } else {
            items(jobs, key = { it.id }) { job ->
                JobCard(
                    job = job,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onDeleteJob = { viewModel.deleteJob(it) }
                )
            }
        }
    }
}

@Composable
private fun CardHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = CyberSurface
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.telejob_hero_banner_1786563213919),
                contentDescription = "Header Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.35f
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WorkHistory,
                        contentDescription = null,
                        tint = CyberPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تلي جوب AI - رصد الوظائف المباشر",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = CyberTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "استخراج وتحليل فرص العمل عن بُعد من قنوات تلغرام تلقائياً بذكاء Gemini",
                    style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                )
            }
        }
    }
}

@Composable
private fun EmptyJobsView(
    isSearchActive: Boolean,
    onClearFilter: () -> Unit,
    onTriggerSample: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(16.dp),
        color = CyberSurface.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = CyberTextSecondary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isSearchActive) "لا توجد وظائف تطابق نتائج البحث" else "لم يتم رصد وظائف جديدة بعد",
                style = MaterialTheme.typography.titleMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isSearchActive) "جرب تغيير كلمات البحث أو اختيار تصنيف آخر" else "سيعمل المحرك في الخلفية ويستخرج الوظائف فور نشرها بقنوات تلغرام",
                style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isSearchActive) {
                androidx.compose.material3.OutlinedButton(onClick = onClearFilter) {
                    Text("عرض كل الوظائف", color = CyberPrimary)
                }
            } else {
                androidx.compose.material3.Button(
                    onClick = onTriggerSample,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                ) {
                    Text("تجربة توليد وظيفة فورية ✨", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
