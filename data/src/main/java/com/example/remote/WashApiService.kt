package com.example.remote


import com.example.remote.dto.WashingMachineDto

import retrofit2.Response
import retrofit2.http.GET

interface WashApiService {

    @GET("machines")
    suspend fun getWashingMachines(): Response<List<WashingMachineDto>>

}