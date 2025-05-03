package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingHistoryEntity)

    @Query("SELECT * FROM booking_history WHERE userId = :userId ORDER BY startTimeMillis DESC")
    fun getHistoryForUser(userId: String): Flow<List<BookingHistoryEntity>>

    @Query("DELETE FROM booking_history WHERE id = :bookingId")
    suspend fun deleteBookingById(bookingId: Long)
}