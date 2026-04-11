package com.dentalgo.app.features.profile.presenter

import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.features.profile.contract.ProfileContract
import com.dentalgo.app.features.profile.data.ProfileRepository
import com.dentalgo.app.features.profile.data.UpdateProfileRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ProfilePresenter(
    private val repository: ProfileRepository,
    private val scope: CoroutineScope
) : ProfileContract.Presenter {

    private var view: ProfileContract.View? = null

    override fun attachView(view: ProfileContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadProfile(token: String) {
        view?.showLoading()
        scope.launch {
            when (val result = repository.getProfile(token)) {
                is ApiResult.Success -> {
                    result.data.user?.let { view?.displayProfile(it) }
                        ?: view?.showError("Failed to load profile data.")
                }
                is ApiResult.Error -> view?.showError(result.message)
                is ApiResult.NetworkError -> view?.onNetworkError()
            }
            view?.hideLoading()
        }
    }

    override fun updateProfile(token: String, name: String, email: String, phone: String, bio: String) {
        if (name.isBlank() || email.isBlank()) {
            view?.showError("Name and email cannot be empty.")
            return
        }
        view?.showLoading()
        scope.launch {
            val request = UpdateProfileRequest(name, email, phone, bio)
            when (val result = repository.updateProfile(token, request)) {
                is ApiResult.Success -> view?.onUpdateSuccess(result.data.message, result.data.user)
                is ApiResult.Error -> view?.showError(result.message)
                is ApiResult.NetworkError -> view?.onNetworkError()
            }
            view?.hideLoading()
        }
    }
}
