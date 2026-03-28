package com.dentalgo.app.ui.screens

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
import com.dentalgo.app.data.model.AppointmentData
import com.dentalgo.app.ui.components.*
import com.dentalgo.app.ui.theme.*
import com.dentalgo.app.ui.viewmodel.ProfileState
import com.dentalgo.app.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    token: String,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToAppointment: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onLogout: () -> Unit
) {
    var nameField  by remember { mutableStateOf("") }
    var emailField by remember { mutableStateOf("") }
    var phoneField by remember { mutableStateOf("") }
    var bioField   by remember { mutableStateOf("") }

    var successMsg by remember { mutableStateOf("") }
    var errorMsg   by remember { mutableStateOf("") }

    val profileState by viewModel.profileState.collectAsState()
    val updateState  by viewModel.updateState.collectAsState()
    val isUpdating   = updateState is ProfileState.Loading

    // Load profile on first composition
    LaunchedEffect(Unit) {
        viewModel.loadProfile(token)
    }

    // Populate fields when profile loads
    LaunchedEffect(profileState) {
        if (profileState is ProfileState.Loaded) {
            val u = (profileState as ProfileState.Loaded).user
            nameField  = u.name
            emailField = u.email
            phoneField = u.phone  ?: ""
            bioField   = u.bio    ?: ""
        }
    }

    // Update result
    LaunchedEffect(updateState) {
        when (val s = updateState) {
            is ProfileState.Success -> {
                successMsg = s.message.ifBlank { "Profile updated successfully." }
                errorMsg   = ""
                s.user?.let { u ->
                    nameField  = u.name
                    emailField = u.email
                    phoneField = u.phone  ?: ""
                    bioField   = u.bio    ?: ""
                }
                viewModel.resetUpdate()
            }
            is ProfileState.Error   -> { errorMsg = s.message; successMsg = "" }
            is ProfileState.NetworkError -> { errorMsg = "No internet connection."; successMsg = "" }
            else -> {}
        }
    }

    val displayName = when (val s = profileState) {
        is ProfileState.Loaded -> s.user.name
        else -> nameField.ifBlank { "Loading…" }
    }

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
                    // Logout
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
                        text = displayName.take(1).uppercase(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(10.dp))
                Text(displayName, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = DentalGoOnSurface)

                Spacer(Modifier.height(24.dp))

                // Loading skeleton
                if (profileState is ProfileState.Loading) {
                    CircularProgressIndicator(color = DentalGoPrimary)
                    Spacer(Modifier.height(16.dp))
                }

                // Banners
                if (errorMsg.isNotBlank())   { ErrorBanner(errorMsg); Spacer(Modifier.height(12.dp)) }
                if (successMsg.isNotBlank()) { SuccessBanner(successMsg); Spacer(Modifier.height(12.dp)) }

                // Form
                DentalGoTextField(
                    value = nameField, onValueChange = { nameField = it; errorMsg = ""; successMsg = "" },
                    label = "Full Name", placeholder = "Enter your full name",
                    imeAction = ImeAction.Next, enabled = !isUpdating
                )
                Spacer(Modifier.height(14.dp))

                DentalGoTextField(
                    value = emailField, onValueChange = { emailField = it; errorMsg = ""; successMsg = "" },
                    label = "Email", placeholder = "Enter your email",
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next, enabled = !isUpdating
                )
                Spacer(Modifier.height(14.dp))

                DentalGoTextField(
                    value = phoneField, onValueChange = { phoneField = it; successMsg = "" },
                    label = "Phone Number", placeholder = "Enter your phone number",
                    keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next, enabled = !isUpdating
                )
                Spacer(Modifier.height(14.dp))

                DentalGoTextField(
                    value = bioField, onValueChange = { bioField = it },
                    label = "Bio", placeholder = "What's on your mind?",
                    imeAction = ImeAction.Done, singleLine = false, maxLines = 4,
                    enabled = !isUpdating
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
                        viewModel.updateProfile(token, nameField, emailField, phoneField, bioField)
                    },
                    isLoading = isUpdating,
                    enabled   = !isUpdating
                )

                Spacer(Modifier.height(28.dp))

                // Appointments preview section
                if (profileState is ProfileState.Loaded) {
                    val appointments = emptyList<AppointmentData>() // From dashboard state in a real scenario
                    AppointmentsSummarySection(appointments)
                }

                Spacer(Modifier.height(24.dp))
            }

            if (isUpdating) LoadingOverlay()
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
                        text = appt.service,
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
