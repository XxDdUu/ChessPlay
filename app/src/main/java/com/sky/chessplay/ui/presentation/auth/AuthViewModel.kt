package com.sky.chessplay.ui.presentation.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.sky.chessplay.domain.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _authState.value = if (user != null) {
            AuthState.Authenticated(user)
        } else {
            AuthState.Idle
        }
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }
    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                auth.currentUser?.let {
                    _authState.value = AuthState.Authenticated(it)
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
    }
    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                auth.currentUser?.let {
                    _authState.value = AuthState.Authenticated(it)
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Register failed")
            }
    }
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }
}
