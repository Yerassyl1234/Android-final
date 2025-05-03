package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking_history")
data class BookingHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val machineNumber: Int,
    val washMode: String, // "Быстрая стирка", "Ручная стирка", "Хлопок"
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val userId: String // ID пользователя Firebase для связи
)