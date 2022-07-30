package cn.ommiao.library.colorpicker

import android.graphics.Matrix
import android.util.Range
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    state: MutableState<ColorPickerStateValue>,
    size: Dp = with(LocalConfiguration.current) {
        minOf(screenWidthDp, screenHeightDp).dp
    },
    btnSize: Dp = 36.dp,
    centerPadding: Dp = 24.dp,
    initialColorDot: ColorDot,
    initialLightness: Float = 1f,
    position: ColorPickerPosition = ColorPickerPosition.BottomLeft,
    btnBackgroundColor: Color = Color.White,
    backgroundColor: Color = Color.White,
    elevation: Dp = 3.dp,
    onColorPickCancelled: () -> Unit = {},
    onColorPicked: (ColorDot, Float) -> Unit = { _, _ -> }
) {
    val pickerCanvasSize = (size - btnSize * 3 / 2 - centerPadding * 2) * 2
    val pickerCanvasSizePx = with(LocalDensity.current) { pickerCanvasSize.toPx() }
    val halfPickerCanvasSizePx = pickerCanvasSizePx / 2
    // relative offset with center point of whole view
    val pickerPositionOffset =
        position.getPickerCenterOffset(
            size = size,
            btnSize = btnSize,
            centerPadding = centerPadding
        )

    val pickerOffset = pickerPositionOffset.toOffset() - DpOffset(
        (pickerCanvasSize - size) / 2,
        (pickerCanvasSize - size) / 2
    ).toOffset()

    val colorDots = rememberColorDots(key = state.value, canvasSize = pickerCanvasSizePx)
    var selectedDotColor by remember(initialColorDot) {
        mutableStateOf(initialColorDot)
    }

    var colorPickerButtonPosition by remember {
        mutableStateOf(ColorPickerButtonPosition.START)
    }
    val onBloomFinished = {
        colorPickerButtonPosition = ColorPickerButtonPosition.END
    }
    PickLaunchEffect(state, selectedDotColor, colorDots, onBloomFinished)
    var pickerRotation by remember(state.value) {
        mutableStateOf(
            -selectedDotColor.hsv.get(0).plus(position.getSelectedDotInitialRotation())
        )
    }
    val canvasCenter = remember {
        Offset(halfPickerCanvasSizePx, halfPickerCanvasSizePx)
    }
    val pickerRotateCenter = remember(position) {
        canvasCenter + pickerOffset
    }

    var btnRotationSnap by remember(position, initialLightness) {
        mutableStateOf(position.getRoattion(initialLightness))
    }

    val btnRotation by animateFloatAsState(targetValue = btnRotationSnap)

    val btnRotateCenter = position.getStartBtnOffset(size, btnSize, centerPadding).toOffset()

    var lightness by remember(position, initialLightness) {
        mutableStateOf(initialLightness)
    }

    val btnColorAnimatable = remember(position) {
        Animatable(selectedDotColor.lightnessColor(lightness))
    }

    LaunchedEffect(selectedDotColor) {
        btnColorAnimatable.animateTo(selectedDotColor.lightnessColor(lightness))
    }

    LaunchedEffect(lightness) {
        if (state.value == ColorPickerStateValue.PREVIEW) {
            btnColorAnimatable.animateTo(selectedDotColor.lightnessColor(lightness))
        } else {
            btnColorAnimatable.snapTo(selectedDotColor.lightnessColor(lightness))
        }
    }

    val btnStartOffset = position.getStartBtnOffset(size, btnSize, centerPadding).toOffset()

    val btnEndOffset = position.getEndBtnOffset(size, btnSize, centerPadding).toOffset()

    var picked by remember {
        mutableStateOf(true)
    }

    val btnDynamicOffset by animateOffsetAsState(
        if (colorPickerButtonPosition == ColorPickerButtonPosition.START) btnStartOffset else btnEndOffset,
        animationSpec = tween(300),
        finishedListener = {
            if (it == btnStartOffset) {
                if (picked) {
                    onColorPicked(selectedDotColor, lightness)
                } else {
                    onColorPickCancelled()
                    btnRotationSnap = position.getRoattion(initialLightness)
                }
            }
        }
    )

    val scope = rememberCoroutineScope()
    val btnSizePx = with(LocalDensity.current) { btnSize.toPx() }
    val centerPaddingPx = with(LocalDensity.current) { centerPadding.toPx() }
    val isPreview = state.value == ColorPickerStateValue.PREVIEW
    val backgroundRadius by animateFloatAsState(
        targetValue = if (isPreview) btnSizePx / 2 else pickerCanvasSizePx / 2 + btnSizePx + centerPaddingPx / 2,
        tween(if (isPreview) 300 else 600)
    )
    val shape by remember(position, backgroundRadius) {
        mutableStateOf(
            GenericShape { _, _ ->
                addOval(Rect(btnStartOffset, backgroundRadius))
            }
        )
    }
    Box(modifier = modifier.size(size).clipToBounds()) {
        Canvas(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = elevation,
                    shape = shape,
                    clip = true
                )
                .background(backgroundColor)
                .pointerInput(state.value, position) {
                    detectTapGestures(
                        onLongPress = { offset ->
                            val offsetRotated =
                                offset.rotate(btnRotateCenter, btnRotationSnap)
                            if (state.value == ColorPickerStateValue.PICK && offsetRotated.inCircle(
                                    radius = btnSize.toPx() / 2,
                                    center = btnDynamicOffset
                                )
                            ) {
                                picked = false
                                state.value = ColorPickerStateValue.PREVIEW
                                colorPickerButtonPosition = ColorPickerButtonPosition.START
                                selectedDotColor = initialColorDot
                                lightness = initialLightness
                            }
                        }
                    ) { offset ->
                        scope.launch {
                            val offsetRotated =
                                offset.rotate(btnRotateCenter, btnRotation)
                            val pickerOffsetRotated =
                                (offset - pickerOffset).rotate(canvasCenter, pickerRotation)
                            if (offsetRotated.inCircle(
                                    radius = btnSize.toPx() / 2,
                                    center = btnDynamicOffset
                                )
                            ) {
                                picked = true
                                state.value = state.value.reverse()
                                if (state.value == ColorPickerStateValue.PREVIEW) {
                                    colorPickerButtonPosition = ColorPickerButtonPosition.START
                                }
                            } else if (state.value == ColorPickerStateValue.PICK &&
                                pickerOffsetRotated.inCircle(halfPickerCanvasSizePx)
                            ) {
                                colorDots
                                    .filter { it.isBlinking() }
                                    .forEach { it.stopBlink() }
                                val nearestDot = colorDots.findNearest(pickerOffsetRotated)
                                selectedDotColor = nearestDot
                                nearestDot.startBlink()
                            }
                        }
                    }
                }
                .pointerInput(state.value, position) {
                    var pickerDragging = false
                    var btnDragging = false
                    detectDragGestures(
                        onDragEnd = {
                            pickerDragging = false
                            btnDragging = false
                        }
                    ) { change, _ ->
                        if (pickerDragging || (
                            (change.previousPosition - pickerOffset)
                                .rotate(canvasCenter, pickerRotation)
                                .inCircle(halfPickerCanvasSizePx) &&
                                (change.position - pickerOffset)
                                    .rotate(canvasCenter, pickerRotation)
                                    .inCircle(halfPickerCanvasSizePx) && btnDragging.not()
                            )
                        ) {
                            pickerDragging = true
                            pickerRotation += change.previousPosition.angle(
                                pickerRotateCenter,
                                change.position
                            )
                            change.consume()
                        } else {
                            if (colorPickerButtonPosition == ColorPickerButtonPosition.END) {
                                if (btnDragging || change.previousPosition
                                    .rotate(btnRotateCenter, btnRotationSnap)
                                    .inCircle(
                                            radius = (btnSize / 2 + centerPadding).toPx(),
                                            center = btnEndOffset
                                        )
                                ) {
                                    btnDragging = true
                                    val dragAngle = change.previousPosition.angle(
                                        btnRotateCenter,
                                        change.position
                                    )
                                    val range = position.getRotationRange()
                                    btnRotationSnap =
                                        (btnRotationSnap + dragAngle).coerceIn(
                                            range.lower,
                                            range.upper
                                        )
                                    lightness = position.getLightness(btnRotationSnap)
                                    change.consume()
                                }
                            }
                        }
                    }
                }
        ) {
            if (state.value == ColorPickerStateValue.PICK) {
                rotate(
                    degrees = pickerRotation,
                    pivot = pickerRotateCenter
                ) {
                    translate(
                        left = pickerOffset.x,
                        top = pickerOffset.y
                    ) {
                        colorDots.forEach {
                            drawCircle(
                                color = it.lightnessColor(lightness),
                                radius = it.radius,
                                center = it.getOffset().value,
                                alpha = it.getAlpha().value
                            )
                        }
                        colorDots[0].let {
                            drawCircle(
                                color = Color.LightGray,
                                radius = it.radius,
                                center = it.getOffset().value,
                                alpha = it.getAlpha().value,
                                style = Stroke(width = it.radius * 0.1f)
                            )
                        }
                    }
                }
            }
            rotate(
                degrees = if (state.value == ColorPickerStateValue.PREVIEW) btnRotation else btnRotationSnap,
                pivot = btnRotateCenter
            ) {
                val color = btnColorAnimatable.asState().value
                val halfBtnSize = btnSize.toPx() / 2
                drawCircle(
                    color = btnBackgroundColor,
                    radius = halfBtnSize,
                    center = btnDynamicOffset
                )
                pickerCircleColors.forEach {
                    drawArc(
                        color = it.color,
                        startAngle = it.startAngle,
                        sweepAngle = it.sweepAngle,
                        useCenter = true,
                        topLeft = btnDynamicOffset - Offset(halfBtnSize, halfBtnSize),
                        size = Size(btnSize.toPx(), btnSize.toPx())
                    )
                }
                drawCircle(
                    color = btnBackgroundColor,
                    radius = halfBtnSize * 0.8f,
                    center = btnDynamicOffset
                )
                drawCircle(
                    color = color,
                    radius = halfBtnSize * 0.7f,
                    center = btnDynamicOffset
                )
            }
        }
    }
}

