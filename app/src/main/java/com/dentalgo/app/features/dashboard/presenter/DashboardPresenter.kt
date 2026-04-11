package com.dentalgo.app.features.dashboard.presenter

import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.features.dashboard.contract.DashboardContract
import com.dentalgo.app.features.dashboard.data.DashboardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DashboardPresenter(
    private val repository: DashboardRepository,
    private val scope: CoroutineScope
) : DashboardContract.Presenter {

    private var view: DashboardContract.View? = null

    override fun attachView(view: DashboardContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadDashboard(token: String) {
        view?.showLoading()
        scope.launch {
            when (val result = repository.getDashboard(token)) {
                is ApiResult.Success -> {
                    view?.displayDashboardContent(
                        user = result.data.user,
                        appointments = result.data.appointments ?: emptyList()
                    )
                }
                is ApiResult.Error -> view?.showError(result.message)
                is ApiResult.NetworkError -> view?.onNetworkError()
            }
            view?.hideLoading()
        }
    }
}
