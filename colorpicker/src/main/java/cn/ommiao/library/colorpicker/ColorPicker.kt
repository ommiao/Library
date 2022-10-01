package cn.ommiao.library.colorpicker

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Composable
fun ColorPicker(
    state: MutableState<ColorPickerStateValue> = rememberColorPackerState(),
    centerPadding: CenterPadding = CenterPadding(),
    canvasPadding: Dp = 16.dp,
    previewSize: Dp = 32.dp,
    alignment: ColorPickerAlignment = ColorPickerAlignment.BottomLeft,
    initialColorDot: ColorDot = ColorDot(hsv = arrayOf(0f, 1f, 1f)),
    initialLightness: Float = 1f,
    elevation: Dp = 4.dp,
    background: Color = Color.White,
    onColorPicked: (colorDot: ColorDot, lightness: Float) -> Unit
) {
    var pickerState by state
    BoxWithConstraints(Modifier.fillMaxSize().clipToBounds()) {
        val size = DpSize(maxWidth, maxHeight)
        val previewRadius = previewSize / 2
        val previewRadiusPx = with(LocalDensity.current) { previewRadius.toPx() }
        val wheelCenterDpOffset = alignment.getCanvasCenter(size, centerPadding, previewRadius)
        val wheelCenterOffset = wheelCenterDpOffset.toOffset()
        val wheelRadiusDp =
            alignment.getCanvasRadius(size, centerPadding, canvasPadding, previewRadius)
        val wheelSizePx = with(LocalDensity.current) { (wheelRadiusDp * 2).toPx() }
        val wheelRadiusPx = wheelSizePx / 2
        val colorDots = rememberColorDots(wheelSizePx)
        val wheelDefaultCenterOffset = DpOffset(wheelRadiusDp, wheelRadiusDp).toOffset()
        var activeColorDot by remember {
            mutableStateOf(colorDots.first { it == initialColorDot })
        }
        val activeDotOffset = remember(alignment, centerPadding) {
            Animatable(Offset(wheelRadiusPx, wheelRadiusPx), typeConverter = Offset.VectorConverter)
        }
        val activeDotRadius = remember(alignment) {
            Animatable(previewRadiusPx)
        }
        val activeDotColor = remember {
            Animatable(activeColorDot.color)
        }
        var bloomingOrShrinking by remember(alignment) {
            mutableStateOf(false)
        }
        val lightness = remember {
            Animatable(initialLightness)
        }
        val wheelRotation = remember(alignment) {
            Animatable(-activeColorDot.hsv[0].plus(alignment.getSelectedDotInitialRotation()))
        }
        LaunchedEffect(alignment, initialColorDot, initialLightness) {
            activeColorDot = colorDots.first { it == initialColorDot }
            launch {
                lightness.snapTo(initialLightness)
                activeDotColor.animateTo(
                    activeColorDot.lightnessColor(initialLightness),
                    lowSpring()
                )
            }
            launch {
                wheelRotation.animateTo(
                    -activeColorDot.hsv[0].plus(alignment.getSelectedDotInitialRotation()),
                    lowSpring()
                )
            }
        }
        LaunchedEffect(pickerState) {
            if (pickerState == ColorPickerStateValue.PICK) {
                ColorDot.Animatables.values.forEach { it.snapTo(0f) }
                val delayUnit = BLOOM_DURATION / 20
                repeat(DENSITY) { index ->
                    launch {
                        ColorDot.Animatables[index]?.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                BLOOM_DURATION,
                                delayMillis = (DENSITY - index - 1) * delayUnit,
                                easing = LinearOutSlowInEasing
                            )
                        )
                        if (index == DENSITY - 1) {
                            val active = colorDots.first { it == initialColorDot }
                            launch {
                                activeDotOffset.animateTo(
                                    active.offset,
                                    lowSpring()
                                )
                                bloomingOrShrinking = false
                            }
                            launch {
                                activeDotRadius.animateTo(active.radius, lowSpring())
                            }
                        }
                    }
                }
            }
        }
        val wheelTranslateOffset = wheelCenterOffset - wheelDefaultCenterOffset
        val clipRadius = remember(alignment) {
            Animatable(previewRadiusPx)
        }
        val shape by remember(alignment, clipRadius.asState().value, centerPadding) {
            mutableStateOf(
                GenericShape { _, _ ->
                    addOval(Rect(wheelCenterOffset, clipRadius.value))
                }
            )
        }
        val scope = rememberCoroutineScope()
        if (pickerState == ColorPickerStateValue.PICK) {
            val onCancel = {
                if (bloomingOrShrinking.not()) {
                    scope.launch {
                        scope.launch {
                            bloomingOrShrinking = true
                            activeDotOffset.animateTo(
                                Offset(wheelRadiusPx, wheelRadiusPx),
                                lowSpring()
                            )
                        }
                        scope.launch {
                            activeDotColor.animateTo(
                                initialColorDot.lightnessColor(initialLightness),
                                lowSpring()
                            )
                        }
                        scope.launch {
                            activeDotRadius.animateTo(previewRadiusPx, lowSpring())
                            clipRadius.animateTo(previewRadiusPx, lowSpring())
                            val preferredRotation =
                                -initialColorDot.hsv[0].plus(alignment.getSelectedDotInitialRotation())
                            wheelRotation.animateTo(preferredRotation)
                            activeColorDot = colorDots.first { it == initialColorDot }
                            lightness.snapTo(initialLightness)
                            pickerState = ColorPickerStateValue.PREVIEW
                            bloomingOrShrinking = false
                        }
                    }
                }
            }
            BackHandler(onBack = onCancel)
            Box(
                Modifier.fillMaxSize()
                    .clickable(onClick = onCancel)
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = elevation,
                    shape = shape
                )
                .background(background)
                .clickInputModifier(
                    colorDots,
                    pickerState,
                    alignment,
                    wheelRadiusPx,
                    wheelTranslateOffset,
                    wheelDefaultCenterOffset,
                    wheelRotation.asState(),
                    onPreviewClicked = {
                        if (bloomingOrShrinking) return@clickInputModifier
                        scope.launch {
                            bloomingOrShrinking = true
                            clipRadius.animateTo(wheelRadiusPx, lowSpring())
                        }
                        scope.launch {
                            pickerState = ColorPickerStateValue.PICK
                        }
                    },
                    onDotClicked = { pickedDot ->
                        if (bloomingOrShrinking) return@clickInputModifier
                        if (activeColorDot == pickedDot) {
                            scope.launch {
                                bloomingOrShrinking = true
                                activeDotOffset.animateTo(
                                    Offset(wheelRadiusPx, wheelRadiusPx),
                                    lowSpring()
                                )
                            }
                            scope.launch {
                                activeDotRadius.animateTo(previewRadiusPx, lowSpring())
                                clipRadius.animateTo(previewRadiusPx, lowSpring())
                                val preferredRotation =
                                    -activeColorDot.hsv[0].plus(alignment.getSelectedDotInitialRotation())
                                wheelRotation.animateTo(preferredRotation)
                                pickerState = ColorPickerStateValue.PREVIEW
                                if (pickedDot != initialColorDot || lightness.value != initialLightness) {
                                    onColorPicked.invoke(pickedDot, lightness.value)
                                }
                                bloomingOrShrinking = false
                            }
                        } else {
                            activeColorDot = pickedDot
                            scope.launch {
                                activeDotOffset.animateTo(pickedDot.offset, lowSpring())
                            }
                            scope.launch {
                                activeDotRadius.animateTo(pickedDot.radius, lowSpring())
                            }
                            scope.launch {
                                activeDotColor.animateTo(
                                    pickedDot.lightnessColor(lightness.value)
                                )
                            }
                        }
                    }
                )
                .dragInputModifier(
                    pickerState,
                    alignment,
                    wheelRadiusPx,
                    wheelTranslateOffset,
                    wheelCenterOffset,
                    onLightnessRotate = {
                        if (bloomingOrShrinking) return@dragInputModifier
                        scope.launch {
                            lightness.snapTo((lightness.value - it / 90f).coerceIn(0f, 1f))
                            activeDotColor.snapTo(activeColorDot.lightnessColor(lightness.value))
                        }
                    },
                    onPickerRotate = {
                        if (bloomingOrShrinking) return@dragInputModifier
                        scope.launch {
                            val rotation = (wheelRotation.value + it) % 360f
                            wheelRotation.snapTo(if (rotation > 0f) rotation - 360f else rotation)
                        }
                    }
                )
        ) {
            drawColorDots(
                pickerState,
                wheelRotation.value,
                wheelDefaultCenterOffset,
                wheelTranslateOffset,
                colorDots,
                activeColorDot,
                activeDotOffset.asState().value,
                activeDotRadius.asState().value,
                activeDotColor.asState().value,
                lightness.asState().value,
                background,
                bloomingOrShrinking
            )
        }
    }
}

