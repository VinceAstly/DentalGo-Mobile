package com.dentalgo.app.features.appointment.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dentalgo.app.features.appointment.contract.AppointmentContract
import com.dentalgo.app.features.appointment.data.UserAppointmentItem
import com.dentalgo.app.ui.components.*
import com.dentalgo.app.ui.theme.*

private val dentalServices = listOf(
    "Basic Dental Procedures",
    "Orthodontics",
    "Teeth Whitening",
    "Endodontics",
    "Periodontics",
    "Oral Surgery"
)

private val availableDates = listOf(
    "2026-06-02 — Monday, 9:00 AM",
    "2026-06-02 — Monday, 11:00 AM",
    "2026-06-03 — Tuesday, 10:00 AM",
    "2026-06-03 — Tuesday, 2:00 PM",
    "2026-06-04 — Wednesday, 9:00 AM",
    "2026-06-05 — Thursday, 3:00 PM",
    "2026-06-06 — Friday, 10:00 AM",
    "2026-06-09 — Monday, 1:00 PM",
    "2026-06-10 — Tuesday, 9:00 AM",
    "2026-06-11 — Wednesday, 11:00 AM"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(
    presenterProvider: () -> AppointmentContract.Presenter,
    token: String,
    onNavigateToSummary: (service: String, date: String, notes: String) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val presenter = remember { presenterProvider() }

    var selectedService by remember { mutableStateOf("") }
    var selectedDate    by remember { mutableStateOf("") }
    var notes           by remember { mutableStateOf("") }
    var errorMsg        by remember { mutableStateOf("") }
    var isLoading       by remember { mutableStateOf(false) }

    var serviceExpanded by remember { mutableStateOf(false) }
    var dateExpanded    by remember { mutableStateOf(false) }

    var bookedDates     by remember { mutableStateOf<Set<String>>(emptySet()) }
    var pendingServices by remember { mutableStateOf<Set<String>>(emptySet()) }

    val view = remember {
        object : AppointmentContract.View {
            override fun showLoading()              { isLoading = true; errorMsg = "" }
            override fun hideLoading()              { isLoading = false }
            override fun showError(message: String) { errorMsg = message }
            override fun onNetworkError()           { errorMsg = "No internet connection. Please try again." }
            override fun onBookingSuccess(message: String) {} // handled by summary screen
            override fun onAppointmentsLoaded(appointments: List<UserAppointmentItem>) {
                bookedDates     = appointments.mapNotNull { it.date?.trim()?.lowercase() }.toSet()
                pendingServices = appointments.mapNotNull { it.serviceType?.trim()?.lowercase() }.toSet()
            }
            override fun onDuplicateDetected(reason: String) { errorMsg = reason }
            override fun onProceedToSummary(service: String, date: String, notes: String) {
                onNavigateToSummary(service, date, notes)
            }
        }
    }

    DisposableEffect(presenter) {
        presenter.attachView(view)
        presenter.loadUserAppointments(token)
        onDispose { presenter.detachView() }
    }

    Scaffold(
        bottomBar = {
            AppointmentBottomBar(
                onAppointment = {},
                onDashboard   = onNavigateToDashboard,
                onHistory     = onNavigateToHistory
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DentalGoBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Menu, contentDescription = null, tint = DentalGoOnSurface)
                        Spacer(Modifier.width(10.dp))
                        Text("Appointments", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = DentalGoOnSurface)
                    }
                    Box(
                        modifier = Modifier.size(42.dp).clip(CircleShape).background(DentalGoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(Modifier.height(28.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Book an Appointment", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DentalGoOnSurface)
                }
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Fill in the details below to schedule your visit.", fontSize = 13.sp, color = DentalGoTextSecondary)
                }
                Spacer(Modifier.height(20.dp))

                AnimatedVisibility(visible = errorMsg.isNotBlank(), enter = fadeIn(), exit = fadeOut()) {
                    Column {
                        ErrorBanner(errorMsg)
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Text(
                    text = "Select Services",
                    style = MaterialTheme.typography.labelLarge,
                    color = DentalGoLabel,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = serviceExpanded,
                    onExpandedChange = { if (!isLoading) serviceExpanded = !serviceExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedService.ifBlank { "" },
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isLoading,
                        placeholder = { Text("Choose a service", color = DentalGoTextMuted, fontSize = 14.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceExpanded) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DentalGoPrimary,
                            unfocusedBorderColor = DentalGoBorder,
                            focusedContainerColor = DentalGoSurface,
                            unfocusedContainerColor = DentalGoSurface,
                            focusedTrailingIconColor = DentalGoPrimary,
                            unfocusedTrailingIconColor = DentalGoTextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = serviceExpanded,
                        onDismissRequest = { serviceExpanded = false },
                        modifier = Modifier.background(DentalGoSurface)
                    ) {
                        dentalServices.forEach { service ->
                            val isBooked = service.trim().lowercase() in pendingServices
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            service,
                                            fontSize = 14.sp,
                                            color = if (isBooked) DentalGoTextMuted
                                                    else if (service == selectedService) DentalGoPrimary
                                                    else DentalGoOnSurface,
                                            fontWeight = if (service == selectedService) FontWeight.SemiBold
                                                         else FontWeight.Normal
                                        )
                                        if (isBooked) {
                                            Text(
                                                "Booked",
                                                fontSize = 11.sp,
                                                color = DentalGoWarning,
                                                fontWeight = FontWeight.Medium
                                            )
                                        } else if (service == selectedService) {
                                            Icon(Icons.Filled.Check, contentDescription = null, tint = DentalGoPrimary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                onClick = {
                                    if (!isBooked) {
                                        selectedService = service
                                        errorMsg        = ""
                                    } else {
                                        errorMsg = "You already have a pending \"$service\" appointment."
                                    }
                                    serviceExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Choose Date",
                    style = MaterialTheme.typography.labelLarge,
                    color = DentalGoLabel,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = dateExpanded,
                    onExpandedChange = { if (!isLoading) dateExpanded = !dateExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedDate.ifBlank { "" },
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isLoading,
                        placeholder = { Text("Select available slot", color = DentalGoTextMuted, fontSize = 14.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dateExpanded) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DentalGoPrimary,
                            unfocusedBorderColor = DentalGoBorder,
                            focusedContainerColor = DentalGoSurface,
                            unfocusedContainerColor = DentalGoSurface,
                            focusedTrailingIconColor = DentalGoPrimary,
                            unfocusedTrailingIconColor = DentalGoTextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = dateExpanded,
                        onDismissRequest = { dateExpanded = false },
                        modifier = Modifier.background(DentalGoSurface)
                    ) {
                        availableDates.forEach { slot ->
                            val isBooked = slot.trim().lowercase() in bookedDates
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            slot,
                                            fontSize = 14.sp,
                                            color = if (isBooked) DentalGoTextMuted
                                                    else if (slot == selectedDate) DentalGoPrimary
                                                    else DentalGoOnSurface,
                                            fontWeight = if (slot == selectedDate) FontWeight.SemiBold
                                                         else FontWeight.Normal
                                        )
                                        if (isBooked) {
                                            Text("Booked", fontSize = 11.sp, color = DentalGoWarning, fontWeight = FontWeight.Medium)
                                        } else if (slot == selectedDate) {
                                            Icon(Icons.Filled.Check, contentDescription = null, tint = DentalGoPrimary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                onClick = {
                                    if (!isBooked) {
                                        selectedDate = slot
                                        errorMsg     = ""
                                    } else {
                                        errorMsg = "This time slot is already booked."
                                    }
                                    dateExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                DentalGoTextField(
                    value         = notes,
                    onValueChange = { notes = it; errorMsg = "" },
                    label         = "Special Notes",
                    placeholder   = "Describe any symptoms or special requests…",
                    singleLine    = false,
                    maxLines      = 5,
                    imeAction     = ImeAction.Default,
                    enabled       = !isLoading
                )

                Spacer(Modifier.height(28.dp))

                DentalGoPrimaryButton(
                    text      = "Review & Confirm",
                    isLoading = isLoading,
                    enabled   = !isLoading,
                    onClick   = {
                        presenter.validateAndNavigate(token, selectedService, selectedDate, notes)
                    }
                )

                Spacer(Modifier.height(24.dp))

                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = DentalGoPrimary.copy(alpha = 0.07f)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = DentalGoPrimary, modifier = Modifier.size(20.dp).padding(top = 1.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Appointments are subject to dentist availability. You will receive a confirmation once your booking is approved.",
                            fontSize = 12.sp, color = DentalGoTextSecondary, lineHeight = 18.sp
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            if (isLoading) LoadingOverlay()
        }
    }
}

@Composable
private fun AppointmentBottomBar(
    onAppointment: () -> Unit,
    onDashboard: () -> Unit,
    onHistory: () -> Unit
) {
    NavigationBar(containerColor = DentalGoSurface, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = true, onClick = onAppointment,
            icon = { Icon(Icons.Filled.CalendarToday, "Appointment") },
            label = { Text("Appointment", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = DentalGoPrimary, indicatorColor = DentalGoPrimary.copy(alpha = 0.12f), unselectedIconColor = DentalGoTextSecondary)
        )
        NavigationBarItem(
            selected = false, onClick = onDashboard,
            icon = { Icon(Icons.Filled.Home, "Dashboard") },
            label = { Text("Dashboard", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = DentalGoPrimary, unselectedIconColor = DentalGoTextSecondary)
        )
        NavigationBarItem(
            selected = false, onClick = onHistory,
            icon = { Icon(Icons.Filled.History, "History") },
            label = { Text("History", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = DentalGoPrimary, unselectedIconColor = DentalGoTextSecondary)
        )
    }
}
