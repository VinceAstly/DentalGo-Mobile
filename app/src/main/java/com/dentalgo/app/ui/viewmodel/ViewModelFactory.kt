package com.dentalgo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dentalgo.app.data.repository.ApiRepository

/**
 * Factory that injects ApiRepository into all ViewModels.
 * Usage: ViewModelProvider(this, ViewModelFactory(repository))[MyViewModel::class.java]
 */
class ViewModelFactory(private val repository: ApiRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java)      -> AuthViewModel(repository)      as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java)   -> ProfileViewModel(repository)   as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
