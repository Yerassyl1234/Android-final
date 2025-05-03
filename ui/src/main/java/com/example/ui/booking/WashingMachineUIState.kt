package com.example.ui.booking

import androidx.compose.runtime.Stable

@Stable
data class WashingMachineUIState(
    val id: Int,
    val image: String,
    val number: Int,
    val isActive:Boolean,
    val onFastWashClick: () -> Unit,
    val onManualWashClick: () -> Unit,
    val onCottonWashClick: () -> Unit
)