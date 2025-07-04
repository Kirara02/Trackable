package com.uniguard.trackable.presentation.screens.find

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uniguard.trackable.presentation.components.CircleProgressBar
import com.uniguard.trackable.presentation.screens.uhf.viewmodel.UhfViewModel

@Composable
fun FindScreen() {
    val viewModel: UhfViewModel = hiltViewModel()

    val tagEpc by viewModel.selectedEpc.collectAsState()
    val rssi by viewModel.rssi.collectAsState()
    val focusRequester = remember { FocusRequester() }
    var isFinding by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    fun toggleFinding() {
        if (isFinding) {
            viewModel.stopFinding()
        } else {
            viewModel.startFinding()
        }
        isFinding = !isFinding
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .onKeyEvent { event ->
                if (event.nativeKeyEvent.keyCode == 523 &&
                    event.nativeKeyEvent.action == android.view.KeyEvent.ACTION_DOWN
                ) {
                    toggleFinding()
                    true
                } else false
            }
            .focusRequester(focusRequester)
            .focusable(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tag EPC",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = tagEpc ?: "No tag selected",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            color = if (tagEpc != null) Color.Black else Color.Gray
        )

        // 🎯 Progress bar
        CircleProgressBar(
            value = rssi.toFloat().coerceIn(0f, 100f),
            maxValue = 100f,
            hintText = "Signal",
            unitText = "Strength",
            modifier = Modifier.size(240.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 🕹️ Action button
        Button(
            onClick = { toggleFinding() },
            enabled = tagEpc != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                if (isFinding) "🔍 Stop Finding" else "🎯 Start Finding",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Press side scan key (KEYCODE 523) to toggle",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
