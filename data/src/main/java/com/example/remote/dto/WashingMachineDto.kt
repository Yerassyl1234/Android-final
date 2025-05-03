package com.example.remote.dto


import com.google.gson.annotations.SerializedName

data class WashingMachineDto(
    @SerializedName("id")
    val id: String?, // ID как строка

    @SerializedName("number")
    val machineNumber: Int?,

    @SerializedName("status")
    val status: String?
)