@Composable
private fun PickLaunchEffect(
    state: MutableState<ColorPickerStateValue>,
    dot: ColorDot,
    colorDots: List<ColorDot>,
    onBloomFinished: () -> Unit
) {
    LaunchedEffect(state.value) {
        if (state.value == ColorPickerStateValue.PICK) {
            colorDots.find { it == dot }?.startBlink()
        }
    }
    colorDots.forEach {
        LaunchedEffect(state.value) {
            if (state.value == ColorPickerStateValue.PICK) {
                it.startOffset()
                onBloomFinished()
            }
        }
    }
}

@Composable
fun rememberColorDots(key: ColorPickerStateValue, canvasSize: Float): List<ColorDot> {
    val density = DENSITY
    val dots = remember(canvasSize) {
        mutableListOf<ColorDot>().apply {
            val half = canvasSize / 2
            val strokeWidth = STROKE_RATIO * (1f + GAP_PERCENTAGE)
            val maxRadius = half - strokeWidth - half / density
            val cSize = maxRadius / (density - 1) / 2
            val initialOffset = Offset(half, half)
            for (i in 0 until density) {
                val p = i.toFloat() / (density - 1)
                val jitter = (i - density / 2f) / density
                val radius = maxRadius * p
                val size =
                    max(
                        1.5f + strokeWidth,
                        cSize + if (i == 0) 0f else cSize * SIZE_JITTER * jitter
                    )
                val total = minOf(calculateTotalCount(radius, size), density * 2)
                for (j in 0 until total) {
                    val angle = PI * 2 * j / total + PI / total * ((i + 1) % 2)
                    val x = half + (radius * cos(angle)).toFloat()
                    val y = half + (radius * sin(angle)).toFloat()
                    val hsv = Array(3) { 0f }
                    hsv[0] = (angle * 180 / PI).toFloat()
                    hsv[1] = radius / maxRadius
                    hsv[2] = 1f
                    add(
                        ColorDot(
                            radius = size - strokeWidth,
                            offset = Offset(x, y),
                            initialOffset = initialOffset,
                            hsv = hsv,
                            layer = i
                        )
                    )
                }
            }
        }.toList()
    }
    return remember(key) {
        dots.map { it.copy() }
    }
}

