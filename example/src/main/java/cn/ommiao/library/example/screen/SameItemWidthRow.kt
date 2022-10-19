package cn.ommiao.library.example.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import cn.ommiao.library.theme.Appearance
import cn.ommiao.library.theme.foundation.Spacing

@Composable
fun SameItemWidthRowScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Appearance.colors.backgroundPrimary)
    ) {
        SameItemWidthRow(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium.dp())
        ) { maxItemSize ->
            Button(modifier = Modifier.width(maxItemSize), onClick = { }) {
                Text(text = "Short")
            }
            Button(modifier = Modifier.width(maxItemSize), onClick = { }) {
                Text(text = "Loooooong")
            }
        }
    }
}

@Composable
private fun SameItemWidthRow(
    modifier: Modifier,
    horizontalArrangement: Arrangement.Horizontal,
    content: @Composable (maxItemWidth: Dp) -> Unit
) {
    SubcomposeLayout(modifier) { constraints ->
        val maxItemWidth =
            subcompose("viewToMeasure") {
                content(Dp.Unspecified)
            }.maxOfOrNull {
                it.measure(Constraints()).width.toDp()
            } ?: Dp.Unspecified

        val contentPlaceable = subcompose("content") {
            Row(
                horizontalArrangement = horizontalArrangement
            ) {
                content(maxItemWidth)
            }
        }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}
