package com.uniguard.trackable.presentation.screens.scanner

import android.view.KeyEvent
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.uniguard.trackable.presentation.screens.scanner.viewmodel.ScannerViewModel

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val resultText by viewModel.scanResult.collectAsState()
    val scanBitmap by viewModel.scanBitmap.collectAsState()

    val colorPrimary = MaterialTheme.colorScheme.primary
    val colorSecondary = MaterialTheme.colorScheme.secondaryContainer

    val boxState = remember { mutableStateOf(TriggerButtonState(colorSecondary, "Tap to Scan")) }
    var isKeyDownHandled by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> viewModel.registerReceiver()
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> viewModel.unregisterReceiver()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onKeyEvent { keyEvent ->
                val keyCode = keyEvent.nativeKeyEvent.keyCode
                when (keyEvent.nativeKeyEvent.action) {
                    KeyEvent.ACTION_DOWN -> {
                        if (keyCode in 520..523 && !isKeyDownHandled) {
                            boxState.value = TriggerButtonState(colorPrimary, "Scanning...")
                            viewModel.startScan()
                            isKeyDownHandled = true
                            true
                        } else false
                    }

                    KeyEvent.ACTION_UP -> {
                        if (keyCode in 520..523) {
                            boxState.value = TriggerButtonState(colorSecondary, "Tap to Scan")
                            viewModel.stopScan()
                            isKeyDownHandled = false
                            true
                        } else false
                    }

                    else -> false
                }
            }
            .focusRequester(focusRequester)
            .focusable()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("📦 Scan Result", style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = resultText?.printable ?: "No data scanned yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text("🖼️ Scan Image", style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (scanBitmap != null) {
                    Image(
                        bitmap = scanBitmap!!.asImageBitmap(),
                        contentDescription = "Scan Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("No image available", color = Color.DarkGray)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        ScanTriggerButton(
            boxColor = boxState.value.color,
            boxText = boxState.value.text,
            onPress = {
                boxState.value = TriggerButtonState(colorPrimary, "Scanning...")
                viewModel.startScan()
            },
            onRelease = {
                boxState.value = TriggerButtonState(colorSecondary, "Tap to Scan")
                viewModel.stopScan()
            }
        )
    }
}

@Composable
fun ScanTriggerButton(
    boxColor: Color,
    boxText: String,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    Surface(
        color = boxColor,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onPress(); true
                    }
                    MotionEvent.ACTION_UP -> {
                        onRelease(); true
                    }
                    else -> false
                }
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = boxText, color = Color.White)
        }
    }
}

data class TriggerButtonState(val color: Color, val text: String)
