package com.liquidglass.vncalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LiquidAmbientBackground(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val baseGradient = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF060B1A),
                Color(0xFF0D1B3E),
                Color(0xFF0B132B)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFDCEBFF),
                Color(0xFFEAE6FF),
                Color(0xFFE0F7FA)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseGradient)
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-70).dp, y = (-40).dp)
                .blur(95.dp)
                .background(
                    color = if (isDark) Color(0xFF2563EB).copy(alpha = 0.55f) else Color(0xFF38BDF8).copy(alpha = 0.50f),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 90.dp, y = (-50).dp)
                .blur(100.dp)
                .background(
                    color = if (isDark) Color(0xFF7C3AED).copy(alpha = 0.45f) else Color(0xFFA78BFA).copy(alpha = 0.42f),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 90.dp)
                .blur(110.dp)
                .background(
                    color = if (isDark) Color(0xFF06B6D4).copy(alpha = 0.40f) else Color(0xFF2DD4BF).copy(alpha = 0.40f),
                    shape = CircleShape
                )
        )

        content()
    }
}

@Composable
fun LiquidGlassCard(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    contentPadding: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val surfaceBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.17f),
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.05f)
            ),
            start = Offset.Zero,
            end = Offset(900f, 900f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.72f),
                Color.White.copy(alpha = 0.46f),
                Color.White.copy(alpha = 0.36f)
            ),
            start = Offset.Zero,
            end = Offset(900f, 900f)
        )
    }

    val specularBorder = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.55f),
                Color.White.copy(alpha = 0.14f),
                Color.White.copy(alpha = 0.06f),
                Color.White.copy(alpha = 0.28f)
            ),
            start = Offset.Zero,
            end = Offset(800f, 800f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.80f)
            ),
            start = Offset.Zero,
            end = Offset(800f, 800f)
        )
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isDark) 18.dp else 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.14f),
                spotColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.14f)
            )
            .clip(shape)
            .background(surfaceBrush)
            .border(width = 1.dp, brush = specularBorder, shape = shape)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun LiquidGlassPill(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(50)
    val bg = when {
        accentColor != null -> accentColor.copy(alpha = if (isDark) 0.28f else 0.18f)
        isDark -> Color.White.copy(alpha = 0.14f)
        else -> Color.White.copy(alpha = 0.65f)
    }
    val borderColor = when {
        accentColor != null -> accentColor.copy(alpha = 0.55f)
        isDark -> Color.White.copy(alpha = 0.30f)
        else -> Color.White.copy(alpha = 0.85f)
    }

    val clickableModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .clickable(onClick = onClick)
    } else {
        modifier.clip(shape)
    }

    Box(
        modifier = clickableModifier
            .background(bg)
            .border(width = 0.8.dp, color = borderColor, shape = shape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}
