package cn.ommiao.library.example.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.ommiao.library.example.LocalNavHostController
import cn.ommiao.library.theme.Appearance

@Composable
fun HomepageScreen() {
    val navHostController = LocalNavHostController.current
    Box(modifier = Modifier.fillMaxSize().background(Appearance.colors.backgroundPrimary)) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Route::class.sealedSubclasses.filter { it.simpleName != Route.Homepage::class.simpleName }
                .forEach {
                    Button(onClick = {
                        it.objectInstance?.navigate(navHostController)
                    }) {
                        Text(text = it.simpleName!!)
                    }
                }
        }
    }
}
