package com.example.orderlist.data.repository


import com.example.data.model.WashingMachineData


interface WashingMachineRepository {

    suspend fun getWashingMachines(): Result<List<WashingMachineData>>
}