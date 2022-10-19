package cn.ommiao.library.example.screen

import androidx.navigation.NavHostController

sealed class Route(val route: String) {

    object Homepage : Route("homepage")

    object Theme : Route("theme")

    object ColorPicker : Route("color-picker")

    object Overscroll : Route("overscroll")

    object SameItemWidthRow : Route("same-width-item-row")

    fun navigate(navHostController: NavHostController) {
        navHostController.navigate(route)
    }
}
