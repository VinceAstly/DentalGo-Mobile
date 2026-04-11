package com.dentalgo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dentalgo.app.ui.theme.*

/* ──────────────────────────────────────────────
   DentalGo Branded Text Field
   ────────────────────────────────────────────── */
@Composable
fun DentalGoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    singleLine: Boolean = true,
    maxLines: Int = 1,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = DentalGoLabel,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, color = DentalGoTextMuted, fontSize = 14.sp)
            },
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = enabled,
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction() },
                onNext = { onImeAction() }
            ),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = DentalGoPrimary,
                unfocusedBorderColor = DentalGoBorder,
                errorBorderColor     = DentalGoError,
                focusedLabelColor    = DentalGoPrimary,
                cursorColor          = DentalGoPrimary,
                focusedContainerColor   = DentalGoSurface,
                unfocusedContainerColor = DentalGoSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/* ──────────────────────────────────────────────
   DentalGo Password Field
   ────────────────────────────────────────────── */
@Composable
fun DentalGoPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter password",
    isError: Boolean = false,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = DentalGoLabel,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = DentalGoTextMuted, fontSize = 14.sp) },
            singleLine = true,
            enabled = enabled,
            isError = isError,
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(onDone = { onImeAction() }),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility
                                      else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = DentalGoTextSecondary
                    )
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = DentalGoPrimary,
                unfocusedBorderColor = DentalGoBorder,
                errorBorderColor     = DentalGoError,
                cursorColor          = DentalGoPrimary,
                focusedContainerColor   = DentalGoSurface,
                unfocusedContainerColor = DentalGoSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/* ──────────────────────────────────────────────
   DentalGo Primary Button
   ────────────────────────────────────────────── */
@Composable
fun DentalGoPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DentalGoPrimary,
            disabledContainerColor = DentalGoPrimary.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

/* ──────────────────────────────────────────────
   Error / Info Banner
   ────────────────────────────────────────────── */
@Composable
fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    if (message.isNotBlank()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DentalGoError.copy(alpha = 0.10f))
                .border(1.dp, DentalGoError.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = DentalGoError,
                fontSize = 13.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SuccessBanner(message: String, modifier: Modifier = Modifier) {
    if (message.isNotBlank()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DentalGoSuccess.copy(alpha = 0.10f))
                .border(1.dp, DentalGoSuccess.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = DentalGoSuccess,
                fontSize = 13.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/* ──────────────────────────────────────────────
   DentalGo Logo Text
   ────────────────────────────────────────────── */
@Composable
fun DentalGoLogo(modifier: Modifier = Modifier, fontSize: Int = 22) {
    Text(
        text = "DentalGo",
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Bold,
        color = DentalGoPrimary,
        modifier = modifier
    )
}

/* ──────────────────────────────────────────────
   Full-screen loading overlay
   ────────────────────────────────────────────── */
@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DentalGoSurface),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = DentalGoPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Please wait…", color = DentalGoTextSecondary, fontSize = 14.sp)
            }
        }
    }
}
