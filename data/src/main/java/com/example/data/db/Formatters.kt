package com.example.data.db

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formatMillisToDateTime(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("ru"))
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(millis))
}