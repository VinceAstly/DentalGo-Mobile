package com.dentalgo.app.features.profile.contract

import com.dentalgo.app.core.models.UserData

interface ProfileContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onNetworkError()
        fun displayProfile(user: UserData)
        fun onUpdateSuccess(message: String, user: UserData?)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadProfile(token: String)
        fun updateProfile(token: String, name: String, email: String, phone: String, bio: String)
    }
}
