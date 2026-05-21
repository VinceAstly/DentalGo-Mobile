package com.dentalgo.app.features.appointment.presenter

import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.features.appointment.contract.AppointmentContract
import com.dentalgo.app.features.appointment.data.AppointmentRepository
import com.dentalgo.app.features.appointment.data.BookAppointmentRequest
import com.dentalgo.app.features.appointment.data.UserAppointmentItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppointmentPresenter(
    private val repository: AppointmentRepository,
    private val scope: CoroutineScope
) : AppointmentContract.Presenter {

    private var view: AppointmentContract.View? = null

    /** In-memory cache of the user's existing (pending) appointments. */
    private var existingAppointments: List<UserAppointmentItem> = emptyList()

    override fun attachView(view: AppointmentContract.View) { this.view = view }
    override fun detachView() { this.view = null }

    /* ── Load existing appointments ── */
    override fun loadUserAppointments(token: String) {
        scope.launch {
            when (val result = repository.getUserAppointments(token)) {
                is ApiResult.Success -> {
                    existingAppointments = result.data.filter {
                        it.status?.lowercase() !in listOf("cancelled", "completed")
                    }
                    view?.onAppointmentsLoaded(existingAppointments)
                }
                is ApiResult.Error        -> view?.onAppointmentsLoaded(emptyList())
                is ApiResult.NetworkError -> view?.onAppointmentsLoaded(emptyList())
            }
        }
    }

    /* ── Step 1: Validate + duplicate check, then navigate to summary ── */
    override fun validateAndNavigate(token: String, service: String, date: String, notes: String) {
        if (service.isBlank()) { view?.showError("Please select a service."); return }
        if (date.isBlank())    { view?.showError("Please choose a date.");    return }

        val normService = service.trim().lowercase()
        val normDate    = date.trim().lowercase()

        val slotConflict = existingAppointments.any {
            it.date?.trim()?.lowercase() == normDate
        }
        if (slotConflict) {
            view?.onDuplicateDetected(
                "This time slot is already booked. Please choose a different date or time."
            )
            return
        }

        val serviceConflict = existingAppointments.any {
            it.serviceType?.trim()?.lowercase() == normService
        }
        if (serviceConflict) {
            view?.onDuplicateDetected(
                "You already have a pending \"$service\" appointment. " +
                "Please wait for it to be completed or cancelled before booking again."
            )
            return
        }

        // All clear — proceed to summary screen
        view?.onProceedToSummary(service, date, notes)
    }

    /* ── Step 2: Actually POST the booking (called from summary screen) ── */
    override fun confirmBooking(token: String, service: String, date: String, notes: String) {
        view?.showLoading()
        scope.launch {
            val request = BookAppointmentRequest(
                email       = token,
                serviceType = service,
                date        = date,
                notes       = notes
            )
            when (val result = repository.bookAppointment(request)) {
                is ApiResult.Success -> {
                    // Update local cache so duplicate guard works without re-fetching
                    existingAppointments = existingAppointments + UserAppointmentItem(
                        id          = result.data.id,
                        email       = result.data.email,
                        serviceType = result.data.serviceType,
                        date        = result.data.date,
                        notes       = result.data.notes,
                        status      = result.data.status ?: "pending"
                    )
                    view?.onBookingSuccess("Appointment booked successfully!")
                    view?.onAppointmentsLoaded(existingAppointments)
                }
                is ApiResult.Error        -> view?.showError(result.message)
                is ApiResult.NetworkError -> view?.onNetworkError()
            }
            view?.hideLoading()
        }
    }
}
