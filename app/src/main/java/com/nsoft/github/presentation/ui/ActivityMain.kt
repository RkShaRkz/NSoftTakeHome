package com.nsoft.github.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nsoft.github.domain.navigation.NavigationRoutes
import com.nsoft.github.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    NavigationComponent()
                }
            }
        }
    }


    @Composable
    fun NavigationComponent(viewModel: MainViewModel = hiltViewModel()) {
        val navController: NavHostController = rememberNavController()

        val startDestination = NavigationRoutes.FIRST_SCREEN.getRouteName()

        NavHost(navController = navController, startDestination = startDestination) {
            composable(NavigationRoutes.FIRST_SCREEN.getRouteName()) { FirstScreen(navController) }
            composable(NavigationRoutes.SECOND_SCREEN.getRouteName()) { SecondScreen(navController) }
            composable(NavigationRoutes.THIRD_SCREEN.getRouteName()) { ThirdScreen(navController) }
        }
    }
}