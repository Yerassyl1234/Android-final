package com.example.ui.profile

import com.example.data.BookingHistoryRepository
import com.example.data.db.formatMillisToDateTime
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    application: Application,
    private val bookingHistoryRepository: BookingHistoryRepository,
    private val firebaseAuth:FirebaseAuth
    ) : AndroidViewModel(application) {



    private val _isLoadingHistory = MutableStateFlow(false)
    val isLoadingHistory: StateFlow<Boolean> = _isLoadingHistory.asStateFlow()

    private val _historyError = MutableStateFlow<String?>(null)
    val historyError: StateFlow<String?> = _historyError.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookingHistory: StateFlow<List<com.example.ui.profile.BookingHistoryUI>> = flow {
        val userId = firebaseAuth.currentUser?.uid
        emit(userId)
    }.flatMapLatest { userId ->
        if (userId == null) {
            flowOf(emptyList())
        } else {
            _isLoadingHistory.value = true
            _historyError.value = null
            bookingHistoryRepository.getHistoryForUser(userId)
                .map { entityList ->
                    entityList.map { entity ->
                        BookingHistoryUI(
                            id = entity.id,
                            description = "Машина ${entity.machineNumber} - ${entity.washMode}",
                            startTimeFormatted = formatMillisToDateTime(entity.startTimeMillis),
                            endTimeFormatted = formatMillisToDateTime(entity.endTimeMillis)
                        )
                    }
                }
                .onEach { _isLoadingHistory.value = false }
                .catch { e ->
                    println("Error loading history: ${e.message}")
                    _historyError.value = "Не удалось загрузить историю."
                    _isLoadingHistory.value = false
                    emit(emptyList())
                }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun deleteHistoryItem(bookingId: Long) {
        viewModelScope.launch {
            try {
                bookingHistoryRepository.deleteBooking(bookingId)
            } catch (e: Exception) {
                _historyError.value = "Не удалось удалить запись."
            }
        }
    }
}
