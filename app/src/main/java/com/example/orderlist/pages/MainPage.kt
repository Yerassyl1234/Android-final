package com.example.orderlist.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.orderlist.AuthState
import com.example.orderlist.AuthViewModel

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState=authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value){
        when(authState.value){
        is AuthState.UnAuthenticated -> navController.navigate("login")
        else -> Unit
    }}
}