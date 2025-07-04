package com.uniguard.trackable.presentation.screens.rnw

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uniguard.trackable.presentation.components.DropdownSelector
import com.uniguard.trackable.presentation.screens.uhf.viewmodel.UhfViewModel
import com.uniguard.trackable.utils.OtgUtils

@Composable
fun ReadWriteScreen(
    viewModel: UhfViewModel = hiltViewModel()
) {
    val selectedEpc by viewModel.selectedEpc.collectAsState()
    var memBank by remember { mutableIntStateOf(1) }
    var wordPtr by remember { mutableStateOf("2") }
    var length by remember { mutableStateOf("6") }
    var password by remember { mutableStateOf("00000000") }
    var writeContent by remember { mutableStateOf("A1B2C3D4E5F6A7B8C9D0E1F2") }
    var readResult by remember { mutableStateOf<String?>(null) }
    var operationResult by remember { mutableStateOf<String?>(null) }

    val memOptions = listOf("RESERVED", "EPC", "TID", "USER")

    val scrollState = rememberScrollState()

    var showPaddingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        OtgUtils.setPOGOPINEnable(true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Text("🔐 EPC Selected", style = MaterialTheme.typography.labelLarge)
        Text(
            text = selectedEpc ?: "-",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Blue
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("📦 Memory Configuration", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        DropdownSelector(
            label = "Memory Bank",
            options = memOptions,
            selectedIndex = memBank,
            onSelected = { memBank = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = wordPtr,
                onValueChange = { wordPtr = it },
                label = { Text("WordPtr") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = length,
                onValueChange = { length = it },
                label = { Text("Length") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Access Password") },
            placeholder = { Text("Default: 00000000") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("📝 Data Operations", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = writeContent,
            onValueChange = { writeContent = it },
            label = { Text("Write Content (Hex)") },
            placeholder = { Text("Ex: A1B2C3D4...") },
            supportingText = {
                if (writeContent.length % 4 != 0) {
                    Text("⚠️ Must be even length (4 chars = 1 word)", color = Color.Red)
                }
            },
            isError = writeContent.length % 4 != 0,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    val epc = selectedEpc ?: return@Button
                    viewModel.readFromTag(
                        epc = epc,
                        memBank = memBank,
                        wordPtr = wordPtr.toIntOrNull() ?: 0,
                        length = length.toIntOrNull() ?: 6,
                        password = password
                    ) {
                        it.onSuccess { result ->
                            readResult = result
                            operationResult = "✅ Read Success"
                        }.onFailure { error ->
                            readResult = null
                            operationResult = "❌ Read Failed: ${error.message}"
                        }
                    }
                },
                enabled = selectedEpc != null
            ) {
                Text("🔍 Read")
            }

            Button(
                onClick = {
                    val epc = selectedEpc ?: return@Button

                    when {
                        writeContent.length > 24 -> {
                            operationResult = "❌ Content too long. Max 24 hex chars (12 words)"
                        }

                        writeContent.length < 24 -> {
                            showPaddingDialog = true
                        }

                        else -> {
                            viewModel.writeToTag(
                                epc = epc,
                                memBank = memBank,
                                wordPtr = wordPtr.toIntOrNull() ?: 0,
                                password = password,
                                content = writeContent
                            ) {
                                it.onSuccess {
                                    if (memBank == 1 && (wordPtr.toIntOrNull() == 2)) {
                                        viewModel.selectEpc(writeContent)
                                    }
                                    operationResult = "✅ Write Success"
                                }.onFailure { error ->
                                    operationResult = "❌ Write Failed: ${error.message}"
                                }
                            }
                        }
                    }
                },
                enabled = selectedEpc != null
            ) {
                Text("✏️ Write")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        readResult?.let {
            Text("📖 Read Result: $it", color = Color(0xFF3F51B5))
        }

        operationResult?.let {
            val color = if (it.startsWith("✅")) Color(0xFF2E7D32) else Color(0xFFD32F2F)
            Text(it, color = color)
        }

        if (showPaddingDialog) {
            AlertDialog(
                onDismissRequest = { showPaddingDialog = false },
                title = { Text("Incomplete Data") },
                text = {
                    val missing = 24 - writeContent.length
                    Text("Content is shorter than 24 characters.\nAdd ${missing} zero(s) (0) to fill the remaining?")
                },
                confirmButton = {
                    TextButton(onClick = {
                        val epc = selectedEpc ?: return@TextButton

                        val padded = writeContent.padEnd(24, '0')
                        viewModel.writeToTag(
                            epc = epc,
                            memBank = memBank,
                            wordPtr = wordPtr.toIntOrNull() ?: 0,
                            password = password,
                            content = padded
                        ) {
                            it.onSuccess {
                                if (memBank == 1 && (wordPtr.toIntOrNull() == 2)) {
                                    viewModel.selectEpc(padded)
                                }
                                writeContent = padded
                                operationResult = "✅ Write Success"
                            }.onFailure { error ->
                                operationResult = "❌ Write Failed: ${error.message}"
                            }
                        }
                        showPaddingDialog = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPaddingDialog = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
