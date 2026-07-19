package com.uv.questionsanswers

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit) {
    var isAdminMode by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Animated Logo/Icon
            StaggeredFadeIn(index = 0) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    ) {}
                    Surface(
                        modifier = Modifier.size(70.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 8.dp
                    ) {
                        Crossfade(targetState = isAdminMode, label = "iconAnim") { isAdmin ->
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = if (isAdmin) Icons.Default.Lock else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Text
            StaggeredFadeIn(index = 1) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isAdminMode) "Admin Access" else "Welcome Back",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isAdminMode) "Enter secure credentials" else "Ready to test your knowledge?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Main Login Card
            StaggeredFadeIn(index = 2) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    // Custom Mode Switcher
                    CustomModeSwitcher(
                        isAdminMode = isAdminMode,
                        onModeChange = { 
                            isAdminMode = it
                            errorMessage = null 
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    PremiumTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = "Enter Password",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 12.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    PremiumButton(
                        text = if (isAdminMode) "ADMIN LOGIN" else "CONTINUE AS USER",
                        onClick = {
                            val isValid = if (isAdminMode) {
                                password == "admin@123"
                            } else {
                                password == "vansi@123"
                            }

                            if (isValid) {
                                onLoginSuccess(if (isAdminMode) "Admin" else "User", if (isAdminMode) "Admin" else "Vansi")
                            } else {
                                errorMessage = "Invalid password. Access denied."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
}

@Composable
fun CustomModeSwitcher(isAdminMode: Boolean, onModeChange: (Boolean) -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp)
    ) {
        val width = maxWidth
        val transition = updateTransition(targetState = isAdminMode, label = "mode")
        val indicatorOffset by transition.animateDp(label = "offset") { if (it) width / 2 else 0.dp }

        // Animated Indicator
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
        )

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .clickable { onModeChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "User",
                    fontWeight = if (!isAdminMode) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (!isAdminMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .clickable { onModeChange(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Admin",
                    fontWeight = if (isAdminMode) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (isAdminMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
