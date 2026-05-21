package com.dentalgo.app.features.profile.presenter

import com.dentalgo.app.core.models.UserData
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
            // token is actually the user's email
            when (val result = repository.getProfile(token)) {
                is ApiResult.Success -> {
                    val r = result.data
                    val userData = UserData(
                        id     = r.id,
                        name   = r.fullName ?: "",
                        email  = r.email ?: token,
                        phone  = r.phone,
                        bio    = r.bio,
                        avatar = null
                    )
                    view?.displayProfile(userData)
                }
                is ApiResult.Error        -> view?.showError(result.message)
                is ApiResult.NetworkError -> view?.onNetworkError()
            }
            
            // Fetch appointments if profile load was successful (or even if not, we can try)
            when (val appointmentsResult = repository.getAppointments(token)) {
                is ApiResult.Success -> {
                    view?.displayAppointments(appointmentsResult.data)
                }
                else -> {
                    view?.displayAppointments(emptyList())
                }
            }
            view?.hideLoading()
        }
    }

    override fun updateProfile(token: String, name: String, email: String, phone: String, bio: String) {
        if (name.isBlank()) {
            view?.showError("Name cannot be empty.")
            return
        }
        view?.showLoading()
        scope.launch {
            // Backend path uses email; token IS the email here
            val request = UpdateProfileRequest(fullName = name, phone = phone, bio = bio)
            when (val result = repository.updateProfile(token, request)) {
                is ApiResult.Success -> {
                    val updated = UserData(
                        id     = null,
                        name   = name,
                        email  = token,
                        phone  = phone,
                        bio    = bio,
                        avatar = null
                    )
                    view?.onUpdateSuccess(result.data ?: "Profile updated successfully!", updated)
                }
                is ApiResult.Error        -> view?.showError(result.message)
                is ApiResult.NetworkError -> view?.onNetworkError()
            }
            view?.hideLoading()
        }
    }
}

