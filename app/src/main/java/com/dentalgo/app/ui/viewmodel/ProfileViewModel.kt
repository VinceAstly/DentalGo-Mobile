package com.dentalgo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentalgo.app.data.model.*
import com.dentalgo.app.data.repository.ApiRepository
import com.dentalgo.app.data.repository.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/* ──── State holders ──── */
sealed class ProfileState {
    object Idle    : ProfileState()
    object Loading : ProfileState()
    data class Loaded(val user: UserData) : ProfileState()
    data class Success(val message: String, val user: UserData?) : ProfileState()
    data class Error(val message: String) : ProfileState()
    object NetworkError : ProfileState()
}

sealed class PasswordState {
    object Idle    : PasswordState()
    object Loading : PasswordState()
    data class Success(val message: String) : PasswordState()
    data class Error(val message: String) : PasswordState()
    object NetworkError : PasswordState()
}

class ProfileViewModel(private val repository: ApiRepository) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _updateState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val updateState: StateFlow<ProfileState> = _updateState

    private val _passwordState = MutableStateFlow<PasswordState>(PasswordState.Idle)
    val passwordState: StateFlow<PasswordState> = _passwordState

    /* ───── GET PROFILE ───── */
    fun loadProfile(token: String) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            _profileState.value = when (val result = repository.getProfile(token)) {
                is ApiResult.Success -> {
                    val user = result.data.user
                    if (user != null) ProfileState.Loaded(user)
                    else ProfileState.Error(result.data.message.ifBlank { "Failed to load profile." })
                }
                is ApiResult.Error       -> ProfileState.Error(result.message)
                is ApiResult.NetworkError -> ProfileState.NetworkError
            }
        }
    }

    /* ───── UPDATE PROFILE ───── */
    fun updateProfile(token: String, name: String, email: String, phone: String, bio: String) {
        when {
            name.isBlank()  -> { _updateState.value = ProfileState.Error("Name cannot be empty."); return }
            email.isBlank() -> { _updateState.value = ProfileState.Error("Email cannot be empty."); return }
        }
        viewModelScope.launch {
            _updateState.value = ProfileState.Loading
            val result = repository.updateProfile(
                token,
                UpdateProfileRequest(name.trim(), email.trim(), phone.trim(), bio.trim())
            )
            _updateState.value = when (result) {
                is ApiResult.Success  -> ProfileState.Success(result.data.message, result.data.user)
                is ApiResult.Error    -> ProfileState.Error(result.message)
                is ApiResult.NetworkError -> ProfileState.NetworkError
            }
        }
    }

    /* ───── CHANGE PASSWORD ───── */
    fun changePassword(token: String, current: String, newPass: String, confirm: String) {
        when {
            current.isBlank() || newPass.isBlank() || confirm.isBlank() ->
                _passwordState.value = PasswordState.Error("Please fill in all password fields.")
            newPass != confirm ->
                _passwordState.value = PasswordState.Error("New passwords do not match.")
            newPass.length < 8 ->
                _passwordState.value = PasswordState.Error("Password must be at least 8 characters.")
            else -> viewModelScope.launch {
                _passwordState.value = PasswordState.Loading
                val result = repository.changePassword(
                    token,
                    ChangePasswordRequest(current, newPass, confirm)
                )
                _passwordState.value = when (result) {
                    is ApiResult.Success  -> PasswordState.Success(result.data.message)
                    is ApiResult.Error    -> PasswordState.Error(result.message)
                    is ApiResult.NetworkError -> PasswordState.NetworkError
                }
            }
        }
    }

    fun resetUpdate()   { _updateState.value   = ProfileState.Idle }
    fun resetPassword() { _passwordState.value = PasswordState.Idle }
}
