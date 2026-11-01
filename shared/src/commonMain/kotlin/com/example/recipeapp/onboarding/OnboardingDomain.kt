package com.example.recipeapp.onboarding

import com.russhwolf.settings.Settings

interface OnboardingRepository {
    fun isCompleted(): Boolean
    fun setCompleted(value: Boolean)
}

class OnboardingRepositoryImpl(private val settings: Settings) : OnboardingRepository {
    override fun isCompleted(): Boolean = settings.getBoolean(KEY_ONBOARDING_DONE, false)
    override fun setCompleted(value: Boolean) = settings.putBoolean(KEY_ONBOARDING_DONE, value)

    private companion object {
        const val KEY_ONBOARDING_DONE = "onboarding_completed"
    }
}

class SetOnboardingCompletedUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(completed: Boolean = true) = repository.setCompleted(completed)
}

class IsOnboardingCompletedUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(): Boolean = repository.isCompleted()
}
