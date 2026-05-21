package com.dentalgo.app.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dentalgo.app.core.models.AppointmentData
import com.dentalgo.app.core.models.UserData
import com.dentalgo.app.features.profile.contract.ProfileContract
import com.dentalgo.app.ui.components.*
import com.dentalgo.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    presenterProvider: () -> ProfileContract.Presenter,
    token: String,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToAppointment: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val presenter = remember { presenterProvider() }

    var nameField  by remember { mutableStateOf("") }
    var emailField by remember { mutableStateOf("") }
    var phoneField by remember { mutableStateOf("") }
    var bioField   by remember { mutableStateOf("") }

    var successMsg   by remember { mutableStateOf("") }
    var errorMsg     by remember { mutableStateOf("") }
    var isLoading    by remember { mutableStateOf(false) }
    var user         by remember { mutableStateOf<UserData?>(null) }
    var appointments by remember { mutableStateOf<List<AppointmentData>>(emptyList()) }

    val view = remember {
        object : ProfileContract.View {
            override fun showLoading() { isLoading = true; errorMsg = ""; successMsg = "" }
            override fun hideLoading() { isLoading = false }
            override fun showError(message: String) { errorMsg = message }
            override fun onNetworkError() { errorMsg = "No internet connection." }
            override fun displayProfile(loadedUser: UserData) {
                user = loadedUser
                nameField = loadedUser.name
                emailField = loadedUser.email
                phoneField = loadedUser.phone ?: ""
                bioField = loadedUser.bio ?: ""
            }
            override fun onUpdateSuccess(message: String, updatedUser: UserData?) {
                successMsg = message.ifBlank { "Profile updated successfully." }
                updatedUser?.let {
                    user = it
                    nameField = it.name
                    emailField = it.email
                    phoneField = it.phone ?: ""
                    bioField = it.bio ?: ""
                }
            }
            override fun displayAppointments(loaded: List<AppointmentData>) {
                appointments = loaded
            }
        }
    }

    DisposableEffect(presenter) {
        presenter.attachView(view)
        presenter.loadProfile(token)
        onDispose { presenter.detachView() }
    }

    val displayName = user?.name ?: nameField.ifBlank { "Loading…" }

    Scaffold(
        bottomBar = {
            ProfileBottomBar(
                onAppointment = onNavigateToAppointment,
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

                // Page title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Menu, contentDescription = null, tint = DentalGoOnSurface)
                        Spacer(Modifier.width(10.dp))
                        Text("Profile", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = DentalGoOnSurface)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = DentalGoError)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(DentalGoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (displayName.isNotEmpty()) displayName.take(1).uppercase() else "?",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(10.dp))
                Text(displayName, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = DentalGoOnSurface)

                Spacer(Modifier.height(24.dp))

                // Banners
                if (errorMsg.isNotBlank())   { ErrorBanner(errorMsg); Spacer(Modifier.height(12.dp)) }
                if (successMsg.isNotBlank()) { SuccessBanner(successMsg); Spacer(Modifier.height(12.dp)) }

                // Form
                DentalGoTextField(
                    value = nameField, onValueChange = { nameField = it; errorMsg = ""; successMsg = "" },
                    label = "Full Name", placeholder = "Enter your full name",
                    imeAction = ImeAction.Next, enabled = !isLoading
                )
                Spacer(Modifier.height(14.dp))

                DentalGoTextField(
                    value = emailField, onValueChange = { emailField = it; errorMsg = ""; successMsg = "" },
                    label = "Email", placeholder = "Enter your email",
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next, enabled = !isLoading
                )
                Spacer(Modifier.height(14.dp))

                DentalGoTextField(
                    value = phoneField, onValueChange = { phoneField = it; successMsg = "" },
                    label = "Phone Number", placeholder = "Enter your phone number",
                    keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next, enabled = !isLoading
                )
                Spacer(Modifier.height(14.dp))

                DentalGoTextField(
                    value = bioField, onValueChange = { bioField = it },
                    label = "Bio", placeholder = "What's on your mind?",
                    imeAction = ImeAction.Done, singleLine = false, maxLines = 4,
                    enabled = !isLoading
                )

                // Change Password link
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = "Change Password?",
                        color = DentalGoPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable { onNavigateToChangePassword() }
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Update Button
                DentalGoPrimaryButton(
                    text = "Update Changes",
                    onClick = {
                        presenter.updateProfile(token, nameField, emailField, phoneField, bioField)
                    },
                    isLoading = isLoading,
                    enabled   = !isLoading
                )

                Spacer(Modifier.height(28.dp))

                // Appointments preview section
                if (user != null || appointments.isNotEmpty()) {
                    AppointmentsSummarySection(appointments)
                }

                Spacer(Modifier.height(24.dp))
            }

            if (isLoading) LoadingOverlay()
        }
    }
}

@Composable
private fun AppointmentsSummarySection(appointments: List<AppointmentData>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppointmentColumn(title = "Pending Appointments", items = appointments.filter { it.status == "pending" })
        AppointmentColumn(title = "History Appointments", items = appointments.filter { it.status == "completed" })
    }
}

@Composable
private fun RowScope.AppointmentColumn(title: String, items: List<AppointmentData>) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DentalGoSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = DentalGoOnSurface)
            Spacer(Modifier.height(8.dp))
            if (items.isEmpty()) {
                Text("None", fontSize = 12.sp, color = DentalGoTextMuted)
            } else {
                items.take(3).forEach { appt ->
                    Text(
                        text = appt.serviceType ?: "—",
                        fontSize = 12.sp,
                        color = DentalGoTextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileBottomBar(
    onAppointment: () -> Unit,
    onDashboard: () -> Unit,
    onHistory: () -> Unit
) {
    NavigationBar(
        containerColor = DentalGoSurface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onAppointment,
            icon = { Icon(Icons.Filled.CalendarToday, "Appointment") },
            label = { Text("Appointment", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = DentalGoPrimary,
                unselectedIconColor = DentalGoTextSecondary
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onDashboard,
            icon = { Icon(Icons.Filled.Home, "Dashboard") },
            label = { Text("Dashboard", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = DentalGoPrimary,
                unselectedIconColor = DentalGoTextSecondary
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onHistory,
            icon = { Icon(Icons.Filled.History, "History") },
            label = { Text("History", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = DentalGoPrimary,
                unselectedIconColor = DentalGoTextSecondary
            )
        )
    }
}
