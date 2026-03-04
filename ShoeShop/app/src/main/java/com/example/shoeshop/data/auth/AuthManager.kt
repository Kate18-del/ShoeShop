import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


object AuthManager {
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    private val _refreshToken = MutableStateFlow<String?>(null)
    val refreshToken: StateFlow<String?> = _refreshToken.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun setAuthData(userId: String, accessToken: String, refreshToken: String) {
        _userId.value = userId
        _accessToken.value = accessToken
        _refreshToken.value = refreshToken
        _isAuthenticated.value = true

        // Логируем для отладки
        android.util.Log.d("AuthManager", "Token saved: ${accessToken.take(20)}...")
        android.util.Log.d("AuthManager", "Token parts: ${accessToken.split(".").size}")
    }

    fun clearAuthData() {
        _userId.value = null
        _accessToken.value = null
        _refreshToken.value = null
        _isAuthenticated.value = false
    }

    fun getAuthHeaders(): Map<String, String> {
        return mapOf(
            "Authorization" to "Bearer ${_accessToken.value.orEmpty()}"
        )
    }
}