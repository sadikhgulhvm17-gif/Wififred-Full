package com.wififred.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wififred.app.data.LogStore
import com.wififred.app.data.ServerStore
import com.wififred.app.data.SshManager
import com.wififred.app.presentation.navigation.Screen
import com.wififred.app.presentation.theme.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController) {
    var connected by remember { mutableStateOf(false) }
    var connecting by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("غير متصل") }
    val servers by ServerStore.servers.collectAsState()
    val defaultServer = servers.find { it.isDefault }
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().background(DarkBackground).verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Wififred", style = MaterialTheme.typography.headlineLarge, color = CyanPrimary, fontWeight = FontWeight.Bold)
        Text("حرية اتصال مشفّرة", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(40.dp))

        Box(
            Modifier.size(200.dp).background(
                when {
                    connected -> GreenOnline.copy(alpha = 0.15f)
                    connecting -> OrangeWarn.copy(alpha = 0.15f)
                    else -> RedOffline.copy(alpha = 0.15f)
                }, CircleShape
            ),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    if (connecting) return@Button
                    if (connected) {
                        scope.launch {
                            SshManager.disconnect()
                            connected = false
                            statusMessage = "غير متصل"
                        }
                    } else {
                        val server = defaultServer
                        if (server == null) {
                            LogStore.error("لا يوجد خادم افتراضي — أضف خادماً أولاً")
                            statusMessage = "أضف خادماً أولاً"
                        } else {
                            scope.launch {
                                connecting = true
                                statusMessage = "جارٍ الاتصال..."
                                val result = SshManager.connect(
                                    host = server.host,
                                    port = server.port,
                                    username = server.username.ifBlank { "root" },
                                    password = server.password
                                )
                                connecting = false
                                if (result.isSuccess) {
                                    connected = true
                                    statusMessage = "متصل: ${server.name}"
                                } else {
                                    connected = false
                                    statusMessage = "فشل: ${result.exceptionOrNull()?.message ?: "خطأ"}"
                                }
                            }
                        }
                    }
                },
                enabled = !connecting,
                modifier = Modifier.size(150.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        connected -> GreenOnline.copy(alpha = 0.25f)
                        connecting -> OrangeWarn.copy(alpha = 0.25f)
                        else -> RedOffline.copy(alpha = 0.25f)
                    },
                    contentColor = when {
                        connected -> GreenOnline
                        connecting -> OrangeWarn
                        else -> RedOffline
                    }
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (connecting) {
                        CircularProgressIndicator(color = OrangeWarn, modifier = Modifier.size(40.dp), strokeWidth = 3.dp)
                    } else {
                        Icon(
                            if (connected) Icons.Filled.Lock else Icons.Filled.LockOpen,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(if (connected) "قطع" else "اتصال", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            statusMessage,
            color = when {
                connected -> GreenOnline
                connecting -> OrangeWarn
                else -> RedOffline
            },
            style = MaterialTheme.typography.titleLarge
        )

        if (!connecting && !connected && defaultServer == null) {
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = OrangeWarn.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = OrangeWarn)
                    Spacer(Modifier.width(8.dp))
                    Text("أضف خادماً من الإعدادات أولاً", color = OrangeWarn, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
            Column(Modifier.padding(16.dp)) {
                Text("نظرة سريعة", color = CyanPrimary, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(10.dp))
                InfoRow("الحالة", if (connected) "متصل" else if (connecting) "جارٍ الاتصال" else "غير متصل")
                InfoRow("الخادم النشط", defaultServer?.name ?: "لم يُحدد")
                InfoRow("العنوان", defaultServer?.let { "${it.host}:${it.port}" } ?: "—")
                InfoRow("المستخدم", defaultServer?.username?.ifBlank { "—" } ?: "—")
                InfoRow("عدد الخوادم", "${servers.size}")
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickButton("WiFi", Icons.Filled.Wifi, Modifier.weight(1f)) { navController.navigate(Screen.Wifi.route) }
            QuickButton("الخوادم", Icons.Filled.Storage, Modifier.weight(1f)) { navController.navigate(Screen.Servers.route) }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickButton("توجيه", Icons.Filled.Apps, Modifier.weight(1f)) { navController.navigate("split") }
            QuickButton("السجلات", Icons.Filled.List, Modifier.weight(1f)) { navController.navigate("logs") }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun QuickButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier.height(90.dp), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = label, tint = CyanPrimary)
            Spacer(Modifier.height(8.dp))
            Text(label, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
