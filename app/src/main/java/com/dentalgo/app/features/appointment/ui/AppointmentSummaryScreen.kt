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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dentalgo.app.features.appointment.contract.AppointmentContract
import com.dentalgo.app.features.appointment.data.UserAppointmentItem
import com.dentalgo.app.ui.components.*
import com.dentalgo.app.ui.theme.*

@Composable
fun AppointmentSummaryScreen(
    presenterProvider: () -> AppointmentContract.Presenter,
    token: String,
    service: String,
    date: String,
    notes: String,
    onBookingSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val presenter = remember { presenterProvider() }

    var isLoading  by remember { mutableStateOf(false) }
    var errorMsg   by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }
    var booked     by remember { mutableStateOf(false) }

    val view = remember {
        object : AppointmentContract.View {
            override fun showLoading()              { isLoading = true; errorMsg = "" }
            override fun hideLoading()              { isLoading = false }
            override fun showError(message: String) { errorMsg = message }
            override fun onNetworkError()           { errorMsg = "No internet connection. Please try again." }
            override fun onDuplicateDetected(reason: String) { errorMsg = reason }
            override fun onProceedToSummary(service: String, date: String, notes: String) {}
            override fun onAppointmentsLoaded(appointments: List<UserAppointmentItem>) {}
            override fun onBookingSuccess(message: String) {
                successMsg = message
                booked     = true
            }
        }
    }

    DisposableEffect(presenter) {
        presenter.attachView(view)
        onDispose { presenter.detachView() }
    }

    // Navigate away once booking confirmed and user has seen the success message
    LaunchedEffect(booked) {
        if (booked) {
            kotlinx.coroutines.delay(2000)
            onBookingSuccess()
        }
    }

    Scaffold(
        bottomBar = {
            SummaryBottomBar(
                onAppointment = onNavigateBack,
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
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = DentalGoOnSurface)
                        }
                        Spacer(Modifier.width(4.dp))
                        Text("Appointments", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = DentalGoOnSurface)
                    }
                    Box(
                        modifier = Modifier.size(42.dp).clip(CircleShape).background(DentalGoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("Appointment Summary", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DentalGoOnSurface, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(4.dp))
                Text("Please review your appointment details before confirming.", fontSize = 13.sp, color = DentalGoTextSecondary, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(20.dp))

                AnimatedVisibility(visible = errorMsg.isNotBlank(), enter = fadeIn(), exit = fadeOut()) {
                    Column { ErrorBanner(errorMsg); Spacer(Modifier.height(12.dp)) }
                }
                AnimatedVisibility(visible = successMsg.isNotBlank(), enter = fadeIn(), exit = fadeOut()) {
                    Column { SuccessBanner(successMsg); Spacer(Modifier.height(12.dp)) }
                }

                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = DentalGoSurface),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(Brush.horizontalGradient(listOf(DentalGoPrimaryDark, DentalGoPrimary, DentalGoAccent)))
                        )

                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(DentalGoPrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = DentalGoPrimary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Text("Appointment Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DentalGoOnSurface)
                            }

                            Spacer(Modifier.height(20.dp))
                            HorizontalDivider(color = DentalGoBorder.copy(alpha = 0.5f))
                            Spacer(Modifier.height(16.dp))

                            SummarySection(label = "Services") {
                                SummaryRow(label = service, value = "")
                            }

                            Spacer(Modifier.height(14.dp))

                            SummarySection(label = "Date & Time") {
                                SummaryRow(label = date.substringAfter("— ").trim().ifBlank { date }, value = "")
                            }

                            if (notes.isNotBlank()) {
                                Spacer(Modifier.height(14.dp))
                                SummarySection(label = "Special Notes") {
                                    Text(notes, fontSize = 14.sp, color = DentalGoTextSecondary, lineHeight = 20.sp)
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = DentalGoBorder.copy(alpha = 0.5f))
                            Spacer(Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Status", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DentalGoLabel)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(DentalGoWarning.copy(alpha = 0.12f))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Pending Confirmation", fontSize = 12.sp, color = DentalGoWarning, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                if (!booked) {
                    DentalGoPrimaryButton(
                        text      = "Confirm Appointment",
                        isLoading = isLoading,
                        enabled   = !isLoading,
                        onClick   = {
                            presenter.confirmBooking(token, service, date, notes)
                        }
                    )
                }

                Spacer(Modifier.height(14.dp))

                if (!booked) {
                    OutlinedButton(
                        onClick  = onNavigateBack,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = DentalGoPrimary),
                        border   = androidx.compose.foundation.BorderStroke(1.5.dp, DentalGoBorder)
                    ) {
                        Text("Go Back & Edit", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }

            if (isLoading) LoadingOverlay()
        }
    }
}

@Composable
private fun SummarySection(label: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DentalGoLabel, modifier = Modifier.padding(bottom = 6.dp))
        content()
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = DentalGoTextSecondary, modifier = Modifier.weight(1f))
        if (value.isNotBlank()) {
            Text(value, fontSize = 14.sp, color = DentalGoOnSurface, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SummaryBottomBar(
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
