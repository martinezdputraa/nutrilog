package com.martinezdputra.nutrilog.core.domain.model

sealed class Gender(val name: String) {
    object Male: Gender("male")
    object Female: Gender("female")

    companion object {
        fun fromString(name: String): Gender {
            return when(name.lowercase()) {
                "male" -> Male
                "female" -> Female
                else -> Female
            }
        }
    }
}
