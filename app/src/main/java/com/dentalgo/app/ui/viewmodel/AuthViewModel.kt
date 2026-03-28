package com.dentalgo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentalgo.app.data.model.LoginRequest
import com.dentalgo.app.data.model.RegisterRequest
import com.dentalgo.app.data.repository.ApiRepository
import com.dentalgo.app.data.repository.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/* ──── State holders ──── */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String, val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object NetworkError : AuthState()
}

class AuthViewModel(private val repository: ApiRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    /* ───── LOGIN ───── */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("Please fill in all fields.")
            return
        }
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val result = repository.login(LoginRequest(email.trim(), password))
            _loginState.value = when (result) {
                is ApiResult.Success -> {
                    val token = result.data.token
                    if (token != null) {
                        AuthState.Success(token, result.data.message)
                    } else {
                        AuthState.Error(result.data.message.ifBlank { "Login failed." })
                    }
                }
                is ApiResult.Error -> AuthState.Error(result.message)
                is ApiResult.NetworkError -> AuthState.NetworkError
            }
        }
    }

    /* ───── REGISTER ───── */
    fun register(name: String, email: String, password: String, confirmPassword: String, phone: String) {
        when {
            name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank() ->
                _registerState.value = AuthState.Error("Please fill in all fields.")
            password != confirmPassword ->
                _registerState.value = AuthState.Error("Passwords do not match.")
            password.length < 8 ->
                _registerState.value = AuthState.Error("Password must be at least 8 characters.")
            else -> viewModelScope.launch {
                _registerState.value = AuthState.Loading
                val result = repository.register(
                    RegisterRequest(
                        name = name.trim(),
                        email = email.trim(),
                        password = password,
                        password_confirmation = confirmPassword,
                        phone = phone.trim()
                    )
                )
                _registerState.value = when (result) {
                    is ApiResult.Success -> {
                        val token = result.data.token
                        if (token != null) {
                            AuthState.Success(token, result.data.message)
                        } else {
                            AuthState.Error(result.data.message.ifBlank { "Registration failed." })
                        }
                    }
                    is ApiResult.Error  -> AuthState.Error(result.message)
                    is ApiResult.NetworkError -> AuthState.NetworkError
                }
            }
        }
    }

    fun resetLogin()    { _loginState.value    = AuthState.Idle }
    fun resetRegister() { _registerState.value = AuthState.Idle }
}
