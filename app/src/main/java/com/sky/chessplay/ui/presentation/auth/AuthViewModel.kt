package com.sky.chessplay.ui.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.data.remote.dto.request.LoginRequest
import com.sky.chessplay.domain.repository.AuthRepository
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.remote.dto.request.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthStep {
    EMAIL,
    LOGIN_PASSWORD,
    REGISTER_DETAILS
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentStep = MutableStateFlow(AuthStep.EMAIL)
    val currentStep: StateFlow<AuthStep> = _currentStep.asStateFlow()

    private val _isLoginMode = MutableStateFlow(true)
    val isLoginMode: StateFlow<Boolean> = _isLoginMode.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _countryCode = MutableStateFlow("VN")
    val countryCode: StateFlow<String> = _countryCode.asStateFlow()

    init {
        checkAuth()
    }

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onUsernameChange(value: String) {
        _username.value = value
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
    }

    fun onCountryCodeChange(value: String) {
        _countryCode.value = value
    }

    fun setLoginMode(isLogin: Boolean) {
        _isLoginMode.value = isLogin
    }

    fun goToStep(step: AuthStep) {
        _currentStep.value = step
    }

    fun goBack() {
        if (_currentStep.value != AuthStep.EMAIL) {
            _currentStep.value = AuthStep.EMAIL
            _authState.value = AuthState.Idle
        }
    }

    fun onNextClick() {
        val emailVal = _email.value.trim()

        if (emailVal.isBlank()) {
            _authState.value = AuthState.Error("Email không được để trống")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
            _authState.value = AuthState.Error("Email không hợp lệ")
            return
        }

        _authState.value = AuthState.Idle
        if (_isLoginMode.value) {
            _currentStep.value = AuthStep.LOGIN_PASSWORD
        } else {
            _currentStep.value = AuthStep.REGISTER_DETAILS
        }
    }

    fun onLoginClick() {
        val emailVal = _email.value.trim()
        val passwordVal = _password.value

        if (passwordVal.isBlank()) {
            _authState.value = AuthState.Error("Mật khẩu không được để trống")
            return
        }

        signIn(emailVal, passwordVal)
    }

    fun onRegisterClick() {
        val emailVal = _email.value.trim()
        val usernameVal = _username.value.trim()
        val passwordVal = _password.value
        val confirmPasswordVal = _confirmPassword.value
        val countryCodeVal = _countryCode.value

        if (usernameVal.isBlank()) {
            _authState.value = AuthState.Error("Username không được để trống")
            return
        }
        if (passwordVal.isBlank()) {
            _authState.value = AuthState.Error("Mật khẩu không được để trống")
            return
        }
        if (passwordVal != confirmPasswordVal) {
            _authState.value = AuthState.Error("Mật khẩu xác nhận không khớp")
            return
        }

        register(emailVal, passwordVal, usernameVal, countryCodeVal)
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
                onFailure = { AuthState.Error(it.message ?: "Đăng nhập thất bại") }
            )
        }
    }

    fun register(email: String, password: String, username: String, countryCode: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                val response = repo.register(
                    RegisterRequest(
                        password = password,
                        email = email,
                        username = username,
                        country_code = countryCode
                    )
                )

                _authState.value = response.fold(
                    onSuccess = {
                        // Registration success. Auto login.
                        val loginResult = repo.login(
                            LoginRequest(email = email, password = password)
                        )
                        loginResult.fold(
                            onSuccess = { AuthState.Authenticated(it) },
                            onFailure = { AuthState.Error(it.message ?: "Đăng ký thành công nhưng đăng nhập tự động thất bại") }
                        )
                    },
                    onFailure = { e ->
                        AuthState.Error(e.message ?: "Đăng ký thất bại")
                    }
                )

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Đăng ký thất bại")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _authState.value = AuthState.Unauthenticated
            _currentStep.value = AuthStep.EMAIL
        }
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
