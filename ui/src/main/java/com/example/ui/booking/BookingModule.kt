package com.example.ui.booking

import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val bookingModule = module {
    viewModel {
        BookingViewModel(
            application = get(),
            bookingHistoryRepository = get(),
            firebaseAuth = get(),
            washingMachineRepository = get()
        )
    }
}