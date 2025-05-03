package com.example.ui.booking // Пакет в модуле :ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.data.BookingHistoryRepository
import com.example.data.WashCompletionWorker
import com.example.data.db.BookingHistoryEntity
import com.example.data.model.WashingMachineData
import com.example.orderlist.data.repository.WashingMachineRepository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay // Убедитесь, что delay импортирован
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class BookingViewModel(
    application: Application,
    private val bookingHistoryRepository: BookingHistoryRepository, // Тип из data.repository
    private val firebaseAuth: FirebaseAuth,
    private val washingMachineRepository: WashingMachineRepository // Тип из data.repository
) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val workManager = WorkManager.getInstance(context)

    // Типы StateFlow используют WashingMachineUIState из этого пакета
    private val _washingMachines = MutableStateFlow<List<WashingMachineUIState>>(emptyList())
    val washingMachines: StateFlow<List<WashingMachineUIState>> = _washingMachines.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadWashingMachines()
    }

    fun refreshWashingMachines() {
        loadWashingMachines()
    }

    private fun loadWashingMachines() {
        viewModelScope.launch {
            Log.d("BookingViewModel", "loadWashingMachines: Starting network request...")
            _isLoading.value = true
            _error.value = null
            // Используем WashingMachineRepository, тип WashingMachineData из data.model
            washingMachineRepository.getWashingMachines()
                .onSuccess { fetchedData: List<WashingMachineData> -> // Явно указал тип для ясности
                    Log.d("BookingViewModel", "loadWashingMachines: Success! Fetched ${fetchedData.size} items.")
                    // mapDataListToUIStateList принимает List<WashingMachineData>
                    _washingMachines.value = mapDataListToUIStateList(fetchedData)
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    Log.e("BookingViewModel", "loadWashingMachines: Failure: ${exception.message}", exception)
                    _error.value = "Ошибка загрузки машин: ${exception.localizedMessage ?: "Неизвестная ошибка"}"
                    _washingMachines.value = emptyList()
                    _isLoading.value = false
                }
        }
    }

    // Метод принимает List<WashingMachineData> из data.model
    private fun mapDataListToUIStateList(dataList: List<WashingMachineData>): List<WashingMachineUIState> {
        Log.d("BookingViewModel", "Mapping ${dataList.size} data models to UI states.")
        return dataList.map { data -> // data теперь типа WashingMachineData
            // Создаем WashingMachineUIState из этого пакета
            WashingMachineUIState(
                id = data.id,
                image = "washing_machine_icon", // Замените на реальное изображение если нужно
                number = data.number,
                isActive = data.isCurrentlyActive,
                // Передаем правильные параметры в лямбды
                onFastWashClick = { bookMachineAndScheduleNotification(data.id, data.number, "Быстрая стирка", 30) },
                onManualWashClick = { bookMachineAndScheduleNotification(data.id, data.number, "Ручная стирка", 60) },
                onCottonWashClick = { bookMachineAndScheduleNotification(data.id, data.number, "Хлопок", 120) }
            )
        }
    }

    // --- ВЕРНУЛИ ПАРАМЕТРЫ В ФУНКЦИЮ ---
    private fun bookMachineAndScheduleNotification(
        machineId: Int,
        machineNumber: Int,
        washMode: String,
        durationMinutes: Long
    ) {
        // ---------------------------------
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            _error.value = "Ошибка: пользователь не аутентифицирован."
            Log.w("BookingViewModel", "bookMachine requested but user is null")
            return
        }

        val currentMachineState = _washingMachines.value.find { it.id == machineId }
        if (currentMachineState?.isActive == true) {
            Log.w("BookingViewModel", "Attempted to book machine $machineNumber which is already busy.")
            _error.value = "Машина $machineNumber уже занята."
            // Запускаем корутину для сброса ошибки через некоторое время
            viewModelScope.launch {
                delay(3000)
                // Сбрасываем только если текущая ошибка - та же самая
                if (_error.value == "Машина $machineNumber уже занята.") {
                    _error.value = null
                }
            }
            return
        }

        Log.d("BookingViewModel", "Attempting to book machine $machineId ($machineNumber) - Mode: $washMode")
        viewModelScope.launch {
            // Можно добавить индикатор загрузки для конкретной карточки, если нужно
            // _isLoading.value = true // Или использовать отдельный StateFlow для ID бронируемой машины

            delay(500) // Имитация задержки бронирования

            val startTime = System.currentTimeMillis()
            val endTime = startTime + TimeUnit.MINUTES.toMillis(durationMinutes)

            // Используем BookingHistoryEntity из data.db
            val historyEntry = BookingHistoryEntity(
                machineNumber = machineNumber,
                washMode = washMode,
                startTimeMillis = startTime,
                endTimeMillis = endTime,
                userId = userId
            )

            try {
                Log.d("BookingViewModel", "Inserting booking history: $historyEntry")
                bookingHistoryRepository.insertBooking(historyEntry) // Используем репозиторий
                Log.d("BookingViewModel", "Updating machine status for ID: $machineId")
                updateMachineStatus(machineId, true) // Обновляем UI
                Log.d("BookingViewModel", "Scheduling notification for machine $machineNumber at $endTime")
                scheduleWashCompletionNotification(machineNumber, endTime) // Планируем уведомление
            } catch (dbError: Exception) {
                Log.e("BookingViewModel", "Error saving booking history", dbError)
                _error.value = "Не удалось сохранить историю бронирования."
            } finally {
                // _isLoading.value = false // Убираем общий индикатор, если не ставили
            }
        }
    }

    private fun scheduleWashCompletionNotification(machineNumber: Int, endTimeMillis: Long) {
        val currentTimeMillis = System.currentTimeMillis()
        val delayMillis = endTimeMillis - currentTimeMillis
        Log.d("BookingViewModel", "Scheduling notification with delay: $delayMillis ms")

        if (delayMillis <= 0) {
            Log.w("BookingViewModel", "Attempted to schedule notification for the past. Skipping.")
            return
        }

        val inputData = Data.Builder()
            .putInt("MACHINE_NUMBER", machineNumber)
            .putLong("END_TIME", endTimeMillis)
            .build()

        // Используем WashCompletionWorker из data.workers
        val workRequest = OneTimeWorkRequestBuilder<WashCompletionWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("wash_complete_notification_machine_$machineNumber")
            .build()

        workManager.enqueueUniqueWork(
            "wash_complete_${machineNumber}_${endTimeMillis}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        Log.i("BookingViewModel", "Notification work enqueued for machine $machineNumber")
    }

    private fun updateMachineStatus(machineId: Int, newActiveState: Boolean) {
        Log.d("BookingViewModel", "Updating state for machine $machineId to isActive=$newActiveState")
        _washingMachines.update { currentList ->
            currentList.map { machine ->
                if (machine.id == machineId) {
                    machine.copy(isActive = newActiveState)
                } else {
                    machine
                }
            }
        }
    }
}