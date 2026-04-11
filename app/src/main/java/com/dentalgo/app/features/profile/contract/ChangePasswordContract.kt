package com.dentalgo.app.features.profile.contract

interface ChangePasswordContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onNetworkError()
        fun onChangePasswordSuccess(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun changePassword(token: String, currentPass: String, newPass: String, confirmPass: String)
    }
}
