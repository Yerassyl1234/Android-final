package com.example.data

import androidx.room.Room
import com.example.data.db.AppDatabase
import com.example.orderlist.data.repository.WashingMachineRepository
import com.example.orderlist.data.repository.WashingMachineRepositoryImpl
import com.example.remote.WashApiService

import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single { FirebaseAuth.getInstance() }
    single { AppDatabase.getDatabase(androidContext()) } // Упрощено
    single { get<AppDatabase>().bookingHistoryDao() }
    single { BookingHistoryRepository(bookingHistoryDao = get()) }

    single { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }
    single { OkHttpClient.Builder().addInterceptor(get<HttpLoggingInterceptor>()).build() }
    single {
        val baseUrl = "https://6815685532debfe95dbb85f6.mockapi.io/"
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>().create(WashApiService::class.java) }
    single<WashingMachineRepository> { WashingMachineRepositoryImpl(washApiService = get()) }
}