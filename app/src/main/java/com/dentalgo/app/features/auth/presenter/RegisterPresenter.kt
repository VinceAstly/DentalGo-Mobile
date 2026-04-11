package com.dentalgo.app.features.auth.presenter

import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.features.auth.contract.RegisterContract
import com.dentalgo.app.features.auth.data.AuthRepository
import com.dentalgo.app.features.auth.data.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RegisterPresenter(
    private val repository: AuthRepository,
    private val scope: CoroutineScope
) : RegisterContract.Presenter {

    private var view: RegisterContract.View? = null

    override fun attachView(view: RegisterContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun register(
        name: String, email: String, password: String, confirmPassword: String, phone: String
    ) {
        when {
            name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank() ->
                view?.showError("Please fill in all fields.")
            password != confirmPassword ->
                view?.showError("Passwords do not match.")
            password.length < 8 ->
                view?.showError("Password must be at least 8 characters.")
            else -> {
                view?.showLoading()
                scope.launch {
                    val request = RegisterRequest(
                        name = name.trim(),
                        email = email.trim(),
                        password = password,
                        password_confirmation = confirmPassword,
                        phone = phone.trim()
                    )
                    when (val result = repository.register(request)) {
                        is ApiResult.Success -> {
                            val token = result.data.token
                            if (token != null) {
                                view?.onRegisterSuccess(token)
                            } else {
                                view?.showError(result.data.message.ifBlank { "Registration failed." })
                            }
                        }
                        is ApiResult.Error -> view?.showError(result.message)
                        is ApiResult.NetworkError -> view?.onNetworkError()
                    }
                    view?.hideLoading()
                }
            }
        }
    }
}
