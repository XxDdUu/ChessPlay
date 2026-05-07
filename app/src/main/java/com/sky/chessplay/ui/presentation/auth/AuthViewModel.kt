package com.sky.chessplay.ui.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.data.remote.dto.request.LoginRequest
import com.sky.chessplay.domain.repository.AuthRepository
import com.sky.chessplay.domain.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    init {
        checkAuth()
    }
    fun onEmailChange(value: String) {
        _email.value = value
    }
    fun onNextClick() {
        val email = _email.value

        if (email.isBlank()) {
            _authState.value = AuthState.Error("Email không được để trống")
            return
        }
        _authState.value = AuthState.Error("Chưa implement password screen 😅")
    }

    fun checkAuth() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repo.getMe()

            _authState.value = result.fold(
                onSuccess = { user ->
                    AuthState.Authenticated(user)
                },
                onFailure = {
                    AuthState.Idle
                }
            )
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val request = LoginRequest(
                email = email,
                password = password
            )

            val result = repo.login(request)

            _authState.value = result.fold(
                onSuccess = { AuthState.Authenticated(it) },
                onFailure = { AuthState.Error(it.message ?: "Error") }
            )
        }
    }
    fun register(email: String, password: String, username: String, countryCode: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                val response = repo.login(
                    LoginRequest(email, password)
                )

                _authState.value = response.fold(
                    onSuccess = { user ->
                        AuthState.Authenticated(user)
                    },
                    onFailure = { e ->
                        AuthState.Error(e.message ?: "Error")
                    }
                )

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }
    fun logout() {
        _authState.value = AuthState.Idle
    }
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repo.loginWithGoogle(idToken)

            result.fold(
                onSuccess = {
                    val meResult = repo.getMe()

                    _authState.value = meResult.fold(
                        onSuccess = { user ->
                            AuthState.Authenticated(user)
                        },
                        onFailure = {
                            AuthState.Error("GetMe failed")
                        }
                    )
                },
                onFailure = {
                    _authState.value =
                        AuthState.Error(it.message ?: "Google login failed")
                }
            )
        }
    }
    fun onGoogleClick(idToken: String) {
        signInWithGoogle(idToken)
    }
}
