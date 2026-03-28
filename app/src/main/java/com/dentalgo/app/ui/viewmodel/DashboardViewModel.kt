package com.dentalgo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentalgo.app.data.model.AppointmentData
import com.dentalgo.app.data.model.UserData
import com.dentalgo.app.data.repository.ApiRepository
import com.dentalgo.app.data.repository.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DashboardState {
    object Idle    : DashboardState()
    object Loading : DashboardState()
    data class Loaded(
        val user: UserData?,
        val appointments: List<AppointmentData>
    ) : DashboardState()
    data class Error(val message: String) : DashboardState()
    object NetworkError : DashboardState()
}

class DashboardViewModel(private val repository: ApiRepository) : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val dashboardState: StateFlow<DashboardState> = _dashboardState

    fun loadDashboard(token: String) {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading
            _dashboardState.value = when (val result = repository.getDashboard(token)) {
                is ApiResult.Success -> DashboardState.Loaded(
                    user         = result.data.user,
                    appointments = result.data.appointments ?: emptyList()
                )
                is ApiResult.Error        -> DashboardState.Error(result.message)
                is ApiResult.NetworkError -> DashboardState.NetworkError
            }
        }
    }
}