fun Offset.angle(center: Offset, other: Offset): Float {
    val dx1 = this.x - center.x
    val dy1 = this.y - center.y
    val angle1 = asin(dy1 / hypot(dx1, dy1)) * 180 / PI
    val dx2 = other.x - center.x
    val dy2 = other.y - center.y
    val angle2 = asin(dy2 / hypot(dx2, dy2)) * 180 / PI
    return if (other.x > center.x) {
        angle2 - angle1
    } else {
        angle1 - angle2
    }.toFloat()
}

fun Offset.rotate(center: Offset, angle: Float): Offset {
    matrix.reset()
    matrix.preRotate(-angle, center.x, center.y)
    val points = FloatArray(2)
    points[0] = this.x
    points[1] = this.y
    matrix.mapPoints(points)
    return Offset(points[0], points[1])
}

fun Offset.inCircle(radius: Float, center: Offset = Offset(radius, radius)): Boolean {
    return (this - center).getDistanceSquared() <= radius * radius
}

@Composable
fun DpOffset.toOffset(): Offset {
    return with(LocalDensity.current) {
        Offset(x.toPx(), y.toPx())
    }
}

private fun List<ColorDot>.findNearest(offset: Offset): ColorDot {
    return minBy { it.distanceTo(offset) }
}

private fun calculateTotalCount(radius: Float, size: Float): Int {
    return max(
        1,
        ((1f - GAP_PERCENTAGE) * PI / asin((size / radius).toDouble()) + 0.5f).toInt()
    )
}

@Composable
fun rememberColorPackerState(): MutableState<ColorPickerStateValue> {
    return remember {
        mutableStateOf(ColorPickerStateValue.PREVIEW)
    }
}

