package cn.ommiao.library.example.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.ommiao.library.example.data.MaterialColors
import cn.ommiao.library.example.data.toColor
import cn.ommiao.library.overscroll.OverscrollLazyColumn
import cn.ommiao.theme.example.R
import kotlin.math.roundToInt

val columnItemHeight = 200.dp
val columnHeaderHeight = 456.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OverscrollScreen() {
    val statusBarHeight = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(this).toDp()
    }
    val topBarOffset = remember {
        mutableStateOf(0f)
    }
    val scrollState = rememberLazyListState()
    OverscrollLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        maxOverscrollHeight = 100.dp,
        onOverscrollHeightChange = {
            topBarOffset.value = it
        },
        overscrollContent = {
            OverscrollContent()
        }
    ) {
        items(MaterialColors) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(columnItemHeight),
                backgroundColor = item.toColor(),
                shape = RoundedCornerShape(0.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
    TopBar(statusBarHeight, topBarOffset)
}

@Composable
private fun BoxScope.OverscrollContent() {
    Image(
        painter = painterResource(id = R.mipmap.puppy3),
        contentDescription = "puppy-top",
        modifier = Modifier
            .fillMaxWidth()
            .height(columnHeaderHeight),
        contentScale = ContentScale.Crop
    )
    Text(
        text = "A little dog",
        color = Color.White,
        style = MaterialTheme.typography.h3.copy(
            fontFamily = montFamily
        ),
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomEnd)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TopBar(
    statusBarHeight: Dp,
    topBarOffset: MutableState<Float>
) {
    Card(
        backgroundColor = Color.White.copy(alpha = 0.88f),
        modifier = Modifier
            .padding(top = statusBarHeight)
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp)
            .offset { IntOffset(x = 0, y = topBarOffset.value.roundToInt()) },
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nothing to show",
                style = MaterialTheme.typography.body1.copy(
                    fontFamily = montFamily
                )
            )
        }
    }
}

val montFamily = FontFamily(
    Font(R.font.mont_regular, FontWeight.Normal),
    Font(R.font.mont_bold, FontWeight.Bold)
)
