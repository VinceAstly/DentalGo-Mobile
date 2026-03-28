package com.dentalgo.app.ui.screens

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
import com.dentalgo.app.ui.viewmodel.AuthState
import com.dentalgo.app.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    var errorMsg        by remember { mutableStateOf("") }

    val registerState by viewModel.registerState.collectAsState()
    val isLoading = registerState is AuthState.Loading

    // React to state changes
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is AuthState.Success -> {
                errorMsg = ""
                onRegisterSuccess(state.token)
                viewModel.resetRegister()
            }
            is AuthState.Error       -> errorMsg = state.message
            is AuthState.NetworkError -> errorMsg = "No internet connection. Please check your network."
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
            Spacer(Modifier.height(36.dp))

            // Logo
            DentalGoLogo(modifier = Modifier.align(Alignment.Start), fontSize = 22)

            Spacer(Modifier.height(32.dp))

            // Title
            Text(
                text = "Register",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DentalGoOnSurface
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Create your DentalGo account here.",
                fontSize = 14.sp,
                color = DentalGoTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            // Error Banner
            if (errorMsg.isNotBlank()) {
                ErrorBanner(message = errorMsg)
                Spacer(Modifier.height(16.dp))
            }

            // Full Name
            DentalGoTextField(
                value = fullName,
                onValueChange = { fullName = it; errorMsg = "" },
                label = "Full Name",
                placeholder = "Enter Full Name",
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )
            Spacer(Modifier.height(14.dp))

            // Password
            DentalGoPasswordField(
                value = password,
                onValueChange = { password = it; errorMsg = "" },
                label = "Password",
                placeholder = "Enter Password",
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )
            Spacer(Modifier.height(14.dp))

            // Confirm Password
            DentalGoPasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; errorMsg = "" },
                label = "Confirm Password",
                placeholder = "Enter Confirm Password",
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )
            Spacer(Modifier.height(14.dp))

            // Email
            DentalGoTextField(
                value = email,
                onValueChange = { email = it; errorMsg = "" },
                label = "Email",
                placeholder = "Enter Email",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )
            Spacer(Modifier.height(14.dp))

            // Phone Number
            DentalGoTextField(
                value = phone,
                onValueChange = { phone = it; errorMsg = "" },
                label = "Phone Number",
                placeholder = "Enter Phone Number",
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
                onImeAction = {
                    viewModel.register(fullName, email, password, confirmPassword, phone)
                },
                enabled = !isLoading
            )

            Spacer(Modifier.height(28.dp))

            // Sign Up Button
            DentalGoPrimaryButton(
                text = "Sign up",
                onClick = { viewModel.register(fullName, email, password, confirmPassword, phone) },
                isLoading = isLoading,
                enabled = !isLoading
            )

            Spacer(Modifier.height(16.dp))

            // Back
            Text(
                text = "Back",
                fontSize = 14.sp,
                color = DentalGoPrimary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    if (!isLoading) {
                        viewModel.resetRegister()
                        onNavigateBack()
                    }
                }
            )

            Spacer(Modifier.height(32.dp))
        }

        if (isLoading) LoadingOverlay()
    }
}
