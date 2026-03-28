package com.dentalgo.app.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dentalgo.app.data.SessionManager
import com.dentalgo.app.data.api.RetrofitClient
import com.dentalgo.app.data.repository.ApiRepository
import com.dentalgo.app.ui.screens.*
import com.dentalgo.app.ui.viewmodel.*

/* ── Route constants ── */
object Routes {
    const val LOGIN           = "login"
    const val REGISTER        = "register"
    const val DASHBOARD       = "dashboard"
    const val PROFILE         = "profile"
    const val CHANGE_PASSWORD = "change_password"
}

@Composable
fun DentalGoNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    /* Shared repo + factory */
    val repository = remember { ApiRepository(RetrofitClient.instance) }
    val factory    = remember { ViewModelFactory(repository) }

    /* Shared ViewModels scoped to NavGraph */
    val authViewModel: AuthViewModel      = viewModel(factory = factory)
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
    val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)

    /* Start on login unless already logged-in */
    val startDest = if (sessionManager.isLoggedIn()) Routes.DASHBOARD else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDest) {

        /* ── Login ── */
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { token ->
                    sessionManager.saveToken(token)
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        /* ── Register ── */
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { token ->
                    sessionManager.saveToken(token)
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        /* ── Dashboard ── */
        composable(Routes.DASHBOARD) {
            val token    = sessionManager.getToken() ?: ""
            val userName = sessionManager.getUserName()
            DashboardScreen(
                viewModel              = dashboardViewModel,
                token                  = token,
                userName               = userName,
                onNavigateToProfile    = { navController.navigate(Routes.PROFILE) },
                onNavigateToAppointment = { /* TODO: Appointment screen */ },
                onNavigateToHistory    = { /* TODO: History screen */ }
            )
        }

        /* ── Profile ── */
        composable(Routes.PROFILE) {
            val token = sessionManager.getToken() ?: ""
            ProfileScreen(
                viewModel                 = profileViewModel,
                token                     = token,
                onNavigateToChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                onNavigateToAppointment   = { /* TODO */ },
                onNavigateToDashboard     = { navController.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.DASHBOARD) { inclusive = true }
                }},
                onNavigateToHistory       = { /* TODO */ },
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        /* ── Change Password ── */
        composable(Routes.CHANGE_PASSWORD) {
            val token = sessionManager.getToken() ?: ""
            ChangePasswordScreen(
                viewModel      = profileViewModel,
                token          = token,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
