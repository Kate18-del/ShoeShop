package com.example.shoeshop.data

import android.util.Log
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

    private val _email = MutableStateFlow<String?>(null)  // Добавляем email
    val email: StateFlow<String?> = _email.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun setAuthData(userId: String, accessToken: String, refreshToken: String, email: String) {
        Log.d("AuthManager", "=== SETTING AUTH DATA ===")
        Log.d("AuthManager", "userId: $userId")
        Log.d("AuthManager", "email: $email")
        Log.d("AuthManager", "accessToken: ${accessToken.take(30)}...")

        _userId.value = userId
        _accessToken.value = accessToken
        _refreshToken.value = refreshToken
        _email.value = email  // Сохраняем email
        _isAuthenticated.value = true
    }

    fun clearAuthData() {
        Log.d("AuthManager", "=== CLEARING AUTH DATA ===")
        _userId.value = null
        _accessToken.value = null
        _refreshToken.value = null
        _email.value = null
        _isAuthenticated.value = false
    }

    fun getAuthHeaders(): Map<String, String> {
        val token = _accessToken.value
        return mapOf(
            "Authorization" to "Bearer ${token.orEmpty()}"
        )
    }

    fun printCurrentState() {
        Log.d("AuthManager", "=== CURRENT STATE ===")
        Log.d("AuthManager", "isAuthenticated: ${_isAuthenticated.value}")
        Log.d("AuthManager", "userId: ${_userId.value}")
        Log.d("AuthManager", "email: ${_email.value}")
        Log.d("AuthManager", "accessToken exists: ${_accessToken.value != null}")
    }
}