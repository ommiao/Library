package cn.ommiao.library.theme.foundation

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class Spacing(private val dimen: Dp) {
    TwoExtraSmall(4.dp),
    ExtraSmall(8.dp),
    Small(12.dp),
    Medium(16.dp),
    Large(24.dp),
    ExtraLarge(32.dp),
    TwoExtraLarge(40.dp),
    ThreeExtraLarge(48.dp);

    fun dp(): Dp {
        return dimen
    }
}

@Composable
fun Spacing.Spacer() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(dp()))
}
