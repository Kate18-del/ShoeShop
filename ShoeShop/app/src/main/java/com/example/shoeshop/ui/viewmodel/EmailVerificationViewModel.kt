
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstproject.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import com.example.shoeshop.data.RetrofitInstance
import kotlinx.coroutines.launch
import kotlin.let
import kotlin.run

class EmailVerificationViewModel : ViewModel() {

    // Общее состояние для обоих типов
    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState: StateFlow<VerificationState> = _verificationState

    // Отдельное состояние для recovery (если нужны разные данные)
    private val _recoveryState = MutableStateFlow<RecoveryState?>(null)
    val recoveryState: StateFlow<RecoveryState?> = _recoveryState

    /**
     * Верификация OTP для подтверждения email
     */
    fun verifyEmailOtp(email: String, otpCode: String) {
        verifyOtpInternal(email, otpCode, OtpType.EMAIL)
    }

    /**
     * Верификация OTP для восстановления пароля
     */
    fun verifyRecoveryOtp(email: String, otpCode: String) {
        verifyOtpInternal(email, otpCode, OtpType.RECOVERY)
    }

    private fun verifyOtpInternal(email: String, otpCode: String, otpType: OtpType) {
        viewModelScope.launch {
            try {
                _verificationState.value = VerificationState.Loading

                when (otpType) {
                    OtpType.RECOVERY -> {
                        Log.d("DEBUG1", "=== НАЧАЛО verifyRecoveryOtp ===")
                        Log.d("DEBUG1", "email: $email")
                        Log.d("DEBUG1", "otpCode: $otpCode")

                        val response = RetrofitInstance.userManagementService.verifyOtp(
                            VerifyOtpRequest(
                                email = email,
                                token = otpCode,
                                type = "recovery"
                            )
                        )

                        Log.d("DEBUG1", "response.code(): ${response.code()}")

                        if (response.isSuccessful) {
                            val body = response.body()
                            Log.d("DEBUG1", "body: $body")

                            if (body != null) {
                                Log.d("DEBUG1", "body.access_token: ${body.access_token}")
                                Log.d("DEBUG1", "body.refresh_token: ${body.refresh_token}")

                                // СОХРАНЯЕМ ТОКЕН
                                _recoveryState.value = RecoveryState(
                                    resetToken = body.refresh_token,
                                    email = email
                                )
                                Log.d("DEBUG1", "_recoveryState.value установлен: ${_recoveryState.value}")

                                _verificationState.value = VerificationState.Success(
                                    type = OtpType.RECOVERY,
                                    data = body
                                )
                                Log.d("DEBUG1", "_verificationState.value установлен: Success")
                            }
                        } else {
                            Log.d("DEBUG1", "response.errorBody(): ${response.errorBody()?.string()}")
                        }

                    }

                    OtpType.EMAIL -> {
                        // Для подтверждения email используем type = "email"
                        val request = VerifyOtpRequest(
                            email = email,
                            token = otpCode,
                            type = "email"
                        )

                        val response = RetrofitInstance.userManagementService.verifyOtp(request)

                        if (response.isSuccessful) {
                            response.body()?.let { verifyResponse ->
                                _verificationState.value = VerificationState.Success(
                                    type = OtpType.EMAIL,
                                    data = verifyResponse
                                )
                            } ?: run {
                                _verificationState.value = VerificationState.Error("Empty response")
                            }
                        } else {
                            val errorMessage = when (response.code()) {
                                401 -> "Неверный код подтверждения"
                                else -> "Ошибка: ${response.message()}"
                            }
                            _verificationState.value = VerificationState.Error(errorMessage)
                        }
                    }
                }
            } catch (e: Exception) {
                _verificationState.value = VerificationState.Error(e.message ?: "Unknown error")
                Log.e("RecoveryOTP", "Exception: ${e.message}", e)
            }
        }
    }

    private fun parseVerificationError(code: Int, message: String, otpType: OtpType): String {
        return when (code) {
            400 -> when (otpType) {
                OtpType.EMAIL -> "Invalid OTP code"
                OtpType.RECOVERY -> "Invalid recovery code"
            }
            401 -> when (otpType) {
                OtpType.EMAIL -> "OTP expired or invalid"
                OtpType.RECOVERY -> "Recovery code expired or invalid"
            }
            404 -> when (otpType) {
                OtpType.EMAIL -> "Email not found"
                OtpType.RECOVERY -> "Email not found or no recovery request"
            }
            429 -> "Too many attempts. Please try again later."
            else -> "Verification failed: $message"
        }
    }

    fun resetState() {
        _verificationState.value = VerificationState.Idle
        _recoveryState.value = null
    }


    fun getResetToken(): String? {
        val token = _recoveryState.value?.resetToken
        Log.d("DEBUG3", "getResetToken() вызван, возвращает: $token")
        return token
    }
}

// Типы OTP
enum class OtpType {
    EMAIL, // Подтверждение email
    RECOVERY // Восстановление пароля
}

// Общее состояние верификации
sealed class VerificationState {
    object Idle : VerificationState()
    object Loading : VerificationState()
    data class Success(
        val type: OtpType,
        val data: Any? = null
    ) : VerificationState()
    data class Error(val message: String) : VerificationState()
}

// Специфичное состояние для recovery
data class RecoveryState(
    val resetToken: String?,
    val email: String
)

