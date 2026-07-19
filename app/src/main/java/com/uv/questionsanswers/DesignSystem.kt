package com.uv.questionsanswers

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object PremiumTheme {
    val CornerLarge = 24.dp
    val CornerMedium = 16.dp
    val ElevationSoft = 4.dp
}

@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .scale(scale),
        shape = RoundedCornerShape(PremiumTheme.CornerMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f)
        ),
        enabled = enabled,
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }
    }
}

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "cardScale")

    Card(
        modifier = modifier
            .scale(scale)
            .let { if (onClick != null) it.clickable(interactionSource = interactionSource, indication = null, onClick = onClick) else it },
        shape = RoundedCornerShape(PremiumTheme.CornerLarge),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(PremiumTheme.CornerLarge))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon?.let { { Icon(it, null) } },
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(PremiumTheme.CornerMedium),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        singleLine = true
    )
}

@Composable
fun StaggeredFadeIn(
    index: Int,
    content: @Composable () -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 100L)
        visible.value = true
    }
    androidx.compose.animation.AnimatedVisibility(
        visible = visible.value,
        enter = androidx.compose.animation.fadeIn(tween(600)) + 
                androidx.compose.animation.slideInVertically(tween(600)) { it / 2 }
    ) {
        content()
    }
}

@Composable
fun BackgroundDecorations() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    
    // Animation for glow circles
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Animation for stickers
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Soft Glow Circles
        Canvas(modifier = Modifier.fillMaxSize().alpha(alpha).blur(80.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF6366F1), Color.Transparent),
                ),
                radius = 400f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.1f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF10B981).copy(alpha = 0.6f), Color.Transparent),
                ),
                radius = 500f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.9f)
            )
        }

        // Floating Stickers
        Box(modifier = Modifier.fillMaxSize()) {
            StickerIcon(
                icon = Icons.Outlined.Quiz,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 40.dp)
                    .offset(y = floatingOffset.dp)
                    .rotate(15f),
                tint = Color(0xFF6366F1).copy(alpha = 0.15f)
            )
            StickerIcon(
                icon = Icons.Outlined.School,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp, bottom = 150.dp)
                    .offset(y = (-floatingOffset).dp)
                    .rotate(-20f),
                tint = Color(0xFF10B981).copy(alpha = 0.15f)
            )
            StickerIcon(
                icon = Icons.Outlined.Lightbulb,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp, end = 60.dp)
                    .offset(y = (floatingOffset * 0.7f).dp)
                    .rotate(10f),
                tint = Color(0xFFF59E0B).copy(alpha = 0.15f)
            )
            StickerIcon(
                icon = Icons.Outlined.Star,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 150.dp, start = 50.dp)
                    .offset(y = (floatingOffset * 1.2f).dp)
                    .rotate(-10f),
                tint = Color(0xFFEC4899).copy(alpha = 0.12f)
            )
        }
    }
}

@Composable
fun StickerIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    tint: Color
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier.size(80.dp),
        tint = tint
    )
}

@Composable
fun PremiumScreen(
    showBackground: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (showBackground) {
            BackgroundDecorations()
        }
        content()
    }
}
