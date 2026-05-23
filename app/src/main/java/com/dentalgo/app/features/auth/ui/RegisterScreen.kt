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
import com.dentalgo.app.features.auth.contract.RegisterContract

@Composable
fun RegisterScreen(
    presenterProvider: () -> RegisterContract.Presenter,
    onRegisterSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val presenter = remember { presenterProvider() }

    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    var errorMsg        by remember { mutableStateOf("") }
    var isLoading       by remember { mutableStateOf(false) }

    val view = remember {
        object : RegisterContract.View {
            override fun showLoading() { isLoading = true }
            override fun hideLoading() { isLoading = false }
            override fun showError(message: String) { errorMsg = message }
            override fun onNetworkError() { errorMsg = "No internet connection. Please check your network." }
            override fun onRegisterSuccess(token: String) {
                errorMsg = ""
                onRegisterSuccess(token)
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
                .padding(horizontal = 28.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            DentalGoLogo(modifier = Modifier.align(Alignment.Start), fontSize = 22)

            Spacer(Modifier.height(32.dp))

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

            if (errorMsg.isNotBlank()) {
                ErrorBanner(message = errorMsg)
                Spacer(Modifier.height(16.dp))
            }

            DentalGoTextField(
                value = fullName,
                onValueChange = { fullName = it; errorMsg = "" },
                label = "Full Name",
                placeholder = "Enter Full Name",
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )
            Spacer(Modifier.height(14.dp))

            DentalGoPasswordField(
                value = password,
                onValueChange = { password = it; errorMsg = "" },
                label = "Password",
                placeholder = "Enter Password",
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )
            Spacer(Modifier.height(14.dp))

            DentalGoPasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; errorMsg = "" },
                label = "Confirm Password",
                placeholder = "Enter Confirm Password",
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )
            Spacer(Modifier.height(14.dp))

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

            DentalGoTextField(
                value = phone,
                onValueChange = { phone = it; errorMsg = "" },
                label = "Phone Number",
                placeholder = "Enter Phone Number",
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
                onImeAction = {
                    presenter.register(fullName, email, password, confirmPassword, phone)
                },
                enabled = !isLoading
            )

            Spacer(Modifier.height(28.dp))

            DentalGoPrimaryButton(
                text = "Sign up",
                onClick = { presenter.register(fullName, email, password, confirmPassword, phone) },
                isLoading = isLoading,
                enabled = !isLoading
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Back",
                fontSize = 14.sp,
                color = DentalGoPrimary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    if (!isLoading) {
                        onNavigateBack()
                    }
                }
            )

        }

        if (isLoading) LoadingOverlay()
    }
}
