package com.dentalgo.app.features.appointment.contract

import com.dentalgo.app.features.appointment.data.UserAppointmentItem

interface AppointmentContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onNetworkError()
        fun onBookingSuccess(message: String)
        fun onAppointmentsLoaded(appointments: List<UserAppointmentItem>)
        fun onDuplicateDetected(reason: String)
        fun onProceedToSummary(service: String, date: String, notes: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadUserAppointments(token: String)
        fun validateAndNavigate(token: String, service: String, date: String, notes: String)
        fun confirmBooking(token: String, service: String, date: String, notes: String)
    }
}

