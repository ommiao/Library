package cn.ommiao.library.theme.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.ommiao.library.theme.Appearance

private val CardElevation = 3.dp
private val CardRadius = 16.dp

@Composable
fun Card(
    modifier: Modifier,
    backgroundColor: Color = Appearance.colors.backgroundPrimaryLow,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        backgroundColor = backgroundColor,
        content = content,
        elevation = CardElevation,
        shape = RoundedCornerShape(CardRadius)
    )
}
