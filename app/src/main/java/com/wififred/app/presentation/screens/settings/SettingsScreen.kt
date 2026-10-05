package com.wififred.app.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.wififred.app.presentation.navigation.Screen
import com.wififred.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات", color = TextPrimary) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle("الأمان")
            SettingSwitch(
                icon = Icons.Filled.Refresh,
                title = "إعادة الاتصال التلقائي",
                subtitle = "الاتصال بخادم احتياطي عند الانقطاع",
                checked = state.autoReconnect,
                onToggle = { viewModel.toggleAutoReconnect() }
            )
            SettingSwitch(
                icon = Icons.Filled.Security,
                title = "مفتاح الإيقاف (Kill Switch)",
                subtitle = "قطع الإنترنت تماماً عند فقدان النفق",
                checked = state.killSwitch,
                onToggle = { viewModel.toggleKillSwitch() }
            )
            SettingSwitch(
                icon = Icons.Filled.VisibilityOff,
                title = "تعتيم حركة المرور",
                subtitle = "إخفاء التوقيع الرقمي للنفق (Obfuscation)",
                checked = state.obfuscation,
                onToggle = { viewModel.toggleObfuscation() }
            )
            SettingSwitch(
                icon = Icons.Filled.Dns,
                title = "DNS Tunneling",
                subtitle = "تجاوز جدران الحماية عبر نفق DNS",
                checked = state.dnsTunneling,
                onToggle = { viewModel.toggleDnsTunneling() }
            )

            Spacer(Modifier.height(8.dp))
            SectionTitle("الخصوصية")
            SettingSwitch(
                icon = Icons.Filled.Block,
                title = "حجب الإعلانات",
                subtitle = "فلترة الإعلانات على مستوى DNS",
                checked = state.blockAds,
                onToggle = { viewModel.toggleBlockAds() }
            )

            Spacer(Modifier.height(8.dp))
            SectionTitle("روابط سريعة")
            SettingLink(
                icon = Icons.Filled.Terminal,
                title = "سجل الأحداث",
                onClick = { navController.navigate(Screen.Logs.route) }
            )
            SettingLink(
                icon = Icons.Filled.Apps,
                title = "توجيه التطبيقات",
                onClick = { navController.navigate(Screen.Split.route) }
            )

            Spacer(Modifier.height(24.dp))
            Text(
                text = "Wififred v1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = CyanPrimary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CyanPrimary)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyanPrimary,
                checkedTrackColor = CyanPrimary.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun SettingLink(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CyanPrimary)
        Spacer(Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary,
            modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
    Spacer(Modifier.height(4.dp))
}
