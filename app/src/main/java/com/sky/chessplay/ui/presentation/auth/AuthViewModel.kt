package com.sky.chessplay.ui.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.data.local.datastore.TokenManager
import com.sky.chessplay.data.remote.dto.request.LoginRequest
import com.sky.chessplay.domain.repository.AuthRepository
import com.sky.chessplay.domain.socket.ChessSocket
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
    REGISTER_DETAILS,
    VERIFY_OTP
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val tokenManager: TokenManager,
    private val chessSocket: ChessSocket
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

    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

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

    fun onOtpChange(value: String) {
        _otp.value = value
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
                    connectSocket()
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

            result.fold(
                onSuccess = {
                    val meResult = repo.getMe()
                    _authState.value = meResult.fold(
                        onSuccess = { user ->
                            connectSocket()
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
                        _otp.value = ""
                        _currentStep.value = AuthStep.VERIFY_OTP
                        AuthState.Success("Đăng ký thành công. Vui lòng nhập mã OTP gửi tới email của bạn.")
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

    fun onVerifyOtpClick() {
        val emailVal = _email.value.trim()
        val otpVal = _otp.value.trim()

        if (otpVal.isBlank()) {
            _authState.value = AuthState.Error("OTP không được để trống")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repo.verifyOtp(emailVal, otpVal)
            _authState.value = result.fold(
                onSuccess = {
                    _email.value = emailVal
                    _password.value = ""
                    _isLoginMode.value = true
                    _currentStep.value = AuthStep.EMAIL
                    AuthState.Success("Xác thực OTP thành công! Vui lòng đăng nhập.")
                },
                onFailure = { e ->
                    AuthState.Error(e.message ?: "Xác thực OTP thất bại")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            chessSocket.disconnect()
            repo.logout()
            _authState.value = AuthState.Unauthenticated
            _currentStep.value = AuthStep.EMAIL
        }
    }

    private fun connectSocket() {
        viewModelScope.launch {
            tokenManager.getToken()?.let { token ->
                chessSocket.connect(token)
            }
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
                            connectSocket()
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
