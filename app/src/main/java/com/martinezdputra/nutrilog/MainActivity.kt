package com.martinezdputra.nutrilog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.martinezdputra.nutrilog.core.navigation.Route
import com.martinezdputra.nutrilog.navigation.navigate
import com.martinezdputra.nutrilog.onboarding_presentation.activity.ActivityLevelScreen
import com.martinezdputra.nutrilog.onboarding_presentation.age.AgeScreen
import com.martinezdputra.nutrilog.onboarding_presentation.gender.GenderScreen
import com.martinezdputra.nutrilog.onboarding_presentation.goal.GoalScreen
import com.martinezdputra.nutrilog.onboarding_presentation.height.HeightScreen
import com.martinezdputra.nutrilog.onboarding_presentation.nutrient_goal.NutrientGoalScreen
import com.martinezdputra.nutrilog.onboarding_presentation.weight.WeightScreen
import com.martinezdputra.nutrilog.onboarding_presentation.welcome.WelcomeScreen
import com.martinezdputra.nutrilog.ui.theme.NutrilogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutrilogTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState
                ) { scaffoldPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Route.WELCOME,
                        modifier = Modifier.padding(scaffoldPadding)
                    ) {
                        composable(Route.WELCOME) {
                            WelcomeScreen(onNavigate = navController::navigate)
                        }
                        composable(Route.AGE) {
                            AgeScreen(
                                onNavigate = navController::navigate,
                                scaffoldState = scaffoldState
                            )
                        }
                        composable(Route.GENDER) {
                            GenderScreen(onNavigate = navController::navigate)
                        }
                        composable(Route.HEIGHT) {
                            HeightScreen(
                                scaffoldState = scaffoldState,
                                onNavigate = navController::navigate
                            )
                        }
                        composable(Route.WEIGHT) {
                            WeightScreen(
                                scaffoldState = scaffoldState,
                                onNavigate = navController::navigate
                            )
                        }
                        composable(Route.NUTRIENT_GOAL) {
                            NutrientGoalScreen(
                                scaffoldState = scaffoldState,
                                onNavigate = navController::navigate
                            )
                        }
                        composable(Route.ACTIVITY) {
                            ActivityLevelScreen(onNavigate = navController::navigate)
                        }
                        composable(Route.GOAL) {
                            GoalScreen(onNavigate = navController::navigate)
                        }
                        composable(Route.TRACKER_OVERVIEW) {

                        }
                        composable(Route.SEARCH) {

                        }
                    }
                }
            }
        }
    }
}
