package cn.ommiao.library.overscroll

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class OverscrollNestedConnection(
    val scrollState: LazyListState,
    val maxOffsetY: Float,
    val onOverscrollHeightChange: (Float) -> Unit
) : NestedScrollConnection {

    private val mutatorMutex = MutatorMutex()

    private var animating = false

    private var offsetY = 0f

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (animating) {
            return available
        }
        if (source == NestedScrollSource.Drag && scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset == 0) {
            val newOffset = offsetY + available.y
            return when {
                newOffset in 0f..maxOffsetY -> {
                    offsetY = newOffset
                    onOverscrollHeightChange(newOffset)
                    available
                }
                newOffset < 0f -> {
                    offsetY = 0f
                    onOverscrollHeightChange(0f)
                    Offset.Zero
                }
                newOffset > maxOffsetY -> {
                    offsetY = maxOffsetY
                    onOverscrollHeightChange(maxOffsetY)
                    Offset.Zero
                }
                else -> {
                    Offset.Zero
                }
            }
        } else {
            return Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (offsetY > 0) {
            with(CoroutineScope(coroutineContext)) {
                animateToZero()
            }
            return super.onPreFling(available)
        }
        return super.onPreFling(available)
    }

    private fun CoroutineScope.animateToZero() = launch {
        mutatorMutex.mutate {
            animating = true
            Animatable(offsetY).animateTo(
                targetValue = 0f,
                animationSpec = tween(200)
            ) {
                offsetY = this.value
                onOverscrollHeightChange(this.value)
                if (this.value == 0f) {
                    animating = false
                }
            }
        }
    }
}
