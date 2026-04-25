package com.dentalgo.app.core.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dentalgo.app.core.data.SessionManager
import com.dentalgo.app.core.network.RetrofitClient
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
import kotlinx.coroutines.CoroutineScope

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
    val coroutineScope = rememberCoroutineScope()

    val authRepo = remember { AuthRepository(RetrofitClient.createService(AuthApi::class.java)) }
    val dashboardRepo = remember { DashboardRepository(RetrofitClient.createService(DashboardApi::class.java)) }
    val profileRepo = remember { ProfileRepository(RetrofitClient.createService(ProfileApi::class.java)) }

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
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
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
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DASHBOARD) {
            val token    = sessionManager.getToken() ?: ""
            val userName = sessionManager.getUserName()
            DashboardScreen(
                presenterProvider      = { DashboardPresenter(dashboardRepo, coroutineScope) },
                token                  = token,
                initialUserName        = userName,
                onNavigateToProfile    = { navController.navigate(Routes.PROFILE) },
                onNavigateToAppointment = { /* TODO */ },
                onNavigateToHistory    = { /* TODO */ }
            )
        }

        composable(Routes.PROFILE) {
            val token = sessionManager.getToken() ?: ""
            ProfileScreen(
                presenterProvider          = { ProfilePresenter(profileRepo, coroutineScope) },
                token                      = token,
                onNavigateToChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                onNavigateToAppointment    = { /* TODO */ },
                onNavigateToDashboard      = { navController.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.DASHBOARD) { inclusive = true }
                }},
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
    }
}
