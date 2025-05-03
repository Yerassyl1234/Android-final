package com.example.ui.profile

data class BookingHistoryUI(
    val id: Long,
    val description: String,
    val startTimeFormatted: String,
    val endTimeFormatted: String
)