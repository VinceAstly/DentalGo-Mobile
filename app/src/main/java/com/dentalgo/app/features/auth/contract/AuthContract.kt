package com.dentalgo.app.features.auth.contract

interface LoginContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onNetworkError()
        fun onLoginSuccess(token: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun login(email: String, password: String)
    }
}

interface RegisterContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onNetworkError()
        fun onRegisterSuccess(token: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun register(name: String, email: String, password: String, confirmPassword: String, phone: String)
    }
}
