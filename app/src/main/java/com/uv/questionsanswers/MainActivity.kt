package com.uv.questionsanswers

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.uv.questionsanswers.ui.theme.QuestionsAnswersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestionsAnswersTheme {
                val context = LocalContext.current
                val prefs = remember { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE) }
                
                var showSplash by remember { mutableStateOf(true) }
                var loggedInUser by remember { 
                    mutableStateOf(prefs.getString("user_name", null)) 
                }
                var isAdmin by remember { 
                    mutableStateOf(prefs.getBoolean("is_admin", false)) 
                }

                PremiumScreen {
                    AnimatedContent(
                        targetState = showSplash to loggedInUser,
                        transitionSpec = {
                            (fadeIn(tween(600)) + scaleIn(initialScale = 0.95f))
                                .togetherWith(fadeOut(tween(400)) + scaleOut(targetScale = 1.05f))
                        },
                        label = "screenTransition"
                    ) { (splash, user) ->
                        when {
                            splash -> SplashScreen { showSplash = false }
                            user == null -> LoginScreen(onLoginSuccess = { role, name ->
                                if (role == "Admin") {
                                    isAdmin = true
                                    loggedInUser = "Admin"
                                } else {
                                    isAdmin = false
                                    loggedInUser = name
                                }
                                // Save session
                                prefs.edit()
                                    .putString("user_name", loggedInUser)
                                    .putBoolean("is_admin", isAdmin)
                                    .apply()
                            })
                            isAdmin -> AdminPanel(onLogout = { 
                                loggedInUser = null
                                isAdmin = false
                                prefs.edit().clear().apply()
                            })
                            else -> UserPanel(username = user, onLogout = { 
                                loggedInUser = null
                                isAdmin = false
                                prefs.edit().clear().apply()
                            })
                        }
                    }
                }
            }
        }
    }
}
