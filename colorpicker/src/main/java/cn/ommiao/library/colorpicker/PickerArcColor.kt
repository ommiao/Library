package cn.ommiao.library.colorpicker

import androidx.compose.ui.graphics.Color

data class PickerArcColor(
    val color: Color,
    val startAngle: Float,
    val sweepAngle: Float
) {
    companion object {
        val pickerArcColors = run {
            val colorWeights = listOf(
                Color(0xFFef5350) to 1f,
                Color(0xFFec407a) to 1f,
                Color(0xFFab47bc) to 1f,
                Color(0xFF7e57c2) to 1f,
                Color(0xFF5c6bc0) to 1f,
                Color(0xFF42a5f5) to 1f,
                Color(0xFF29b6f6) to 1f,
                Color(0xFF26c6da) to 1f,
                Color(0xFF26a69a) to 1f,
                Color(0xFF66bb6a) to 1f,
                Color(0xFF9ccc65) to 1f,
                Color(0xFFd4e157) to 1f
            )
            val dividerWeight = 0.25f
            val perColorSweepWithDividerAngle = 360f / colorWeights.sumOf { it.second.toLong() }
            val perColorSweepAngle = perColorSweepWithDividerAngle * (1 - dividerWeight)
            var colorStartAngle = -90f - perColorSweepAngle / 2
            colorWeights.map {
                PickerArcColor(
                    color = it.first,
                    startAngle = colorStartAngle,
                    sweepAngle = perColorSweepAngle
                ).also {
                    colorStartAngle -= perColorSweepWithDividerAngle
                }
            }
        }
    }
}
