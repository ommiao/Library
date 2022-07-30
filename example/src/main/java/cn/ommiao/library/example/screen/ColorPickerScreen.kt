package cn.ommiao.library.example.screen

import android.graphics.Matrix
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.ommiao.library.colorpicker.ColorDot
import cn.ommiao.library.colorpicker.ColorPicker
import cn.ommiao.library.colorpicker.ColorPickerPosition
import cn.ommiao.library.colorpicker.rememberColorPackerState
import cn.ommiao.library.theme.Appearance
import cn.ommiao.theme.example.R
import kotlinx.coroutines.launch
import java.util.*

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ColorPickerScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Appearance.colors.backgroundPrimaryBase)
    ) {
        val colorPickerState = rememberColorPackerState()
        val items = remember {
            mutableStateListOf<ColorItem>()
        }
        var btnColor by remember {
            mutableStateOf(
                ColorDot(
                    radius = 0f,
                    offset = Offset.Zero,
                    initialOffset = Offset.Zero,
                    hsv = arrayOf(0f, 1f, 1f),
                    layer = 0
                )
            )
        }
        var lightness by remember {
            mutableStateOf(1f)
        }
        val paintColor = btnColor.lightnessColor(lightness)
        val onPaintColor = paintColor.whiteOrBlack()
        val paintColorAnimation by animateColorAsState(paintColor)
        val onPaintColorAnimation by animateColorAsState(onPaintColor)
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.Center),
            backgroundColor = paintColorAnimation,
            onClick = {
                val added = items.findByHsv(btnColor.hsv[0], btnColor.hsv[1], lightness)
                if (added == null) {
                    items.add(ColorItem(hsv = btnColor.hsv, lightness = lightness))
                } else {
                    items[added.first] = added.second.copy(count = added.second.count + 1)
                }
                items.sortByDescending { it.count }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = onPaintColorAnimation
            )
        }
        val btnSize = 28.dp
        val btnPadding = 32.dp
        val colorBarPadding = 16.dp
        val colorBarInnerPadding = btnPadding - colorBarPadding
        val colorBarHeight = btnSize + colorBarInnerPadding * 2
        val colorItemSize = btnSize * 0.9f
        val colorItemInterPadding = colorBarInnerPadding * 0.75f
        val colorBarElevation = 3.dp
        val alphaBarElevation = 8.dp
        val colorBarItemElevation = 2.dp
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(colorBarPadding)
                .height(colorBarHeight),
            elevation = colorBarElevation,
            shape = RoundedCornerShape(8.dp),
            color = Appearance.colors.backgroundPrimaryHigh
        ) {
            Box {
                val lazyListState = rememberLazyListState()
                val alphaBarWidth = btnSize * 2 + colorBarInnerPadding * 2 + colorItemInterPadding
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(
                            start = colorBarInnerPadding + btnSize / 2,
                            end = alphaBarWidth * (1 - ALPHA_BAR_PATH_RATIO)
                        )
                        .animateContentSize(),
                    contentPadding = PaddingValues(
                        start = colorItemInterPadding + if (items.isEmpty()) 0.dp else btnSize / 2,
                        end = colorBarInnerPadding
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(colorItemInterPadding)
                ) {
                    items(items, key = { it.id }) { item ->
                        Surface(
                            modifier = Modifier
                                .size(colorItemSize)
                                .animateItemPlacement(),
                            color = item.color,
                            shape = CircleShape,
                            elevation = colorBarItemElevation,
                            onClick = {
                                btnColor = ColorDot(hsv = item.hsv)
                                lightness = item.lightness
                            }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedVisibility(
                                    visible = items.findByHsv(
                                        btnColor.hsv[0],
                                        btnColor.hsv[1],
                                        lightness
                                    )?.second == item,
                                    enter = scaleIn(),
                                    exit = scaleOut()
                                ) {
                                    Spacer(
                                        modifier = Modifier
                                            .size(colorItemSize * 0.2f)
                                            .clip(CircleShape)
                                            .background(item.color.whiteOrBlack())
                                    )
                                }
                            }
                        }
                    }
                }
                val alphaBarStartPadding = colorBarInnerPadding + btnSize
                Surface(
                    modifier = Modifier
                        .padding(start = alphaBarStartPadding)
                        .align(Alignment.CenterEnd),
                    elevation = alphaBarElevation,
                    shape = GenericShape { size, _ ->
                        moveTo(size.width * ALPHA_BAR_PATH_RATIO, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    },
                    color = Appearance.colors.backgroundPrimaryHigh
                ) {
                    val scope = rememberCoroutineScope()
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(colorBarInnerPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(colorItemInterPadding)
                    ) {
                        val locationBg = ImageVector.vectorResource(id = R.drawable.ic_location2)
                        Surface(
                            modifier = Modifier.size(btnSize),
                            shape = GenericShape { size, _ ->
                                PathParser().addPathNodes(
                                    (locationBg.root[0] as VectorPath).pathData
                                ).toPath(this).asAndroidPath().transform(
                                    Matrix().apply {
                                        setScale(
                                            size.width / locationBg.viewportWidth,
                                            size.height / locationBg.viewportHeight
                                        )
                                    }
                                )
                            },
                            onClick = {
                                scope.launch {
                                    val selected = items.findByHsv(
                                        btnColor.hsv[0],
                                        btnColor.hsv[1],
                                        lightness
                                    )
                                    selected?.first?.let {
                                        lazyListState.animateScrollToItem(it)
                                    }
                                }
                            },
                            elevation = colorBarItemElevation,
                            color = Color.Transparent
                        ) {
                            Icon(
                                modifier = Modifier.size(btnSize),
                                painter = painterResource(id = R.drawable.ic_location2),
                                tint = Appearance.colors.textPrimary,
                                contentDescription = "location selected color"
                            )
                        }
                        val alpha = 120
                        Surface(
                            modifier = Modifier.size(btnSize).padding(2.dp),
                            shape = RoundedCornerShape(4.dp),
                            elevation = colorBarItemElevation
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_opacity),
                                    contentDescription = "opacity",
                                    tint = Appearance.colors.textPrimary
                                )
                                Column {
                                    Spacer(
                                        modifier = Modifier
                                            .weight(ALPHA_ICON_RATIO)
                                            .fillMaxWidth()
                                            .background(paintColorAnimation.copy(alpha / 255f))
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f - ALPHA_ICON_RATIO)
                                            .fillMaxWidth()
                                            .background(paintColorAnimation)
                                    )
                                }
                                Box(
                                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                                        .fillMaxHeight((1 - ALPHA_ICON_RATIO) * 2),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$alpha",
                                        fontSize = 9.sp,
                                        color = onPaintColorAnimation,
                                        textAlign = TextAlign.Center,
                                        fontFamily = agencyFamily
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        val position = ColorPickerPosition.BottomLeft
        ColorPicker(
            modifier = Modifier
                .align(position.getAlignment()),
            state = colorPickerState,
            initialColorDot = btnColor,
            initialLightness = lightness,
            btnSize = btnSize,
            centerPadding = btnPadding,
            position = position,
            btnBackgroundColor = Appearance.colors.backgroundPrimaryHigh,
            backgroundColor = Appearance.colors.backgroundPrimaryHigh
        ) { colorDot, light ->
            btnColor = colorDot
            lightness = light
        }
    }
}

data class ColorItem(
    val id: String = UUID.randomUUID().toString(),
    val hsv: Array<Float>,
    val lightness: Float,
    val count: Int = 0
) {
    val color: Color
        get() = Color.hsv(hsv[0], hsv[1], lightness)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColorItem

        if (!hsv.contentEquals(other.hsv)) return false
        if (lightness != other.lightness) return false
        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hsv.contentHashCode()
        result = 31 * result + lightness.hashCode()
        result = 31 * result + count.hashCode()
        return result
    }
}

fun List<ColorItem>.findByHsv(h: Float, s: Float, v: Float): Pair<Int, ColorItem>? {
    return firstOrNull { it.hsv[0] == h && it.hsv[1] == s && it.lightness == v }?.let { indexOf(it) to it }
}

fun Color.whiteOrBlack(): Color {
    return if (luminance() > 0.8f) Color.Black else Color.White
}

private const val ALPHA_BAR_PATH_RATIO = 0.1f
private const val ALPHA_ICON_RATIO = 0.618f

val agencyFamily = FontFamily(
    Font(R.font.agency_m, FontWeight.Medium)
)
