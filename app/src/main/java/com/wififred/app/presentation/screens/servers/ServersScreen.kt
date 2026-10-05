package com.wififred.app.presentation.screens.servers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wififred.app.domain.model.ServerConfig
import com.wififred.app.domain.model.VpnProtocol
import com.wififred.app.presentation.components.EmptyState
import com.wififred.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersScreen(viewModel: ServersViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الخوادم", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = CyanPrimary,
                contentColor = DarkBackground
            ) { Icon(Icons.Filled.Add, contentDescription = "إضافة خادم") }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (state.servers.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState("لا توجد خوادم — أضف خادماً للبدء")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.servers, key = { it.id }) { server ->
                    ServerCard(server, viewModel)
                }
            }
        }
    }

    if (showDialog) {
        AddServerDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, host, port, proto, user, pass ->
                viewModel.addServer(name, host, port, proto, user, pass)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ServerCard(server: ServerConfig, viewModel: ServersViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Dns, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(32.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(server.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                if (server.isDefault) {
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Filled.Star, contentDescription = null, tint = OrangeWarn, modifier = Modifier.size(16.dp))
                }
            }
            Text("${server.host}:${server.port}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(server.protocol.name, style = MaterialTheme.typography.labelSmall, color = CyanPrimary)
        }
        IconButton(onClick = { viewModel.setDefault(server.id) }) {
            Icon(Icons.Filled.StarBorder, contentDescription = "افتراضي", tint = TextSecondary)
        }
        IconButton(onClick = { viewModel.deleteServer(server.id) }) {
            Icon(Icons.Filled.Delete, contentDescription = "حذف", tint = RedOffline)
        }
    }
}

@Composable
private fun AddServerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, VpnProtocol, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("22") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var protocol by remember { mutableStateOf(VpnProtocol.SSH) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("إضافة خادم جديد", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("الاسم") }, singleLine = true)
                OutlinedTextField(value = host, onValueChange = { host = it },
                    label = { Text("العنوان (IP/Domain)") }, singleLine = true)
                OutlinedTextField(value = port, onValueChange = { port = it.filter { c -> c.isDigit() } },
                    label = { Text("المنفذ") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = username, onValueChange = { username = it },
                    label = { Text("اسم المستخدم") }, singleLine = true)
                OutlinedTextField(value = password, onValueChange = { password = it },
                    label = { Text("كلمة المرور") }, singleLine = true)
                Text("البروتوكول:", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VpnProtocol.values().take(3).forEach { p ->
                        FilterChip(selected = protocol == p, onClick = { protocol = p },
                            label = { Text(p.name, style = MaterialTheme.typography.labelSmall) })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(name, host, port.toIntOrNull() ?: 22, protocol, username, password)
            }) { Text("حفظ", color = CyanPrimary) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) } }
    )
}
