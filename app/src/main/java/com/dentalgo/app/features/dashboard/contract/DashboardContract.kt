package com.dentalgo.app.features.dashboard.contract

import com.dentalgo.app.core.models.UserData
import com.dentalgo.app.core.models.AppointmentData

interface DashboardContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onNetworkError()
        fun displayDashboardContent(user: UserData?, appointments: List<AppointmentData>)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadDashboard(token: String)
    }
}
