package com.uniguard.trackable.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircleProgressBar(
    modifier: Modifier = Modifier,
    value: Float,
    maxValue: Float = 100f,
    precision: Int = 0,
    hintText: String = "",
    unitText: String = "",
    arcColor: Color = Color.Blue,
    backgroundColor: Color = Color.LightGray,
    arcWidth: Dp = 20.dp,
    textStyle: TextStyle = TextStyle.Default,
    valueTextColor: Color = Color.Black,
    unitTextColor: Color = Color.Gray,
    hintTextColor: Color = Color.Gray,
    animationDuration: Int = 500,
    useGradient: Boolean = true,
    gradientColors: List<Color> = listOf(Color.Blue, Color.Cyan)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = value.coerceIn(0f, maxValue) / maxValue,
        animationSpec = tween(durationMillis = animationDuration),
        label = "progress"
    )

    val formattedValue = remember(value, precision) {
        "%.${precision}f".format(value.coerceIn(0f, maxValue))
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size
            val side = canvasSize.minDimension
            val stroke = arcWidth.toPx()
            val radius = side / 2 - stroke / 2
            val center = Offset(canvasSize.width / 2, canvasSize.height / 2)

            // 🌀 Draw background arc
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )

            // 🌈 Gradient arc
            val brush = if (useGradient)
                Brush.linearGradient(gradientColors)
            else
                SolidColor(arcColor)

            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
        }

        // 📊 Text content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formattedValue,
                style = textStyle.copy(color = valueTextColor, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            )

            if (unitText.isNotBlank()) {
                Text(text = unitText, color = unitTextColor, fontSize = 14.sp)
            }

            if (hintText.isNotBlank()) {
                Text(text = hintText, color = hintTextColor, fontSize = 12.sp)
            }
        }
    }
}
