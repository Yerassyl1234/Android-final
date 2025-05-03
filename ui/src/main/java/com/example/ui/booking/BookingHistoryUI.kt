package com.example.ui.booking

data class BookingHistoryUI(
    val id: Long,
    val description: String,
    val startTimeFormatted: String,
    val endTimeFormatted: String
)