package com.dentalgo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dentalgo.app.ui.components.*
import com.dentalgo.app.ui.theme.*
import com.dentalgo.app.ui.viewmodel.AuthState
import com.dentalgo.app.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()
    val isLoading = loginState is AuthState.Loading

    // React to state changes
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is AuthState.Success -> {
                errorMsg = ""
                onLoginSuccess(state.token)
                viewModel.resetLogin()
            }
            is AuthState.Error   -> { errorMsg = state.message }
            is AuthState.NetworkError -> { errorMsg = "No internet connection. Please check your network." }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DentalGoBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // Logo
            DentalGoLogo(fontSize = 24)

            Spacer(Modifier.height(48.dp))

            // Header
            Text(
                text = "Welcome!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DentalGoOnSurface
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Sign in to access your DentalGo account.",
                fontSize = 14.sp,
                color = DentalGoTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // Error Banner
            if (errorMsg.isNotBlank()) {
                ErrorBanner(message = errorMsg)
                Spacer(Modifier.height(16.dp))
            }

            // Email
            DentalGoTextField(
                value = email,
                onValueChange = { email = it; errorMsg = "" },
                label = "Email",
                placeholder = "Enter your email",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )

            Spacer(Modifier.height(16.dp))

            // Password
            DentalGoPasswordField(
                value = password,
                onValueChange = { password = it; errorMsg = "" },
                label = "Password",
                placeholder = "Enter your password",
                imeAction = ImeAction.Done,
                onImeAction = { viewModel.login(email, password) },
                enabled = !isLoading
            )

            // Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot password?",
                    color = DentalGoPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { /* TODO: forgot password */ }
                )
            }

            Spacer(Modifier.height(28.dp))

            // Sign In Button
            DentalGoPrimaryButton(
                text = "Sign in",
                onClick = { viewModel.login(email, password) },
                isLoading = isLoading,
                enabled = !isLoading
            )

            Spacer(Modifier.height(24.dp))

            // Register Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = DentalGoTextSecondary
                )
                Text(
                    text = "Sign up",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DentalGoPrimary,
                    modifier = Modifier.clickable {
                        if (!isLoading) {
                            viewModel.resetLogin()
                            onNavigateToRegister()
                        }
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }

        // Full loading overlay
        if (isLoading) LoadingOverlay()
    }
}
