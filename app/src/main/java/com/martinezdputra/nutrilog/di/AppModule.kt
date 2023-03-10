package com.martinezdputra.nutrilog.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.martinezdputra.nutrilog.core.data.preferences.DefaultPreferences
import com.martinezdputra.nutrilog.core.domain.preferences.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferences(sharedPref: SharedPreferences): Preferences = DefaultPreferences(sharedPref)

    @Provides
    @Singleton
    fun provideSharedPreferences(
        app: Application
    ): SharedPreferences = app.getSharedPreferences("shared_pref", MODE_PRIVATE)
}
