package com.wififred.app.presentation.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wififred.app.presentation.components.SectionCard
import com.wififred.app.presentation.components.StatRow
import com.wififred.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val stats by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مراقبة الأداء", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SpeedCard("Ping", "${stats.pingMs} ms", CyanPrimary, Modifier.weight(1f))
                SpeedCard("Download", formatSpeed(stats.downloadKbps), GreenOnline, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SpeedCard("Upload", formatSpeed(stats.uploadKbps), OrangeWarn, Modifier.weight(1f))
                SpeedCard("المدة", formatDuration(stats.sessionDurationSeconds), PurpleAccent, Modifier.weight(1f))
            }

            SectionCard(title = "استهلاك البيانات") {
                StatRow("تم تنزيله", formatBytes(stats.totalDownloadedBytes), GreenOnline)
                StatRow("تم رفعه", formatBytes(stats.totalUploadedBytes), OrangeWarn)
                StatRow("الإجمالي", formatBytes(stats.totalDownloadedBytes + stats.totalUploadedBytes), CyanPrimary)
            }

            SectionCard(title = "حالة الجلسة") {
                StatRow("الحالة", "نشطة", GreenOnline)
                StatRow("المدة الكلية", formatDuration(stats.sessionDurationSeconds))
            }
        }
    }
}

@Composable
private fun SpeedCard(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
    }
}

private fun formatSpeed(kbps: Double): String =
    if (kbps > 1000) "%.2f Mbps".format(kbps / 1000) else "%.0f Kbps".format(kbps)

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_073_741_824 -> "%.2f GB".format(bytes / 1_073_741_824.0)
    bytes >= 1_048_576 -> "%.2f MB".format(bytes / 1_048_576.0)
    bytes >= 1024 -> "%.2f KB".format(bytes / 1024.0)
    else -> "$bytes B"
}

private fun formatDuration(sec: Long): String {
    val h = sec / 3600; val m = (sec % 3600) / 60; val s = sec % 60
    return "%02d:%02d:%02d".format(h, m, s)
}
