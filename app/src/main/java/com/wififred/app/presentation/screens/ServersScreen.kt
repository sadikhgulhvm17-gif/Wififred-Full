package com.wififred.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.wififred.app.data.ServerModel
import com.wififred.app.data.ServerStore
import com.wififred.app.presentation.theme.*

@Composable
fun ServersScreen() {
    val servers by ServerStore.servers.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<ServerModel?>(null) }

    Box(Modifier.fillMaxSize().background(DarkBackground)) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("الخوادم (${servers.size})", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )

            if (servers.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.CloudOff, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("لا توجد خوادم", color = TextSecondary, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("اضغط زر + لإضافة خادم SSH", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(servers, key = { it.id }) { server ->
                        ServerCard(
                            server = server,
                            onSetDefault = { ServerStore.setDefault(server.id) },
                            onEdit = { editTarget = server },
                            onDelete = { ServerStore.remove(server.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { editTarget = null; showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            containerColor = CyanPrimary,
            contentColor = DarkBackground
        ) {
            Icon(Icons.Filled.Add, contentDescription = "إضافة")
        }
    }

    if (showDialog || editTarget != null) {
        ServerDialog(
            initial = editTarget,
            onDismiss = { showDialog = false; editTarget = null },
            onSave = { name, host, port, username, password ->
                val target = editTarget
                if (target != null) {
                    ServerStore.update(target.copy(
                        name = name, host = host, port = port,
                        username = username, password = password
                    ))
                } else {
                    ServerStore.add(name, host, port, "SSH", username, password)
                }
                showDialog = false
                editTarget = null
            }
        )
    }
}

@Composable
fun ServerCard(
    server: ServerModel,
    onSetDefault: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Storage,
                contentDescription = null,
                tint = if (server.isDefault) CyanPrimary else TextSecondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        server.name,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (server.isDefault) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(CyanPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("افتراضي", color = CyanPrimary, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Text("${server.host}:${server.port}", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                Text(
                    "${server.protocol} • ${server.username.ifBlank { "بدون مستخدم" }}",
                    color = GreenOnline,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Divider(color = DarkBackground)
        Spacer(Modifier.height(6.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            if (!server.isDefault) {
                TextButton(onClick = onSetDefault) {
                    Icon(Icons.Filled.StarBorder, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("افتراضي", color = CyanPrimary, style = MaterialTheme.typography.labelSmall)
                }
            } else {
                TextButton(onClick = {}) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("نشط", color = CyanPrimary, style = MaterialTheme.typography.labelSmall)
                }
            }
            TextButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = OrangeWarn, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("تعديل", color = OrangeWarn, style = MaterialTheme.typography.labelSmall)
            }
            TextButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = RedOffline, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("حذف", color = RedOffline, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun ServerDialog(
    initial: ServerModel?,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var host by remember { mutableStateOf(initial?.host ?: "") }
    var port by remember { mutableStateOf((initial?.port ?: 22).toString()) }
    var username by remember { mutableStateOf(initial?.username ?: "root") }
    var password by remember { mutableStateOf(initial?.password ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text(if (initial == null) "إضافة خادم SSH" else "تعديل الخادم", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("الاسم") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = host, onValueChange = { host = it },
                    label = { Text("العنوان (IP أو Domain)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = port, onValueChange = { port = it.filter { c -> c.isDigit() } },
                    label = { Text("المنفذ") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = username, onValueChange = { username = it },
                    label = { Text("اسم المستخدم") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("كلمة المرور") }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && host.isNotBlank()) {
                        onSave(name, host, port.toIntOrNull() ?: 22, username, password)
                    }
                }
            ) { Text("حفظ", color = CyanPrimary, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) }
        }
    )
}
