package com.example.ui.profile
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val profileModule = module{
    viewModel {
        ProfileViewModel(
            application = get(),
            bookingHistoryRepository = get(),
            firebaseAuth = get()
        )
    }
}