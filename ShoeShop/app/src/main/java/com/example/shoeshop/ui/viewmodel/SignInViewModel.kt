import SignInRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoeshop.data.RetrofitInstance
import com.example.shoeshop.data.model.ChangePasswordRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState: StateFlow<SignInState> = _signInState

    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState: StateFlow<ChangePasswordState> = _changePasswordState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userManagementService.signIn(
                    SignInRequest(email, password)
                )

                if (response.isSuccessful) {
                    response.body()?.let { signInResponse ->
                        // Сохраняем токен
                        saveAuthToken(signInResponse.access_token)
                        saveRefreshToken(signInResponse.refresh_token)
                        saveUserData(signInResponse.user)

                        Log.v("signIn", "User authenticated: ${signInResponse.user.email}")
                        _signInState.value = SignInState.Success
                    }
                } else {
                    val errorMessage = parseSignInError(response.code(), response.message())
                    _signInState.value = SignInState.Error(errorMessage)
                    Log.e("signIn", "Error code: ${response.code()}, message: ${response.message()}")
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.ConnectException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Connection timeout"
                    is javax.net.ssl.SSLHandshakeException -> "Security error"
                    else -> "Authentication failed: ${e.message}"
                }
                _signInState.value = SignInState.Error(errorMessage)
                Log.e("SignInViewModel", "Exception: ${e.message}", e)
            }
        }
    }

    /**
     * Обмен refresh_token на access_token
     */
    private suspend fun exchangeRefreshTokenForAccessToken(refreshToken: String): String? {
        return try {
            Log.d("TokenExchange", "Exchanging refresh token")

            // Создаем Map с refresh_token
            val request = mapOf("refresh_token" to refreshToken)

            val response = RetrofitInstance.userManagementService.refreshToken(request)

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("TokenExchange", "Exchange successful")

                // Извлекаем access_token из Map
                val accessToken = body?.get("access_token") as? String
                Log.d("TokenExchange", "Access token: ${accessToken?.take(20)}...")

                // Сохраняем новые токены, если они есть
                val newRefreshToken = body?.get("refresh_token") as? String
                if (accessToken != null) {
                    saveAuthToken(accessToken)
                }
                if (newRefreshToken != null) {
                    saveRefreshToken(newRefreshToken)
                }

                accessToken
            } else {
                Log.e("TokenExchange", "Exchange failed: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TokenExchange", "Exception: ${e.message}", e)
            null
        }
    }

    /**
     * Метод для смены пароля, который определяет тип токена
     */
    fun changePassword(token: String, newPassword: String) {
        Log.d("TokenDebug", "=== CHANGE PASSWORD CALLED ===")
        Log.d("TokenDebug", "Token: $token")
        Log.d("TokenDebug", "Token length: ${token.length}")

        if (token.isEmpty()) {
            _changePasswordState.value = ChangePasswordState.Error("Token is empty")
            return
        }

        // Определяем тип токена по наличию точек
        val segments = token.split(".").size
        Log.d("TokenDebug", "Token has $segments segments")

        when {
            segments == 3 -> {
                // Это access_token - используем напрямую
                Log.d("TokenDebug", "Using token as access token")
                viewModelScope.launch {
                    _changePasswordState.value = ChangePasswordState.Loading
                    try {
                        val response = RetrofitInstance.userManagementService.changePassword(
                            authorization = "Bearer $token",
                            changePasswordRequest = ChangePasswordRequest(password = newPassword)
                        )

                        if (response.isSuccessful) {
                            _changePasswordState.value = ChangePasswordState.Success
                        } else {
                            _changePasswordState.value = ChangePasswordState.Error("Failed to change password")
                        }
                    } catch (e: Exception) {
                        _changePasswordState.value = ChangePasswordState.Error(e.message ?: "Network error")
                    }
                }
            }
            segments == 1 -> {
                // Это refresh_token (простая строка) - используем специальный метод
                Log.d("TokenDebug", "Using token as simple refresh token")
                viewModelScope.launch {
                    _changePasswordState.value = ChangePasswordState.Loading
                    try {
                        // Для Supabase с простым refresh_token нужно использовать другой подход
                        // Сначала получаем access_token через специальный endpoint
                        val accessToken = exchangeSimpleRefreshTokenForAccessToken(token)

                        if (accessToken == null) {
                            _changePasswordState.value = ChangePasswordState.Error("Failed to get access token")
                            return@launch
                        }

                        val response = RetrofitInstance.userManagementService.changePassword(
                            authorization = "Bearer $accessToken",
                            changePasswordRequest = ChangePasswordRequest(password = newPassword)
                        )

                        if (response.isSuccessful) {
                            _changePasswordState.value = ChangePasswordState.Success
                        } else {
                            _changePasswordState.value = ChangePasswordState.Error("Failed to change password")
                        }
                    } catch (e: Exception) {
                        _changePasswordState.value = ChangePasswordState.Error(e.message ?: "Network error")
                    }
                }
            }
            else -> {
                Log.e("TokenDebug", "Unknown token format with $segments segments")
                _changePasswordState.value = ChangePasswordState.Error("Invalid token format")
            }
        }
    }

    // Добавьте этот метод для работы с простым refresh_token
    private suspend fun exchangeSimpleRefreshTokenForAccessToken(refreshToken: String): String? {
        return try {
            Log.d("TokenDebug", "Exchanging simple refresh token")

            // Для Supabase нужно отправить запрос на получение access_token
            // Используем тот же endpoint, но с другими параметрами
            val request = mapOf(
                "refresh_token" to refreshToken,
                "grant_type" to "refresh_token"
            )

            // Здесь должен быть специальный endpoint для обмена
            // Например: auth/v1/token?grant_type=refresh_token
            val response = RetrofitInstance.userManagementService.refreshToken(request)

            if (response.isSuccessful) {
                val body = response.body()
                val accessToken = body?.get("access_token") as? String
                Log.d("TokenDebug", "Got access token: ${accessToken?.take(20)}...")
                accessToken
            } else {
                Log.e("TokenDebug", "Failed to exchange token: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TokenDebug", "Exception: ${e.message}", e)
            null
        }

    }

    /**
     * Сброс состояния смены пароля
     */
    fun resetChangePasswordState() {
        _changePasswordState.value = ChangePasswordState.Idle
    }

    private fun parseSignInError(code: Int, message: String): String {
        return when (code) {
            400 -> "Invalid email or password"
            401 -> "Invalid login credentials"
            422 -> "Invalid email format"
            429 -> "Too many login attempts. Please try again later."
            500 -> "Server error. Please try again later."
            else -> "Login failed: $message"
        }
    }

    private fun saveAuthToken(token: String) {
        // TODO: Сохранить токен в SecurePreferences
        Log.d("Auth", "Access token saved: ${token.take(10)}...")
    }

    private fun saveRefreshToken(token: String) {
        // TODO: Сохранить refresh токен
        Log.d("Auth", "Refresh token saved: ${token.take(10)}...")
    }

    private fun saveUserData(user: com.example.myfirstproject.data.model.User) {
        // TODO: Сохранить данные пользователя
        Log.d("Auth", "User data saved: ${user.email}")
    }

    fun resetState() {
        _signInState.value = SignInState.Idle
    }
}

sealed class SignInState {
    object Idle : SignInState()
    object Success : SignInState()
    data class Error(val message: String) : SignInState()
}

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}