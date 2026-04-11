package com.dentalgo.app.features.profile.presenter

import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.features.profile.contract.ChangePasswordContract
import com.dentalgo.app.features.profile.data.ChangePasswordRequest
import com.dentalgo.app.features.profile.data.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ChangePasswordPresenter(
    private val repository: ProfileRepository,
    private val scope: CoroutineScope
) : ChangePasswordContract.Presenter {

    private var view: ChangePasswordContract.View? = null

    override fun attachView(view: ChangePasswordContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun changePassword(token: String, currentPass: String, newPass: String, confirmPass: String) {
        when {
            currentPass.isBlank() || newPass.isBlank() || confirmPass.isBlank() ->
                view?.showError("Please fill in all fields.")
            newPass != confirmPass ->
                view?.showError("New passwords do not match.")
            newPass.length < 8 ->
                view?.showError("Password must be at least 8 characters.")
            else -> {
                view?.showLoading()
                scope.launch {
                    val request = ChangePasswordRequest(currentPass, newPass, confirmPass)
                    when (val result = repository.changePassword(token, request)) {
                        is ApiResult.Success -> view?.onChangePasswordSuccess(result.data.message)
                        is ApiResult.Error -> view?.showError(result.message)
                        is ApiResult.NetworkError -> view?.onNetworkError()
                    }
                    view?.hideLoading()
                }
            }
        }
    }
}
