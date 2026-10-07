package com.wififred.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wififred.app.data.AppPreferences
import com.wififred.app.data.DnsServer
import com.wififred.app.data.LogStore
import com.wififred.app.presentation.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val scope = rememberCoroutineScope()

    val autoReconnect by prefs.autoReconnect.collectAsState(initial = true)
    val killSwitch by prefs.killSwitch.collectAsState(initial = false)
    val blockAds by prefs.blockAds.collectAsState(initial = true)
    val obfuscation by prefs.obfuscation.collectAsState(initial = false)

    var dnsRunning by remember { mutableStateOf(DnsServer.isRunning()) }
    var dnsQueries by remember { mutableStateOf(DnsServer.getQueryCount()) }
    var showDnsDialog by remember { mutableStateOf(false) }

    // تحديث عدد الاستعلامات كل ثانية
    LaunchedEffect(dnsRunning) {
        while (dnsRunning) {
            dnsQueries = DnsServer.getQueryCount()
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(
        Modifier.fillMaxSize().background(DarkBackground).verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopAppBar(
            title = { Text("الإعدادات", color = TextPrimary) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )

        Text("DNS Server محلي", color = CyanPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        // بطاقة DNS Server
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DarkSurface).padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (dnsRunning) Icons.Filled.Dns else Icons.Filled.Dns,
                    contentDescription = null,
                    tint = if (dnsRunning) GreenOnline else TextSecondary
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        if (dnsRunning) "DNS يعمل ✅" else "DNS متوقف",
                        color = if (dnsRunning) GreenOnline else TextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (dnsRunning) "المنفذ: ${DnsServer.getPort()} • استعلامات: $dnsQueries"
                        else "شغّل الخادم لاختبار DNS من أجهزة أخرى",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Switch(
                    checked = dnsRunning,
                    onCheckedChange = { isOn ->
                        scope.launch {
                            if (isOn) {
                                val result = DnsServer.start(5353)
                                dnsRunning = result.isSuccess
                            } else {
                                DnsServer.stop()
                                dnsRunning = false
                            }
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyanPrimary,
                        checkedTrackColor = CyanPrimary.copy(alpha = 0.3f)
                    )
                )
            }

            if (dnsRunning) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "اختبر من أي جهاز: nslookup wififred.local $(getDeviceIp())",
                    color = CyanPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(Modifier.height(6.dp))
                TextButton(
                    onClick = { showDnsDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("إضافة سجل DNS", color = CyanPrimary)
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("الأمان", color = CyanPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        ToggleRow(Icons.Filled.Refresh, "إعادة الاتصال التلقائي", "الاتصال بخادم احتياطي", autoReconnect) {
            scope.launch { prefs.setAutoReconnect(!autoReconnect); LogStore.info("تغيير: إعادة الاتصال") }
        }
        ToggleRow(Icons.Filled.Security, "Kill Switch", "قطع الإنترنت عند فقدان النفق", killSwitch) {
            scope.launch { prefs.setKillSwitch(!killSwitch); LogStore.info("تغيير: Kill Switch") }
        }
        ToggleRow(Icons.Filled.VisibilityOff, "Obfuscation", "إخفاء التوقيع الرقمي", obfuscation) {
            scope.launch { prefs.setObfuscation(!obfuscation); LogStore.info("تغيير: Obfuscation") }
        }
        ToggleRow(Icons.Filled.Block, "حجب الإعلانات", "فلترة DNS", blockAds) {
            scope.launch { prefs.setBlockAds(!blockAds); LogStore.info("تغيير: حجب الإعلانات") }
        }

        Spacer(Modifier.height(16.dp))
        Text("روابط سريعة", color = CyanPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        LinkCard("توجيه التطبيقات", Icons.Filled.Apps) { navController.navigate("split") }
        LinkCard("سجل الأحداث", Icons.Filled.List) { navController.navigate("logs") }

        Spacer(Modifier.height(20.dp))
        Text("Wififred v1.0.0", color = TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(20.dp))
    }

    if (showDnsDialog) {
        AddDnsRecordDialog(
            onDismiss = { showDnsDialog = false },
            onAdd = { domain, ip ->
                DnsServer.addLocalRecord(domain, ip)
                showDnsDialog = false
            }
        )
    }
}

@Composable
fun AddDnsRecordDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var domain by remember { mutableStateOf("") }
    var ip by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("إضافة سجل DNS", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = domain, onValueChange = { domain = it },
                    label = { Text("النطاق (مثال: test.local)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ip, onValueChange = { ip = it },
                    label = { Text("العنوان IP") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (domain.isNotBlank() && ip.isNotBlank()) onAdd(domain, ip)
            }) { Text("إضافة", color = CyanPrimary) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) } }
    )
}

@Composable
fun ToggleRow(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DarkSurface).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CyanPrimary)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = TextPrimary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary, checkedTrackColor = CyanPrimary.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun LinkCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = CyanPrimary)
            Spacer(Modifier.width(12.dp))
            Text(title, color = TextPrimary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}

fun getDeviceIp(): String {
    return try {
        java.net.NetworkInterface.getNetworkInterfaces().toList()
            .flatMap { it.inetAddresses.toList() }
            .firstOrNull { !it.isLoopbackAddress && it.hostAddress?.contains(":") == false }
            ?.hostAddress ?: "127.0.0.1"
    } catch (e: Exception) { "127.0.0.1" }
}
