package com.example.ui.booking

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.ui.authorization.AuthState
import com.example.ui.authorization.AuthViewModel
import com.example.ui.composable.WashingMachineCard
import com.example.ui.navigation.Screen

@Composable
fun BookingScreen(
    modifier: Modifier = Modifier,
    navController: NavController,

) {
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.authState.collectAsState()
    val bookingViewModel: BookingViewModel = koinViewModel()

    LaunchedEffect(authState) {
        if (authState is AuthState.UnAuthenticated) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    when (authState) {
        is AuthState.Authenticated -> {
            Log.d("BookingScreen", "State: Authenticated")
            val washingMachines by bookingViewModel.washingMachines.collectAsStateWithLifecycle()
            val isLoading by bookingViewModel.isLoading.collectAsStateWithLifecycle()
            val error by bookingViewModel.error.collectAsStateWithLifecycle()

            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        Log.d("BookingScreen", "UI: Showing Loading Indicator")
                        CircularProgressIndicator()
                    }
                    error != null -> {
                        Log.d("BookingScreen", "UI: Showing Error: $error")
                        Text(
                            text = error ?: "Неизвестная ошибка",
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    washingMachines.isEmpty() && !isLoading -> {
                        Log.d("BookingScreen", "UI: Showing Empty List Message")
                        Text(
                            text = "Доступных стиральных машин нет.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    else -> {
                        Log.d("BookingScreen", "UI: Showing LazyColumn with ${washingMachines.size} items")
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = washingMachines,
                                key = { machine -> machine.id }
                            ) { machineState ->
                                WashingMachineCard(state = machineState)
                            }
                        }
                    }
                }
            }
        }
        is AuthState.Loading -> {
            Log.d("BookingScreen", "State: Auth Loading")
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text("Проверка авторизации...", modifier = Modifier.padding(top = 60.dp))
            }
        }
        is AuthState.UnAuthenticated -> {
            Log.d("BookingScreen", "State: UnAuthenticated (waiting for redirect)")
            Box(modifier = modifier.fillMaxSize()) { }
        }
        null -> {
            Log.d("BookingScreen", "State: AuthState is null")
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> {
            Log.d("BookingScreen", "State: AuthState Else Branch")
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun BookingScreenContentPreview_Authenticated() {
    val sampleMachines = listOf(
        WashingMachineUIState(id = 1, image = "", number = 1, isActive = false, onFastWashClick = {}, onManualWashClick = {}, onCottonWashClick = {}),
        WashingMachineUIState(id = 2, image = "", number = 2, isActive = true, onFastWashClick = {}, onManualWashClick = {}, onCottonWashClick = {}),
        WashingMachineUIState(id = 3, image = "", number = 3, isActive = false, onFastWashClick = {}, onManualWashClick = {}, onCottonWashClick = {})
    )
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = sampleMachines, key = { it.id }) { machineState ->
                WashingMachineCard(state = machineState)
            }
        }
    }
}
