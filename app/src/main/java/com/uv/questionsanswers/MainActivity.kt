package com.uv.questionsanswers

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
import com.uv.questionsanswers.ui.theme.QuestionsAnswersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestionsAnswersTheme {
                var showSplash by remember { mutableStateOf(true) }
                var loggedInUser by remember { mutableStateOf<String?>(null) }
                var isAdmin by remember { mutableStateOf(false) }

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
                            })
                            isAdmin -> AdminPanel(onLogout = { loggedInUser = null })
                            else -> UserPanel(username = user, onLogout = { loggedInUser = null })
                        }
                    }
                }
            }
        }
    }
}
