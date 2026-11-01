package com.example.recipeapp.di

import com.example.recipeapp.AppViewModel
import com.example.recipeapp.auth.AuthRepository
import com.example.recipeapp.auth.AuthRepositoryImpl
import com.example.recipeapp.auth.AuthViewModel
import com.example.recipeapp.auth.GetUsernameUseCase
import com.example.recipeapp.auth.IsLoggedInUseCase
import com.example.recipeapp.auth.SetLoggedInUseCase
import com.example.recipeapp.auth.ValidatePasswordUseCase
import com.example.recipeapp.auth.ValidateUsernameUseCase
import com.example.recipeapp.home.HomeViewModel
import com.example.recipeapp.onboarding.IsOnboardingCompletedUseCase
import com.example.recipeapp.onboarding.OnboardingRepository
import com.example.recipeapp.onboarding.OnboardingRepositoryImpl
import com.example.recipeapp.onboarding.OnboardingViewModel
import com.example.recipeapp.onboarding.SetOnboardingCompletedUseCase
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<Settings> { Settings() }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    factory { IsOnboardingCompletedUseCase(get()) }
    factory { SetOnboardingCompletedUseCase(get()) }
    factory { IsLoggedInUseCase(get()) }
    factory { SetLoggedInUseCase(get()) }
    factory { GetUsernameUseCase(get()) }
    factory { ValidateUsernameUseCase() }
    factory { ValidatePasswordUseCase() }

    viewModel { AppViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
}
