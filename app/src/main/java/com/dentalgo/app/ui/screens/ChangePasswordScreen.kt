package com.dentalgo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dentalgo.app.ui.components.*
import com.dentalgo.app.ui.theme.*
import com.dentalgo.app.ui.viewmodel.PasswordState
import com.dentalgo.app.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    viewModel: ProfileViewModel,
    token: String,
    onNavigateBack: () -> Unit
) {
    var currentPassword  by remember { mutableStateOf("") }
    var newPassword      by remember { mutableStateOf("") }
    var confirmPassword  by remember { mutableStateOf("") }
    var errorMsg         by remember { mutableStateOf("") }
    var successMsg       by remember { mutableStateOf("") }

    val passwordState by viewModel.passwordState.collectAsState()
    val isLoading = passwordState is PasswordState.Loading

    LaunchedEffect(passwordState) {
        when (val s = passwordState) {
            is PasswordState.Success -> {
                successMsg = s.message.ifBlank { "Password changed successfully." }
                errorMsg = ""
                currentPassword = ""; newPassword = ""; confirmPassword = ""
                viewModel.resetPassword()
            }
            is PasswordState.Error       -> { errorMsg = s.message; successMsg = "" }
            is PasswordState.NetworkError -> { errorMsg = "No internet connection."; successMsg = "" }
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
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (!isLoading) onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DentalGoOnSurface
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Change Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DentalGoOnSurface
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(50))
                        .background(DentalGoPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = DentalGoPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Update your password",
                    fontSize = 16.sp,
                    color = DentalGoTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(28.dp))

                // Banners
                if (errorMsg.isNotBlank())   { ErrorBanner(errorMsg);   Spacer(Modifier.height(12.dp)) }
                if (successMsg.isNotBlank()) { SuccessBanner(successMsg); Spacer(Modifier.height(12.dp)) }

                // Current Password
                DentalGoPasswordField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it; errorMsg = ""; successMsg = "" },
                    label = "Current Password",
                    placeholder = "Enter your current password",
                    imeAction = ImeAction.Next,
                    enabled = !isLoading
                )
                Spacer(Modifier.height(16.dp))

                // New Password
                DentalGoPasswordField(
                    value = newPassword,
                    onValueChange = { newPassword = it; errorMsg = ""; successMsg = "" },
                    label = "New Password",
                    placeholder = "Enter your new password (min. 8 characters)",
                    imeAction = ImeAction.Next,
                    enabled = !isLoading
                )
                Spacer(Modifier.height(16.dp))

                // Confirm New Password
                DentalGoPasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMsg = "" },
                    label = "Confirm New Password",
                    placeholder = "Re-enter your new password",
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        viewModel.changePassword(token, currentPassword, newPassword, confirmPassword)
                    },
                    enabled = !isLoading
                )

                Spacer(Modifier.height(28.dp))

                DentalGoPrimaryButton(
                    text = "Change Password",
                    onClick = {
                        viewModel.changePassword(token, currentPassword, newPassword, confirmPassword)
                    },
                    isLoading = isLoading,
                    enabled   = !isLoading
                )

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = { if (!isLoading) onNavigateBack() }) {
                    Text("Cancel", color = DentalGoTextSecondary, fontSize = 14.sp)
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        if (isLoading) LoadingOverlay()
    }
}