private fun Modifier.clickInputModifier(
    colorDots: List<ColorDot>,
    state: ColorPickerStateValue,
    alignment: ColorPickerAlignment,
    wheelRadius: Float,
    wheelTranslateOffset: Offset,
    wheelDefaultCenterOffset: Offset,
    wheelRotation: State<Float>,
    onPreviewClicked: () -> Unit,
    onDotClicked: (ColorDot) -> Unit
): Modifier =
    pointerInput(state, alignment, wheelTranslateOffset) {
        detectTapGestures { offset ->
            val pickerOffsetRotated =
                (offset - wheelTranslateOffset).rotate(
                    wheelDefaultCenterOffset,
                    wheelRotation.value
                )
            if (state == ColorPickerStateValue.PICK && pickerOffsetRotated.inCircle(wheelRadius)) {
                val nearestDot = colorDots.findNearest(pickerOffsetRotated)
                onDotClicked.invoke(nearestDot)
            } else if (state == ColorPickerStateValue.PREVIEW) {
                onPreviewClicked.invoke()
            }
        }
    }

private fun Modifier.dragInputModifier(
    state: ColorPickerStateValue,
    alignment: ColorPickerAlignment,
    wheelRadius: Float,
    wheelTranslateOffset: Offset,
    wheelCenterOffset: Offset,
    onLightnessRotate: (Float) -> Unit,
    onPickerRotate: (Float) -> Unit
): Modifier {
    return if (state == ColorPickerStateValue.PICK) {
        pointerInput(state, alignment, wheelTranslateOffset) {
            var pickerDragging = false
            var lightnessDragging = false
            detectDragGestures(
                onDragEnd = {
                    pickerDragging = false
                    lightnessDragging = false
                }
            ) { change, _ ->
                val startPosition = change.previousPosition - wheelTranslateOffset
                if (lightnessDragging || (
                    startPosition.inCircle(
                            wheelRadius / 2,
                            center = Offset(wheelRadius, wheelRadius)
                        ) && pickerDragging.not()
                    )
                ) {
                    lightnessDragging = true
                    onLightnessRotate.invoke(
                        change.previousPosition.angle(wheelCenterOffset, change.position)
                    )
                    change.consume()
                } else if (pickerDragging || (startPosition.inCircle(wheelRadius) && lightnessDragging.not())) {
                    pickerDragging = true
                    onPickerRotate.invoke(
                        change.previousPosition.angle(wheelCenterOffset, change.position)
                    )
                    change.consume()
                }
            }
        }
    } else {
        this
    }
}

