package com.dentalgo.app.features.appointment.contract

import com.dentalgo.app.features.appointment.data.UserAppointmentItem

interface AppointmentContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onNetworkError()
        fun onBookingSuccess(message: String)
        // Updates UI with fetched appointments
        fun onAppointmentsLoaded(appointments: List<UserAppointmentItem>)
        // Handles duplicate booking attempt
        fun onDuplicateDetected(reason: String)
        // Navigates to summary after validation
        fun onProceedToSummary(service: String, date: String, notes: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        // Loads user's existing appointments
        fun loadUserAppointments(token: String)
        // Validates booking and checks for duplicates
        fun validateAndNavigate(token: String, service: String, date: String, notes: String)
        // Submits the booking to server
        fun confirmBooking(token: String, service: String, date: String, notes: String)
    }
}

