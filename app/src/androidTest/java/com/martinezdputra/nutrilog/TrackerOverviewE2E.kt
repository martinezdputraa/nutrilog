package com.martinezdputra.nutrilog

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.common.truth.Truth.assertThat
import com.martinezdputra.nutrilog.core.domain.model.ActivityLevel
import com.martinezdputra.nutrilog.core.domain.model.Gender
import com.martinezdputra.nutrilog.core.domain.model.GoalType
import com.martinezdputra.nutrilog.core.domain.model.UserInfo
import com.martinezdputra.nutrilog.core.domain.preferences.Preferences
import com.martinezdputra.nutrilog.core.domain.use_case.FilterOutDigits
import com.martinezdputra.nutrilog.navigation.Route
import com.martinezdputra.nutrilog.repository.TrackerRepositoryFake
import com.martinezdputra.nutrilog.tracker_domain.model.TrackableFood
import com.martinezdputra.nutrilog.tracker_domain.use_case.*
import com.martinezdputra.nutrilog.tracker_presentation.search.SearchScreen
import com.martinezdputra.nutrilog.tracker_presentation.search.SearchViewModel
import com.martinezdputra.nutrilog.tracker_presentation.tracker_overview.TrackerOverviewScreen
import com.martinezdputra.nutrilog.tracker_presentation.tracker_overview.TrackerOverviewViewModel
import com.martinezdputra.nutrilog.ui.theme.NutrilogTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.math.roundToInt

@HiltAndroidTest
class TrackerOverviewE2E {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var preferences: Preferences

    private lateinit var repositoryFake: TrackerRepositoryFake
    private lateinit var trackerUseCases: TrackerUseCases
    private lateinit var trackerOverviewViewModel: TrackerOverviewViewModel
    private lateinit var searchViewModel: SearchViewModel

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        preferences = mockk(relaxed = true)
        every { preferences.loadUserInfo() } returns UserInfo(
            gender = Gender.Male,
            age = 20,
            weight = 80f,
            height = 180,
            activityLevel = ActivityLevel.Medium,
            goalType = GoalType.KeepWeight,
            carbRatio = 0.4f,
            proteinRatio = 0.3f,
            fatRatio = 0.3f,
        )
        repositoryFake = TrackerRepositoryFake()
        trackerUseCases = TrackerUseCases(
            trackFood = TrackFood(repositoryFake),
            searchFood = SearchFood(repositoryFake),
            getFoodsForDate = GetFoodsForDate(repositoryFake),
            deleteTrackedFood = DeleteTrackedFood(repositoryFake),
            calculateMealNutrients = CalculateMealNutrients(preferences)
        )
        trackerOverviewViewModel = TrackerOverviewViewModel(
            preferences = preferences,
            trackerUseCases = trackerUseCases,
        )
        searchViewModel = SearchViewModel(
            trackerUseCases = trackerUseCases,
            filterOutDigits = FilterOutDigits(),
        )
        composeRule.activity.setContent {
            NutrilogTheme {
                val scaffoldState = rememberScaffoldState()
                navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState
                ) { scaffoldPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Route.TRACKER_OVERVIEW,
                        modifier = Modifier.padding(scaffoldPadding)
                    ) {
                        composable(Route.TRACKER_OVERVIEW) {
                            TrackerOverviewScreen(
                                onNavigateToSearch = { mealName, day, month, year ->
                                    navController.navigate(
                                        Route.SEARCH + "/$mealName" +
                                                "/$day" +
                                                "/$month" +
                                                "/$year"
                                    )
                                },
                                viewModel = trackerOverviewViewModel,
                            )
                        }
                        composable(
                            route = Route.SEARCH + "/{mealName}/{dayOfMonth}/{month}/{year}",
                            arguments = listOf(
                                navArgument("mealName") {
                                    type = NavType.StringType
                                },
                                navArgument("dayOfMonth") {
                                    type = NavType.IntType
                                },
                                navArgument("month") {
                                    type = NavType.IntType
                                },
                                navArgument("year") {
                                    type = NavType.IntType
                                },
                            )
                        ) {
                            val mealName = it.arguments?.getString("mealName")!!
                            val dayOfMonth = it.arguments?.getInt("dayOfMonth")!!
                            val month = it.arguments?.getInt("month")!!
                            val year = it.arguments?.getInt("year")!!
                            SearchScreen(
                                scaffoldState = scaffoldState,
                                mealName = mealName,
                                dayOfMonth = dayOfMonth,
                                month = month,
                                year = year,
                                onNavigateUp = navController::navigateUp,
                                viewModel = searchViewModel,
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun addBreakfast_addedFoodAppearsUnderBreakfast_nutrientsProperlyCalculated() {
        repositoryFake.searchResults = listOf(
            TrackableFood(
                name = "banana",
                imageUrl = null,
                caloriesPer100g = 150,
                carbsPer100g = 50,
                proteinPer100g = 5,
                fatPer100g = 1,
            )
        )
        val addedAmount = 150
        val expectedCalories = (1.5f * 150).roundToInt()
        val expectedCarbs = (1.5f * 50).roundToInt()
        val expectedProtein = (1.5f * 5).roundToInt()
        val expectedFat = (1.5f * 1).roundToInt()

        composeRule
            .onNodeWithText("Add Breakfast")
            .assertDoesNotExist()
        composeRule
            .onNodeWithContentDescription("Breakfast")
            .performClick()
        composeRule
            .onNodeWithText("Add Breakfast")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Add Breakfast")
            .performClick()

        assertThat(
            navController
                .currentDestination
                ?.route
                ?.startsWith(Route.SEARCH)
        ).isTrue()

        composeRule
            .onNodeWithTag("search_text_field")
            .performTextInput("banana")
        composeRule
            .onNodeWithContentDescription("Search...")
            .performClick()

        composeRule
            .onNodeWithText("Carbs")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Amount")
            .performTextInput(addedAmount.toString())
        composeRule
            .onNodeWithText("Add")
            .performClick()

        assertThat(
            navController
                .currentDestination
                ?.route
                ?.startsWith(Route.TRACKER_OVERVIEW)
        ).isTrue()

        composeRule
            .onAllNodesWithText(expectedCarbs.toStr())
            .onFirst()
            .assertIsDisplayed()
        composeRule
            .onAllNodesWithText(expectedProtein.toStr())
            .onFirst()
            .assertIsDisplayed()
        composeRule
            .onAllNodesWithText(expectedFat.toStr())
            .onFirst()
            .assertIsDisplayed()
        composeRule
            .onAllNodesWithText(expectedCalories.toStr())
            .onFirst()
            .assertIsDisplayed()
    }
}