enum class ColorPickerStateValue {
    PREVIEW, PICK;

    fun reverse(): ColorPickerStateValue {
        return when (this) {
            PICK -> PREVIEW
            PREVIEW -> PICK
        }
    }
}

enum class ColorPickerPosition {
    TopLeft, TopRight, BottomLeft, BottomRight;

    fun getPickerCenterOffset(size: Dp, btnSize: Dp, centerPadding: Dp): DpOffset {
        return when (this) {
            TopLeft -> {
                DpOffset(
                    -size / 2 + centerPadding + btnSize / 2,
                    -size / 2 + centerPadding + btnSize / 2
                )
            }
            TopRight -> {
                DpOffset(
                    size / 2 - centerPadding - btnSize / 2,
                    -size / 2 + centerPadding + btnSize / 2
                )
            }
            BottomLeft -> {
                DpOffset(
                    -size / 2 + centerPadding + btnSize / 2,
                    size / 2 - centerPadding - btnSize / 2
                )
            }
            BottomRight -> {
                DpOffset(
                    size / 2 - centerPadding - btnSize / 2,
                    size / 2 - centerPadding - btnSize / 2
                )
            }
        }
    }

    fun getSelectedDotInitialRotation(): Float {
        return when (this) {
            TopLeft -> 315f
            TopRight -> 225f
            BottomLeft -> 45f
            BottomRight -> 135f
        }
    }

    fun getRotationRange(): Range<Float> {
        return when (this) {
            TopLeft -> Range(-90f, 0f)
            TopRight -> Range(0f, 90f)
            BottomLeft -> Range(0f, 90f)
            BottomRight -> Range(-90f, 0f)
        }
    }

    fun getLightness(btnRotation: Float): Float {
        return when (this) {
            TopLeft -> 1f + btnRotation / 90f
            TopRight -> 1f - btnRotation / 90f
            BottomLeft -> 1f - btnRotation / 90f
            BottomRight -> 1f + btnRotation / 90f
        }
    }

    fun getRoattion(lightness: Float): Float {
        return when (this) {
            TopLeft -> (lightness - 1f) * 90f
            TopRight -> -(lightness - 1f) * 90f
            BottomLeft -> -(lightness - 1f) * 90f
            BottomRight -> (lightness - 1f) * 90f
        }
    }

    fun getStartBtnOffset(size: Dp, btnSize: Dp, centerPadding: Dp): DpOffset {
        return when (this) {
            TopLeft -> {
                DpOffset(
                    x = centerPadding + btnSize / 2,
                    y = centerPadding + btnSize / 2
                )
            }
            TopRight -> {
                DpOffset(
                    x = size - centerPadding - btnSize / 2,
                    y = centerPadding + btnSize / 2
                )
            }
            BottomLeft -> {
                DpOffset(
                    x = centerPadding + btnSize / 2,
                    y = size - centerPadding - btnSize / 2
                )
            }
            BottomRight -> {
                DpOffset(
                    x = size - centerPadding - btnSize / 2,
                    y = size - centerPadding - btnSize / 2
                )
            }
        }
    }

    fun getEndBtnOffset(size: Dp, btnSize: Dp, centerPadding: Dp): DpOffset {
        return when (this) {
            TopLeft -> {
                DpOffset(
                    x = centerPadding + btnSize / 2,
                    y = size - centerPadding - btnSize / 2
                )
            }
            TopRight -> {
                DpOffset(
                    x = size - centerPadding - btnSize / 2,
                    y = size - centerPadding - btnSize / 2
                )
            }
            BottomLeft -> {
                DpOffset(
                    x = centerPadding + btnSize / 2,
                    y = centerPadding + btnSize / 2
                )
            }
            BottomRight -> {
                DpOffset(
                    x = size - centerPadding - btnSize / 2,
                    y = centerPadding + btnSize / 2
                )
            }
        }
    }

    fun getAlignment(): Alignment {
        return when (this) {
            TopLeft -> Alignment.TopStart
            TopRight -> Alignment.TopEnd
            BottomLeft -> Alignment.BottomStart
            BottomRight -> Alignment.BottomEnd
        }
    }
}

enum class ColorPickerButtonPosition {
    START, END
}

private val pickerCircleColors = run {
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
        PickerCircleColor(
            color = it.first,
            startAngle = colorStartAngle,
            sweepAngle = perColorSweepAngle
        ).also {
            colorStartAngle -= perColorSweepWithDividerAngle
        }
    }
}

private data class PickerCircleColor(
    val color: Color,
    val startAngle: Float,
    val sweepAngle: Float
)

private val matrix = Matrix()

internal const val DENSITY = 12
private const val SIZE_JITTER = 1.2f
private const val STROKE_RATIO = 1.5f
private const val GAP_PERCENTAGE = 0.025f
