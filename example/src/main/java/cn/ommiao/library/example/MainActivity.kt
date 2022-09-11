package cn.ommiao.library.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import cn.ommiao.library.example.screen.ColorPickerScreen
import cn.ommiao.library.example.screen.HomepageScreen
import cn.ommiao.library.example.screen.OverscrollScreen
import cn.ommiao.library.example.screen.Route
import cn.ommiao.library.example.screen.ThemeScreen
import cn.ommiao.library.theme.Appearance
import cn.ommiao.library.theme.Theme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Content()
        }
    }
}

val LocalNavHostController = compositionLocalOf<NavHostController> { error("not provide") }
private const val TRANSITION_DURATION = 500

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Content() {
    var theme by rememberSaveable {
        mutableStateOf(Appearance.Theme.SYSTEM)
    }
    Theme(theme = theme) {
        val navHostController = rememberAnimatedNavController()
        CompositionLocalProvider(LocalNavHostController provides navHostController) {
            AnimatedNavHost(
                navController = navHostController,
                startDestination = Route.Homepage.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(TRANSITION_DURATION)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(TRANSITION_DURATION),
                        targetOffset = { it / 2 }
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(TRANSITION_DURATION),
                        initialOffset = { it / 2 }
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(TRANSITION_DURATION)
                    )
                }
            ) {
                composable(route = Route.Homepage.route) {
                    HomepageScreen()
                }
                composable(route = Route.Theme.route) {
                    ThemeScreen(theme = theme) { theme = it }
                }
                composable(route = Route.ColorPicker.route) {
                    ColorPickerScreen()
                }
                composable(route = Route.Overscroll.route) {
                    OverscrollScreen()
                }
            }
        }
    }
}
