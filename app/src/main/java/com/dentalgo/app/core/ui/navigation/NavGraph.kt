package com.dentalgo.app.core.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dentalgo.app.core.data.SessionManager
import com.dentalgo.app.core.network.RetrofitClient
import com.dentalgo.app.features.appointment.data.AppointmentApi
import com.dentalgo.app.features.appointment.data.AppointmentRepository
import com.dentalgo.app.features.appointment.presenter.AppointmentPresenter
import com.dentalgo.app.features.appointment.ui.AppointmentScreen
import com.dentalgo.app.features.appointment.ui.AppointmentSummaryScreen
import com.dentalgo.app.features.auth.data.AuthApi
import com.dentalgo.app.features.auth.data.AuthRepository
import com.dentalgo.app.features.auth.presenter.LoginPresenter
import com.dentalgo.app.features.auth.presenter.RegisterPresenter
import com.dentalgo.app.features.auth.ui.LoginScreen
import com.dentalgo.app.features.auth.ui.RegisterScreen
import com.dentalgo.app.features.dashboard.data.DashboardApi
import com.dentalgo.app.features.dashboard.data.DashboardRepository
import com.dentalgo.app.features.dashboard.presenter.DashboardPresenter
import com.dentalgo.app.features.dashboard.ui.DashboardScreen
import com.dentalgo.app.features.profile.data.ProfileApi
import com.dentalgo.app.features.profile.data.ProfileRepository
import com.dentalgo.app.features.profile.presenter.ChangePasswordPresenter
import com.dentalgo.app.features.profile.presenter.ProfilePresenter
import com.dentalgo.app.features.profile.ui.ChangePasswordScreen
import com.dentalgo.app.features.profile.ui.ProfileScreen
import java.net.URLDecoder
import java.net.URLEncoder

object Routes {
    const val LOGIN              = "login"
    const val REGISTER           = "register"
    const val DASHBOARD          = "dashboard"
    const val PROFILE            = "profile"
    const val CHANGE_PASSWORD    = "change_password"
    const val APPOINTMENT        = "appointment"
    const val APPOINTMENT_SUMMARY = "appointment_summary"
}

@Composable
fun DentalGoNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    val coroutineScope = rememberCoroutineScope()

    val authRepo        = remember { AuthRepository(RetrofitClient.createService(AuthApi::class.java)) }
    val dashboardRepo   = remember { DashboardRepository(RetrofitClient.createService(DashboardApi::class.java)) }
    val profileRepo     = remember { ProfileRepository(RetrofitClient.createService(ProfileApi::class.java)) }
    val appointmentRepo = remember { AppointmentRepository(RetrofitClient.createService(AppointmentApi::class.java)) }

    val startDest = if (sessionManager.isLoggedIn()) Routes.DASHBOARD else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDest) {

        /* ── Login ── */
        composable(Routes.LOGIN) {
            LoginScreen(
                presenterProvider = { LoginPresenter(authRepo, coroutineScope) },
                onLoginSuccess = { email ->
                    sessionManager.saveToken(email)
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                presenterProvider = { RegisterPresenter(authRepo, coroutineScope) },
                onRegisterSuccess = { email ->
                    sessionManager.saveToken(email)
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.DASHBOARD) {
            val token    = sessionManager.getToken() ?: ""
            val userName = sessionManager.getUserName()
            DashboardScreen(
                presenterProvider       = { DashboardPresenter(dashboardRepo, coroutineScope) },
                token                   = token,
                initialUserName         = userName,
                onNavigateToProfile     = { navController.navigate(Routes.PROFILE) },
                onNavigateToAppointment = { navController.navigate(Routes.APPOINTMENT) },
                onNavigateToHistory     = { /* TODO */ }
            )
        }

        composable(Routes.PROFILE) {
            val token = sessionManager.getToken() ?: ""
            ProfileScreen(
                presenterProvider          = { ProfilePresenter(profileRepo, coroutineScope) },
                token                      = token,
                onNavigateToChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                onNavigateToAppointment    = { navController.navigate(Routes.APPOINTMENT) },
                onNavigateToDashboard      = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigateToHistory        = { /* TODO */ },
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CHANGE_PASSWORD) {
            val token = sessionManager.getToken() ?: ""
            ChangePasswordScreen(
                presenterProvider = { ChangePasswordPresenter(profileRepo, coroutineScope) },
                token             = token,
                onNavigateBack    = { navController.popBackStack() }
            )
        }

        composable(Routes.APPOINTMENT) {
            val token = sessionManager.getToken() ?: ""
            AppointmentScreen(
                presenterProvider   = { AppointmentPresenter(appointmentRepo, coroutineScope) },
                token               = token,
                onNavigateToSummary = { service, date, notes ->
                    val encodedService = URLEncoder.encode(service, "UTF-8")
                    val encodedDate    = URLEncoder.encode(date,    "UTF-8")
                    val encodedNotes   = URLEncoder.encode(notes.ifBlank { " " }, "UTF-8")
                    navController.navigate(
                        "${Routes.APPOINTMENT_SUMMARY}/$encodedService/$encodedDate/$encodedNotes"
                    )
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigateToHistory = { /* TODO */ }
            )
        }

        /* ── Appointment Summary ── */
        composable(
            route = "${Routes.APPOINTMENT_SUMMARY}/{service}/{date}/{notes}",
            arguments = listOf(
                navArgument("service") { type = NavType.StringType; defaultValue = "" },
                navArgument("date")    { type = NavType.StringType; defaultValue = "" },
                navArgument("notes")   { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val token   = sessionManager.getToken() ?: ""
            val service = URLDecoder.decode(backStackEntry.arguments?.getString("service") ?: "", "UTF-8")
            val date    = URLDecoder.decode(backStackEntry.arguments?.getString("date")    ?: "", "UTF-8")
            val notes   = URLDecoder.decode(backStackEntry.arguments?.getString("notes")   ?: "", "UTF-8").trim()
            AppointmentSummaryScreen(
                presenterProvider     = { AppointmentPresenter(appointmentRepo, coroutineScope) },
                token                 = token,
                service               = service,
                date                  = date,
                notes                 = notes,
                onBookingSuccess      = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.APPOINTMENT) { inclusive = true }
                    }
                },
                onNavigateBack        = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigateToHistory   = { /* TODO */ }
            )
        }
    }
}
