package com.uv.questionsanswers

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uv.questionsanswers.ui.theme.NeuBackground

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

        // Neumorphic Logo
        StaggeredFadeIn(index = 0) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .neumorphic(shape = RoundedCornerShape(36.dp))
                    .clip(RoundedCornerShape(36.dp))
                    .background(NeuBackground),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primary,
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

        Spacer(modifier = Modifier.height(40.dp))

        // Welcome Text
        StaggeredFadeIn(index = 1) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "QUIZ PRO",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isAdminMode) "Admin Access" else "User Login",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Main Login Card
        StaggeredFadeIn(index = 2) {
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                // Custom Neumorphic Mode Switcher
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
                    label = "Password",
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
                    text = if (isAdminMode) "LOGIN AS ADMIN" else "START AS USER",
                    onClick = {
                        val isValid = if (isAdminMode) {
                            password == "admin@123"
                        } else {
                            password == "vansi@123"
                        }

                        if (isValid) {
                            onLoginSuccess(if (isAdminMode) "Admin" else "User", if (isAdminMode) "Admin" else "Vansi")
                        } else {
                            errorMessage = "Access denied. Check credentials."
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
            .height(64.dp)
            .neumorphic(elevation = 3.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(NeuBackground)
            .padding(6.dp)
    ) {
        val width = maxWidth
        val transition = updateTransition(targetState = isAdminMode, label = "mode")
        val indicatorOffset by transition.animateDp(label = "offset") { if (it) width / 2 else 0.dp }

        // Neumorphic Indicator
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .neumorphic(elevation = 2.dp, shape = RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(NeuBackground),
            contentAlignment = Alignment.Center
        ) {
            // Inner shadow or highlight could go here
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
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
