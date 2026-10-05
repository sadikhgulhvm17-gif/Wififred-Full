package com.wififred.app.presentation.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.wififred.app.domain.model.ConnectionState
import com.wififred.app.presentation.components.SectionCard
import com.wififred.app.presentation.components.StatRow
import com.wififred.app.presentation.components.StatusBadge
import com.wififred.app.presentation.navigation.Screen
import com.wififred.app.presentation.theme.*

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        // شعار
        Text(
            text = "Wififred",
            style = MaterialTheme.typography.headlineLarge,
            color = CyanPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "حرية اتصال مشفّرة",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(Modifier.height(40.dp))

        // زر الاتصال الكبير
        ConnectButton(
            state = state.connectionState,
            onClick = { viewModel.toggleConnection() }
        )

        Spacer(Modifier.height(24.dp))

        // حالة الاتصال
        val (statusText, statusColor) = when (state.connectionState) {
            is ConnectionState.Connected -> "متصل" to GreenOnline
            is ConnectionState.Connecting -> "جارٍ الاتصال..." to OrangeWarn
            is ConnectionState.Reconnecting -> "إعادة الاتصال..." to OrangeWarn
            is ConnectionState.Error -> "خطأ" to RedOffline
            else -> "غير متصل" to RedOffline
        }
        StatusBadge(text = statusText, color = statusColor)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "الخادم: ${state.activeServerName}",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )

        Spacer(Modifier.height(32.dp))

        // بطاقة معلومات سريعة
        SectionCard(title = "نظرة سريعة") {
            StatRow("الحالة", statusText, statusColor)
            StatRow("الخادم النشط", state.activeServerName)
            StatRow("عدد الخوادم", "${if (state.hasServers) "1+" else "0"}")
        }

        Spacer(Modifier.height(16.dp))

        // أزرار سريعة
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                icon = Icons.Filled.Wifi,
                label = "فحص WiFi",
                modifier = Modifier.weight(1f)
            ) { navController.navigate(Screen.Wifi.route) }

            QuickActionButton(
                icon = Icons.Filled.Apps,
                label = "التطبيقات",
                modifier = Modifier.weight(1f)
            ) { navController.navigate(Screen.Split.route) }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                icon = Icons.Filled.Speed,
                label = "الأداء",
                modifier = Modifier.weight(1f)
            ) { navController.navigate(Screen.Stats.route) }

            QuickActionButton(
                icon = Icons.Filled.Terminal,
                label = "السجلات",
                modifier = Modifier.weight(1f)
            ) { navController.navigate(Screen.Logs.route) }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ConnectButton(
    state: ConnectionState,
    onClick: () -> Unit
) {
    val isConnected = state is ConnectionState.Connected
    val isBusy = state is ConnectionState.Connecting || state is ConnectionState.Reconnecting

    val targetColor = when {
        isConnected -> GreenOnline
        isBusy -> OrangeWarn
        else -> RedOffline
    }
    val animatedColor by animateColorAsState(targetColor, label = "btnColor")

    // نبض للزر عند الاتصال
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnected) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .scale(pulseScale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(animatedColor.copy(alpha = 0.3f), Color.Transparent)
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            enabled = !isBusy,
            modifier = Modifier.size(150.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = animatedColor.copy(alpha = 0.15f),
                contentColor = animatedColor,
                disabledContainerColor = animatedColor.copy(alpha = 0.1f),
                disabledContentColor = animatedColor
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isBusy) {
                    CircularProgressIndicator(
                        color = animatedColor,
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(
                        imageVector = if (isConnected) Icons.Filled.Lock else Icons.Filled.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (isConnected) "إيقاف" else "اتصال",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(90.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = CyanPrimary)
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
    }
}
