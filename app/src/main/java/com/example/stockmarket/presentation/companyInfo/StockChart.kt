package com.example.stockmarket.presentation.companyInfo

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.IntraDayInfo
import com.example.stockmarket.ui.theme.Orange
import kotlin.math.round
import kotlin.math.roundToInt

private const val GRAPH_BACKGROUND_COLOR_TRANSPARENCY = 0.7f
private const val Y_VALUE_AXIS_WIDTH = 30f
private const val X_TIME_AXIS_HEIGHT = 5f
private const val Y_AXIS_VALUES_COUNTER = 5f
private const val SPACING = 100f

@Composable
fun StockChart(
    modifier: Modifier = Modifier,
    infoList: List<IntraDayInfo> = emptyList(),
    graphColor: Color = Orange
) {
    val transparentGraphColor = remember {
        graphColor.copy(alpha = GRAPH_BACKGROUND_COLOR_TRANSPARENCY)
    }
    val upperValue = remember(infoList) {
        (infoList.maxOfOrNull { it.close }?.plus(1))?.roundToInt() ?: 0
    }
    val lowerValue = remember(infoList) {
        infoList.minOfOrNull { it.close }?.toInt() ?: 0
    }
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }
    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - SPACING) / infoList.size

        drawHours(infoList, textPaint, spacePerHour)
        drawValues(upperValue, lowerValue, textPaint)

        var lastX = 0f
        val strokePath = drawMainPath(infoList, lowerValue, upperValue, spacePerHour) { lastX = it }
        val fillPath = android.graphics.Path(strokePath.asAndroidPath()).asComposePath().apply {
            lineTo(lastX, size.height - SPACING)
            lineTo(SPACING, size.height - SPACING)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(transparentGraphColor, Color.Transparent),
                endY = size.height - SPACING
            )
        )

        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }

}

private fun DrawScope.drawMainPath(infoList: List<IntraDayInfo>, lowerValue: Int, upperValue: Int, spacePerHour: Float, lastX: (Float) -> Unit): Path {
    return Path().apply {
        for (index in infoList.indices) {
            val info = infoList[index]
            val nextInfo = infoList.getOrNull(index + 1) ?: infoList.last()
            val leftRatio = (info.close - lowerValue) / (upperValue - lowerValue)
            val rightRatio = (nextInfo.close - lowerValue) / (upperValue - lowerValue)

            val x1 = SPACING + index * spacePerHour
            val y1 = size.height - SPACING - (leftRatio * size.height).toFloat()
            val x2 = SPACING + (index + 1) * spacePerHour
            val y2 = size.height - SPACING - (rightRatio * size.height).toFloat()

            if (index == 0) moveTo(x1, y1)

            lastX((x1 + x2) / 2)
            quadraticBezierTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2f)
        }
    }
}


private fun DrawScope.drawValues(upperValue: Int, lowerValue: Int, textPaint: Paint) {
    val priceStep = (upperValue - lowerValue) / Y_AXIS_VALUES_COUNTER
    (0..4).forEach { index ->
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                round(lowerValue + priceStep * index).toString(),
                Y_VALUE_AXIS_WIDTH,
                size.height - SPACING - index * size.height / Y_AXIS_VALUES_COUNTER,
                textPaint
            )
        }
    }
}

private fun DrawScope.drawHours(infoList: List<IntraDayInfo>, textPaint: Paint, spacePerHour: Float) {
    (0 until infoList.size - 1 step 2).forEach { index ->
        val info = infoList[index]
        val hour = info.date.hour
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                hour.toString(),
                SPACING + index * spacePerHour,
                size.height - X_TIME_AXIS_HEIGHT,
                textPaint
            )
        }
    }
}
