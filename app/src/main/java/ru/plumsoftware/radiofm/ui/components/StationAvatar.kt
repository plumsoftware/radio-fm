package ru.plumsoftware.radiofm.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * "Обложка" станции.
 *
 * Если задан [iconRes] (своя иконка/логотип станции — см. [RadioStation.iconRes]),
 * отображается она. Пока [iconRes] равен `null` (по умолчанию для всех станций, т.к. своих
 * логотипов в проекте нет), рисуется генерируемый цветной градиентный фон с первой буквой
 * названия — это и есть запасной вариант на все случаи.
 */
@Composable
fun StationAvatar(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    @DrawableRes iconRes: Int? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(color, color.copy(alpha = 0.65f)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (iconRes != null) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size),
            )
        } else {
            Text(
                text = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}
