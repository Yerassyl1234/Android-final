package com.example.ui.authorization
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule=module{
    viewModel{AuthViewModel(firebaseAuth = get())}
}