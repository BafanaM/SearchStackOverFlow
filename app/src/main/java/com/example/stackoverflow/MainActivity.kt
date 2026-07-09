package com.example.stackoverflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stackoverflow.core.ui.theme.StackOverflowTheme
import com.example.stackoverflow.feature.detail.DetailRoute
import com.example.stackoverflow.feature.search.SearchRoute
import dagger.hilt.android.AndroidEntryPoint

private const val ROUTE_SEARCH = "search"
private const val ROUTE_DETAIL = "detail/{questionId}"
private const val ARG_QUESTION_ID = "questionId"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StackOverflowTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = ROUTE_SEARCH) {
                    composable(ROUTE_SEARCH) {
                        SearchRoute(
                            onQuestionClick = { questionId ->
                                navController.navigate("detail/$questionId")
                            },
                        )
                    }
                    composable(
                        route = ROUTE_DETAIL,
                        arguments = listOf(navArgument(ARG_QUESTION_ID) { type = NavType.LongType }),
                    ) {
                        DetailRoute(onBackClick = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
