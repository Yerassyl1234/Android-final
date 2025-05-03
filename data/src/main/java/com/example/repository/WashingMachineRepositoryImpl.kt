package com.example.orderlist.data.repository

import android.util.Log
import com.example.data.model.WashingMachineData
import com.example.remote.WashApiService
import com.example.remote.dto.WashingMachineDto

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class WashingMachineRepositoryImpl(
    private val washApiService: WashApiService
) : WashingMachineRepository {

    override suspend fun getWashingMachines(): Result<List<WashingMachineData>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("WashingMachineRepo", "Fetching from API...")
                val response = washApiService.getWashingMachines()
                Log.d("WashingMachineRepo", "API Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val dtoList: List<WashingMachineDto>? = response.body()
                    if (dtoList != null) {
                        val dataList = dtoList.mapNotNull { dto ->
                            val idInt = dto.id?.toIntOrNull() // Парсим ID
                            if (idInt == null || dto.machineNumber == null || dto.status == null) {
                                Log.w("WashingMachineRepo", "Received invalid or incomplete DTO: $dto")
                                null
                            } else {
                                WashingMachineData(
                                    id = idInt,
                                    number = dto.machineNumber,
                                    // Сравниваем со статусом из вашего JSON
                                    isCurrentlyActive = dto.status.equals("busy", ignoreCase = true) // Или другой статус для "занято"
                                )
                            }
                        }
                        Log.d("WashingMachineRepo", "Mapping successful: ${dataList.size} items")
                        Result.success(dataList)
                    } else {
                        Log.w("WashingMachineRepo", "API response body is null")
                        Result.failure(IOException("Empty response body"))
                    }
                } else {
                    Log.e("WashingMachineRepo", "API error: ${response.code()} ${response.message()}")
                    Result.failure(IOException("HTTP error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("WashingMachineRepo", "Exception during API call", e)
                Result.failure(e)
            }
        }
    }
}