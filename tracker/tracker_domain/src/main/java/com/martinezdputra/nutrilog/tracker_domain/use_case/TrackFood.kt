package com.martinezdputra.nutrilog.tracker_domain.use_case

import com.martinezdputra.nutrilog.tracker_domain.model.MealType
import com.martinezdputra.nutrilog.tracker_domain.model.TrackableFood
import com.martinezdputra.nutrilog.tracker_domain.model.TrackedFood
import com.martinezdputra.nutrilog.tracker_domain.repository.TrackerRepository
import java.time.LocalDate
import kotlin.math.roundToInt

class TrackFood(
    private val repository: TrackerRepository
) {
    suspend operator fun invoke(
        food: TrackableFood,
        amount: Int,
        mealType: MealType,
        date: LocalDate
    ) {
        repository.insertTrackedFood(
            TrackedFood(
                name = food.name,
                carbs = ((food.carbsPer100g / 100F) * amount).roundToInt(),
                protein = ((food.proteinPer100g / 100F) * amount).roundToInt(),
                fat = ((food.fatPer100g / 100F) * amount).roundToInt(),
                calories = ((food.caloriesPer100g / 100F) * amount).roundToInt(),
                imageUrl = food.imageUrl,
                mealType = mealType,
                amount = amount,
                date = date,
            )
        )
    }
}
