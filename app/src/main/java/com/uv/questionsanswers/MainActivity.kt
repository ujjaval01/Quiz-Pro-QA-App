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

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PremiumScreen {
                        AnimatedContent(
                            targetState = showSplash to loggedInUser,
                            transitionSpec = {
                                (fadeIn(tween(500)) + slideInHorizontally(tween(500)) { it })
                                    .togetherWith(fadeOut(tween(500)) + slideOutHorizontally(tween(500)) { -it })
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
}
