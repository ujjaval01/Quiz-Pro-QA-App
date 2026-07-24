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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uv.questionsanswers.ui.theme.NeuBackground
import com.uv.questionsanswers.ui.theme.NeuDarkShadow
import com.uv.questionsanswers.ui.theme.NeuLightShadow

object PremiumTheme {
    val CornerLarge = 32.dp
    val CornerMedium = 20.dp
    val ElevationSoft = 6.dp
}

fun Modifier.neumorphic(
    elevation: Dp = 6.dp,
    shape: RoundedCornerShape = RoundedCornerShape(PremiumTheme.CornerMedium),
    isPressed: Boolean = false
): Modifier = this.drawBehind {
    val shadowColor = NeuDarkShadow
    val lightColor = NeuLightShadow
    
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        
        if (isPressed) {
            // Inner shadows for pressed state
            frameworkPaint.color = shadowColor.toArgb()
            frameworkPaint.maskFilter = android.graphics.BlurMaskFilter(elevation.toPx(), android.graphics.BlurMaskFilter.Blur.NORMAL)
            
            // This is a simplification; true inner shadows are harder in Compose without native support
            // For now, we'll just darken the background slightly
        } else {
            // Outer shadows
            // Dark Shadow (Bottom Right)
            frameworkPaint.color = shadowColor.toArgb()
            frameworkPaint.maskFilter = android.graphics.BlurMaskFilter(elevation.toPx(), android.graphics.BlurMaskFilter.Blur.NORMAL)
            canvas.drawRoundRect(
                left = elevation.toPx(),
                top = elevation.toPx(),
                right = size.width + elevation.toPx(),
                bottom = size.height + elevation.toPx(),
                radiusX = shape.topStart.toPx(size, this),
                radiusY = shape.topStart.toPx(size, this),
                paint = paint
            )
            
            // Light Shadow (Top Left)
            frameworkPaint.color = lightColor.toArgb()
            canvas.drawRoundRect(
                left = -elevation.toPx(),
                top = -elevation.toPx(),
                right = size.width - elevation.toPx(),
                bottom = size.height - elevation.toPx(),
                radiusX = shape.topStart.toPx(size, this),
                radiusY = shape.topStart.toPx(size, this),
                paint = paint
            )
        }
    }
}

@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = NeuBackground,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "scale")

    Box(
        modifier = modifier
            .scale(scale)
            .neumorphic(isPressed = isPressed)
            .clip(RoundedCornerShape(PremiumTheme.CornerMedium))
            .background(containerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = contentColor)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = if (enabled) contentColor else contentColor.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = NeuBackground,
    shape: RoundedCornerShape = RoundedCornerShape(PremiumTheme.CornerLarge),
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    Box(
        modifier = modifier
            .neumorphic(shape = shape, isPressed = isPressed && onClick != null)
            .clip(shape)
            .background(containerColor)
            .then(if (onClick != null) Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick) else Modifier)
            .padding(24.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    PremiumCard(modifier = modifier, content = content)
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
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neumorphic(elevation = 3.dp)
                .clip(RoundedCornerShape(PremiumTheme.CornerMedium))
                .background(NeuBackground)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                leadingIcon = leadingIcon?.let { { Icon(it, null, tint = MaterialTheme.colorScheme.primary) } },
                trailingIcon = trailingIcon,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(PremiumTheme.CornerMedium),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true
            )
        }
    }
}

@Composable
fun StaggeredFadeIn(
    index: Int,
    content: @Composable () -> Unit
) {
    content()
}

@Composable
fun BackgroundDecorations() {
    Box(modifier = Modifier.fillMaxSize().background(NeuBackground)) {
        val infiniteTransition = rememberInfiniteTransition(label = "stickers")
        val floatingOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "floating"
        )

        // Floating Stickers with Neumorphic touch (low alpha shadows)
        Box(modifier = Modifier.fillMaxSize()) {
            StickerIcon(
                icon = Icons.Outlined.Quiz,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 40.dp)
                    .offset(y = floatingOffset.dp)
                    .rotate(15f),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            StickerIcon(
                icon = Icons.Outlined.School,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp, bottom = 150.dp)
                    .offset(y = (-floatingOffset).dp)
                    .rotate(-20f),
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun StickerIcon(
    icon: ImageVector,
    modifier: Modifier,
    tint: Color
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier.size(100.dp),
        tint = tint
    )
}

@Composable
fun PremiumScreen(
    showBackground: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(NeuBackground)) {
        if (showBackground) {
            BackgroundDecorations()
        }
        content()
    }
}