private fun DrawScope.drawColorDots(
    state: ColorPickerStateValue,
    wheelRotation: Float,
    wheelDefaultCenterOffset: Offset,
    wheelTranslateOffset: Offset,
    colorDots: List<ColorDot>,
    activeDot: ColorDot,
    activeDotOffset: Offset,
    activeDotRadius: Float,
    activeDotColor: Color,
    lightness: Float,
    background: Color,
    bloomingOrShrinking: Boolean
) {
    translate(
        left = wheelTranslateOffset.x,
        top = wheelTranslateOffset.y
    ) {
        rotate(
            degrees = wheelRotation,
            pivot = wheelDefaultCenterOffset
        ) {
            if (state == ColorPickerStateValue.PICK) {
                colorDots.filter { (it == activeDot && bloomingOrShrinking).not() }.forEach {
                    drawCircle(
                        color = it.lightnessColor(lightness),
                        radius = it.radius,
                        center = it.getOffset(
                            ColorDot.Animatables[it.layer]?.asState()?.value ?: 0f
                        )
                    )
                }
                colorDots[0].let {
                    drawCircle(
                        color = Color.LightGray,
                        radius = it.radius,
                        center = it.initialOffset,
                        style = Stroke(width = it.radius * 0.1f)
                    )
                }
            }
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                activeDot.let {
                    drawCircle(
                        color = background,
                        radius = activeDotRadius,
                        center = activeDotOffset,
                        blendMode = BlendMode.Src
                    )
                    rotate(
                        degrees = 90f,
                        pivot = activeDotOffset
                    ) {
                        PickerArcColor.pickerArcColors.forEach { arcColor ->
                            drawArc(
                                color = arcColor.color,
                                startAngle = arcColor.startAngle,
                                sweepAngle = arcColor.sweepAngle,
                                useCenter = true,
                                topLeft = activeDotOffset - Offset(
                                    activeDotRadius,
                                    activeDotRadius
                                ),
                                size = Size(activeDotRadius * 2, activeDotRadius * 2),
                                blendMode = BlendMode.Src
                            )
                        }
                    }
                    drawCircle(
                        color = activeDotColor,
                        radius = activeDotRadius * 0.8f,
                        center = activeDotOffset,
                        blendMode = if (bloomingOrShrinking || state == ColorPickerStateValue.PREVIEW) BlendMode.Src else BlendMode.DstOut
                    )
                }
                restoreToCount(checkPoint)
            }
        }
    }
}

@Composable
fun rememberColorPackerState(): MutableState<ColorPickerStateValue> {
    return remember {
        mutableStateOf(ColorPickerStateValue.PREVIEW)
    }
}

@Composable
private fun rememberColorDots(wheelSize: Float): List<ColorDot> {
    val density = DENSITY
    return remember(wheelSize) {
        mutableListOf<ColorDot>().apply {
            val half = wheelSize / 2
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
}

private fun calculateTotalCount(radius: Float, size: Float): Int {
    return max(
        1,
        ((1f - GAP_PERCENTAGE) * PI / asin((size / radius).toDouble()) + 0.5f).toInt()
    )
}

fun List<ColorDot>.findNearest(offset: Offset): ColorDot {
    return minBy { it.distanceTo(offset) }
}

private fun <T> lowSpring() =
    spring<T>(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy)

internal const val DENSITY = 12
private const val SIZE_JITTER = 1.2f
private const val STROKE_RATIO = 1.5f
private const val GAP_PERCENTAGE = 0.025f

private const val BLOOM_DURATION = 750
