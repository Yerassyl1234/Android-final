package com.example.data


import com.example.data.db.BookingHistoryDao
import com.example.data.db.BookingHistoryEntity
import kotlinx.coroutines.flow.Flow

class BookingHistoryRepository constructor(
    private val bookingHistoryDao: BookingHistoryDao
) {
    fun getHistoryForUser(userId: String): Flow<List<BookingHistoryEntity>> {
        return bookingHistoryDao.getHistoryForUser(userId)
    }

    suspend fun insertBooking(booking: BookingHistoryEntity) {
        bookingHistoryDao.insertBooking(booking)
    }

    suspend fun deleteBooking(bookingId: Long) {
        bookingHistoryDao.deleteBookingById(bookingId)
    }
}