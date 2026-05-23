package com.dentalgo.app.features.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.dentalgo.app.core.models.UserData
import com.dentalgo.app.features.dashboard.contract.DashboardContract
import com.dentalgo.app.core.models.AppointmentData
import com.dentalgo.app.ui.theme.*

data class DentalService(val name: String, val icon: ImageVector)

private val dentalServices = listOf(
    DentalService("Basic Dental Procedures", Icons.Filled.MedicalServices),
    DentalService("Orthodontics",            Icons.Filled.AutoAwesome),
    DentalService("Teeth Whitening",         Icons.Filled.Brightness7),
    DentalService("Endodontics",             Icons.Filled.Healing),
    DentalService("Periodontics",            Icons.Filled.MonitorHeart),
    DentalService("Oral Surgery",            Icons.Filled.LocalHospital)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    presenterProvider: () -> DashboardContract.Presenter,
    token: String,
    initialUserName: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToAppointment: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val presenter = remember { presenterProvider() }

    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var user by remember { mutableStateOf<UserData?>(null) }
    var appointments by remember { mutableStateOf<List<AppointmentData>>(emptyList()) }

    val view = remember {
        object : DashboardContract.View {
            override fun showLoading() { isLoading = true; errorMsg = "" }
            override fun hideLoading() { isLoading = false }
            override fun showError(message: String) { errorMsg = message }
            override fun onNetworkError() { errorMsg = "No internet connection." }
            override fun displayDashboardContent(loadedUser: UserData?, loadedAppointments: List<AppointmentData>) {
                user = loadedUser
                appointments = loadedAppointments
            }
        }
    }

    DisposableEffect(presenter) {
        presenter.attachView(view)
        presenter.loadDashboard(token)
        onDispose { presenter.detachView() }
    }

    Scaffold(
        bottomBar = {
            DashboardBottomBar(
                onAppointment = onNavigateToAppointment,
                onDashboard   = { /* already here */ },
                onHistory     = onNavigateToHistory
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DentalGoBackground)
                .padding(innerPadding)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = DentalGoOnSurface,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Dashboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DentalGoOnSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DentalGoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(DentalGoPrimaryDark, DentalGoPrimary)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Elevate Your Smile with\nSeamless Dental Care.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White,
                            lineHeight = 26.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "\"DentalGo has completely transformed how I manage my appointments. It's reliable, efficient, and ensures my dental health is always a top priority.\"",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Vince Astly N. Cabungcag",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text("Web Developer", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DentalGoPrimary)
                    }
                }
            } else if (errorMsg.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = DentalGoError.copy(alpha = 0.08f)
                        )
                    ) {
                        Text(
                            text = errorMsg,
                            color = DentalGoError,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Services",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = DentalGoOnSurface,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    dentalServices.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { service ->
                                ServiceCard(
                                    service = service,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size < 2) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            item {
                Text(
                    text = "My Appointments",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = DentalGoOnSurface,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AppointmentsSummarySection(appointments)
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun AppointmentsSummarySection(appointments: List<AppointmentData>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppointmentColumn(
            title = "Upcoming",
            items = appointments.filter { it.status?.lowercase() in listOf("pending", "confirmed") }
        )
        AppointmentColumn(
            title = "History",
            items = appointments.filter { it.status?.lowercase() in listOf("completed", "cancelled") }
        )
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
private fun ServiceCard(service: DentalService, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DentalGoSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = service.name,
                tint = DentalGoPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = service.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DentalGoOnSurface,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun DashboardBottomBar(
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
            selected = true,
            onClick = onDashboard,
            icon = { Icon(Icons.Filled.Home, "Dashboard") },
            label = { Text("Dashboard", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor        = DentalGoPrimary,
                indicatorColor           = DentalGoPrimary.copy(alpha = 0.12f),
                unselectedIconColor      = DentalGoTextSecondary
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
