package com.uniguard.trackable.presentation.screens.uhf

import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.uniguard.trackable.domain.uhf.model.InventoryTag
import com.uniguard.trackable.presentation.screens.uhf.viewmodel.UhfViewModel
import com.uniguard.trackable.utils.Reader

@Composable
fun UhfScreen(
    viewModel: UhfViewModel = hiltViewModel(),
    onNavigateToFind: () -> Unit = {},
    onNavigateToRnW: () -> Unit = {}
) {

    val context = LocalContext.current

    val isConnected by viewModel.isConnected.collectAsState()
    val inventoryRunning by viewModel.inventoryRunning.collectAsState()
    val inventoryMode by viewModel.inventoryMode.collectAsState()
    val enableLed by viewModel.enableLed.collectAsState()
    val tagList by viewModel.tagList.collectAsState()

    var showPopupMenu by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf<InventoryTag?>(null) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    Reader.setOpenScan523(false)
                }

                Lifecycle.Event.ON_PAUSE -> {
                    Reader.setOpenScan523(true)
                }

                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.disconnect()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            Reader.setOpenScan523(true)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .onKeyEvent { event ->
                if (!isConnected){
                    viewModel.connectReader(57600)
                    return@onKeyEvent false
                }

                val keyCode = event.nativeKeyEvent.keyCode
                val isKeyDown = event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN

                if (keyCode in 520..523 && isKeyDown) {
                    if (inventoryRunning) viewModel.stopInventory()
                    else viewModel.startInventory()
                    return@onKeyEvent true
                }

                return@onKeyEvent false
            }
            .focusRequester(focusRequester)
            .focusable()
    ) {
        Text("UHF Reader", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        // ⬆️ Connection Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.connectReader(57600) },
                enabled = !isConnected,
                modifier = Modifier.weight(1f)
            ) {
                Text("Connect", maxLines = 1)
            }

            Button(
                onClick = {
                    if (inventoryRunning) viewModel.stopInventory()
                    else viewModel.startInventory()
                },
                enabled = isConnected,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (inventoryRunning) "Stop" else "Start", maxLines = 1)
            }

            OutlinedButton(
                onClick = viewModel::clearTags,
                enabled = isConnected,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear", maxLines = 1)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🎛️ Modes and LED
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = inventoryMode == 0,
                onClick = {
                    if (inventoryRunning) viewModel.stopInventory()
                    viewModel.setInventoryMode(0)
                },
                enabled = isConnected
            )
            Text("Single", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = inventoryMode == 1,
                onClick = {
                    if (inventoryRunning) viewModel.stopInventory()
                    viewModel.setInventoryMode(1)
                },
                enabled = isConnected
            )
            Text("Loop")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = enableLed,
                onCheckedChange = viewModel::setEnableLed,
                enabled = isConnected
            )
            Text("Enable LED")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🏷️ Tag List
        Text("Detected Tags: ${tagList.size}", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tagList) { tag ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Long-press to show actions",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onLongClick = {
                                selectedTag = tag
                                showPopupMenu = true
                            }
                        ),
                    elevation = CardDefaults.elevatedCardElevation()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("EPC: ${tag.epc}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Count: ${tag.count}", style = MaterialTheme.typography.labelSmall)
                            Text("Strength: ${tag.rssi.toFloat().coerceIn(0f, 100f).toInt()}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // 📋 Popup Actions
        if (showPopupMenu && selectedTag != null) {
            AlertDialog(
                onDismissRequest = {
                    showPopupMenu = false
                    selectedTag = null
                },
                title = { Text("Tag Actions") },
                text = {
                    Column {
                        Text("EPC: ${selectedTag!!.epc}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        TextButton(onClick = {
                            viewModel.selectEpc(selectedTag!!.epc)
                            showPopupMenu = false
                            onNavigateToFind()
                        }) { Text("🔍 Find Tag") }

                        TextButton(onClick = {
                            viewModel.selectEpc(selectedTag!!.epc)
                            showPopupMenu = false
                            onNavigateToRnW()
                        }) { Text("📖 Read & Write") }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = {
                        showPopupMenu = false
                        selectedTag = null
                    }) { Text("Cancel") }
                }
            )
        }
    }
}
