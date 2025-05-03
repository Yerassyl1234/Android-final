package com.example.orderlist.ui.theme // Убедитесь, что пакет правильный

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- ДОБАВЬТЕ ИМПОРТЫ ЦВЕТОВ И ТИПОГРАФИКИ ---
import com.example.ui.theme.Purple80 // Замените на ваши реальные пути/имена
import com.example.ui.theme.PurpleGrey80
import com.example.ui.theme.Pink80
import com.example.ui.theme.Purple40
import com.example.ui.theme.PurpleGrey40
import com.example.ui.theme.Pink40
import com.example.ui.theme.Typography


// ---------------------------------------------

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    /* Other default colors to override... */
)

@Composable
fun OrderListTheme( // <-- Убедитесь, что это имя используется в MainActivity
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Поменяйте на false, если не нужны динамические цвета
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // --- Добавлена настройка системных панелей ---
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window // Безопасное приведение типа
            if (window != null) { // Проверка на null
                window.statusBarColor = colorScheme.primary.toArgb() // Пример
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }
    // -----------------------------------------

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Теперь должно работать
        content = content
    )
}