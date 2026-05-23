package com.dentalgo.app.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dentalgo.app.ui.components.*
import com.dentalgo.app.ui.theme.*
import com.dentalgo.app.features.auth.contract.LoginContract

@Composable
fun LoginScreen(
    presenterProvider: () -> LoginContract.Presenter,
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val presenter = remember { presenterProvider() }

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val view = remember {
        object : LoginContract.View {
            override fun showLoading() { isLoading = true }
            override fun hideLoading() { isLoading = false }
            override fun showError(message: String) { errorMsg = message }
            override fun onNetworkError() { errorMsg = "No internet connection. Please check your network." }
            override fun onLoginSuccess(token: String) {
                errorMsg = ""
                onLoginSuccess(token)
            }
        }
    }

    DisposableEffect(presenter) {
        presenter.attachView(view)
        onDispose { presenter.detachView() }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DentalGoLogo(fontSize = 24)

            Spacer(Modifier.height(48.dp))

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

            if (errorMsg.isNotBlank()) {
                ErrorBanner(message = errorMsg)
                Spacer(Modifier.height(16.dp))
            }

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

            DentalGoPasswordField(
                value = password,
                onValueChange = { password = it; errorMsg = "" },
                label = "Password",
                placeholder = "Enter your password",
                imeAction = ImeAction.Done,
                onImeAction = { presenter.login(email, password) },
                enabled = !isLoading
            )

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

            DentalGoPrimaryButton(
                text = "Sign in",
                onClick = { presenter.login(email, password) },
                isLoading = isLoading,
                enabled = !isLoading
            )

            Spacer(Modifier.height(24.dp))

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
                            onNavigateToRegister()
                        }
                    }
                )
            }
        }

        if (isLoading) LoadingOverlay()
    }
}
