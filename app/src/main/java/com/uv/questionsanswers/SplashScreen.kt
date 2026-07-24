package com.uv.questionsanswers

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uv.questionsanswers.ui.theme.NeuBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000L)
        onFinished()
    }

    Box(modifier = Modifier.fillMaxSize().background(NeuBackground), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .neumorphic(elevation = 8.dp, shape = RoundedCornerShape(40.dp))
                    .clip(RoundedCornerShape(40.dp))
                    .background(NeuBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Quiz, 
                    null, 
                    modifier = Modifier.size(72.dp), 
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(tween(1000, 500)) + expandVertically(tween(1000, 500))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "QUIZ PRO",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 6.sp
                    )
                    Text(
                        "ELEVATE YOUR KNOWLEDGE",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
