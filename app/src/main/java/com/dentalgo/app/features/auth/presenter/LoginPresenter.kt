package com.dentalgo.app.features.auth.presenter

import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.features.auth.contract.LoginContract
import com.dentalgo.app.features.auth.data.AuthRepository
import com.dentalgo.app.features.auth.data.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LoginPresenter(
    private val repository: AuthRepository,
    private val scope: CoroutineScope
) : LoginContract.Presenter {

    private var view: LoginContract.View? = null

    override fun attachView(view: LoginContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            view?.showError("Please fill in all fields.")
            return
        }

        view?.showLoading()
        scope.launch {
            when (val result = repository.login(LoginRequest(email.trim(), password))) {
                is ApiResult.Success -> {
                    val token = result.data.token
                    if (token != null) {
                        view?.onLoginSuccess(token)
                    } else {
                        view?.showError(result.data.message.ifBlank { "Login failed." })
                    }
                }
                is ApiResult.Error -> view?.showError(result.message)
                is ApiResult.NetworkError -> view?.onNetworkError()
            }
            view?.hideLoading()
        }
    }
}
