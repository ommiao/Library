package cn.ommiao.library.overscroll

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.OverscrollConfiguration
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.roundToInt

/*
   overscrollContent will be placed in the first item of LazyColumn
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OverscrollLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overScrollConfiguration: OverscrollConfiguration? = OverscrollConfiguration(),
    maxOverscrollHeight: Dp,
    onOverscrollHeightChange: (Float) -> Unit = {},
    overscrollContent: @Composable BoxScope.() -> Unit,
    content: LazyListScope.() -> Unit
) {
    var lazyColumnHeight by remember {
        mutableStateOf(0)
    }
    var dynamicOffsetY by remember {
        mutableStateOf(0f)
    }
    val maxOffsetY = with(LocalDensity.current) {
        maxOverscrollHeight.toPx()
    }
    val connection = remember {
        OverscrollNestedConnection(
            scrollState = state,
            maxOffsetY = maxOffsetY,
            onOverscrollHeightChange = {
                onOverscrollHeightChange(it)
                dynamicOffsetY = it
            }
        )
    }
    var scrollConfigurationState by remember {
        mutableStateOf(overScrollConfiguration)
    }
    LaunchedEffect(
        dynamicOffsetY,
        remember { derivedStateOf { state.firstVisibleItemIndex } },
        remember { derivedStateOf { state.firstVisibleItemScrollOffset } }
    ) {
        snapshotFlow {
            ScrollOffsetState(
                dynamicOffsetY,
                state.firstVisibleItemIndex,
                state.firstVisibleItemScrollOffset
            )
        }.distinctUntilChanged { old, new ->
            val isShowAllItems =
                state.layoutInfo.totalItemsCount == state.layoutInfo.visibleItemsInfo.size
            val isShowFullAllItems =
                state.layoutInfo.visibleItemsInfo.sumOf { acc -> acc.size } - maxOffsetY < lazyColumnHeight
            if (isShowAllItems && isShowFullAllItems) {
                if (scrollConfigurationState != null) {
                    scrollConfigurationState = null
                }
            } else {
                val shouldRemoveOverscrollConfiguration = !old.isScrollToTop && new.isScrollToTop
                val shouldAddOverScrollConfiguration =
                    scrollConfigurationState == null && (new.hasOffset || !new.isScrollToTop)
                when {
                    shouldRemoveOverscrollConfiguration -> {
                        scrollConfigurationState = null
                    }
                    shouldAddOverScrollConfiguration -> {
                        scrollConfigurationState = overScrollConfiguration
                    }
                }
            }
            old == new
        }.collect()
    }
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides scrollConfigurationState
    ) {
        LazyColumn(
            modifier = modifier
                .onSizeChanged {
                    lazyColumnHeight = it.height
                }
                .nestedScroll(connection),
            state = state,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            userScrollEnabled = userScrollEnabled,
            content = {
                item {
                    Box(
                        modifier = Modifier.layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            layout(
                                placeable.width,
                                (placeable.height - maxOffsetY + dynamicOffsetY).roundToInt()
                            ) {
                                placeable.place(
                                    0,
                                    (dynamicOffsetY - maxOffsetY).roundToInt()
                                )
                            }
                        }
                    ) {
                        overscrollContent()
                    }
                }
                content()
            }
        )
    }
}

internal data class ScrollOffsetState(
    val dynamicOffsetY: Float = 0f,
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0
) {
    val isScrollToTop = firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
    val hasOffset = dynamicOffsetY > 0
}
