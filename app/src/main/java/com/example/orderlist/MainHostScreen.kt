package com.example.orderlist

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.navDeepLink

import com.example.ui.authorization.LoginPage
import com.example.ui.authorization.SignupPage
import com.example.ui.booking.BookingScreen
import com.example.ui.profile.ProfileScreen
import org.koin.androidx.compose.koinViewModel

sealed class Screen(val route: String, val label: String? = null, val icon: ImageVector? = null) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Booking : Screen("booking", "Бронь", Icons.Filled.DateRange)
    object Profile : Screen("profile", "Профиль", Icons.Filled.AccountCircle)
}

val bottomNavItems = listOf(
    Screen.Booking,
    Screen.Profile,
)

private const val MY_APP_URI_SCHEME = "myapp"
private const val MY_APP_URI_HOST = "orderlist.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHostScreen() {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = remember(currentRoute) {
        bottomNavItems.any { it.route == currentRoute }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Booking.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginPage(navController = navController)
            }
            composable(Screen.Signup.route) {
                SignupPage(navController = navController)
            }
            composable(
                route = Screen.Booking.route,
                deepLinks = listOf(navDeepLink { uriPattern = "$MY_APP_URI_SCHEME://$MY_APP_URI_HOST/booking" })
            ) {
                BookingScreen(navController = navController)
            }
            composable(
                route = Screen.Profile.route,
                deepLinks = listOf(
                    navDeepLink { uriPattern = "$MY_APP_URI_SCHEME://$MY_APP_URI_HOST/profile" },
                    navDeepLink { uriPattern = "$MY_APP_URI_SCHEME://$MY_APP_URI_HOST/booking/history" }
                )
            ) {
                ProfileScreen(navController = navController)
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { screen ->
            val label = screen.label
            val icon = screen.icon
            if (label != null && icon != null) {
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label) },
                    alwaysShowLabel = true
                )
            }
        }
    }
}
