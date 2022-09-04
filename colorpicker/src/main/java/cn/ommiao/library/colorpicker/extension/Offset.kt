package cn.ommiao.library.colorpicker

import android.graphics.Matrix
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.hypot

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

private val matrix = Matrix()
