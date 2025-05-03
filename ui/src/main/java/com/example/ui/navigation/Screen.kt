package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String? = null, val icon: ImageVector? = null) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Booking : Screen("booking", "Бронь", Icons.Filled.DateRange)
    object Profile : Screen("profile", "Профиль", Icons.Filled.AccountCircle)
}