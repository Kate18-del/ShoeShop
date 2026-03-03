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
    val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
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
                    Log.e("signIn", "Error code: ${response.code()}, message: ${response.message()}, body: ${response.errorBody()?.string()}")
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
        // Добавьте StateFlow для отслеживания состояния смены пароля






    }
    // Метод для смены пароля
    fun changePassword(token: String, newPassword: String) {
        viewModelScope.launch {
            _changePasswordState.value = ChangePasswordState.Loading
            try {
                val response = RetrofitInstance.userManagementService.changePassword(
                    token = "Bearer $token",
                    changePasswordRequest = ChangePasswordRequest(password = newPassword)
                )

                if (response.isSuccessful && response.body() != null) {
                    _changePasswordState.value = ChangePasswordState.Success
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _changePasswordState.value = ChangePasswordState.Error("Failed to change password: $errorMessage")
                }
            } catch (e: Exception) {
                _changePasswordState.value = ChangePasswordState.Error("Network error: ${e.message}")
            }
        }
    }
    // Метод для сброса состояния
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