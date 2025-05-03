package com.example.ui.authorization

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class AuthViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {


    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        initializeAuthStateListener()
        firebaseAuth.addAuthStateListener(authStateListener)
        _authState.value = AuthState.Loading
    }

    private fun initializeAuthStateListener() {
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user: FirebaseUser? = auth.currentUser
            if (user != null) {
                if (_authState.value !is AuthState.Authenticated) {
                    _authState.value = AuthState.Authenticated
                }
            } else {
                if (_authState.value !is AuthState.UnAuthenticated && _authState.value != AuthState.Loading) {
                    _authState.value = AuthState.UnAuthenticated
                } else if (_authState.value == AuthState.Loading) {
                    _authState.value = AuthState.UnAuthenticated
                }
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email и пароль не могут быть пустыми")
            return
        }
        _authState.value = AuthState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Ошибка входа")
                }
            }
    }

    fun signup(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email и пароль не могут быть пустыми")
            return
        }
        _authState.value = AuthState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Ошибка регистрации")
                }
            }
    }

    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    fun signout() {
        firebaseAuth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}
