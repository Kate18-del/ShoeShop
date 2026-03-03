// SignUpViewModel.kt
package com.example.myfirstproject.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstproject.data.model.SignUpRequest
import com.example.shoeshop.data.RetrofitInstance

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun signUp(signUpRequest: SignUpRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userManagementService.signUp(signUpRequest)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.v("signUp", "User id: ${it.id}")
                        _signUpState.value = SignUpState.Success
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid email or password"
                        422 -> "Unable to validate email address: invalid format"
                        429 -> "Too many requests"
                        else -> "Registration failed: ${response.message()}"
                    }
                    _signUpState.value = SignUpState.Error(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.ConnectException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Connection timeout"
                    else -> "Network error: ${e.message}"
                }
                _signUpState.value = SignUpState.Error(errorMessage)
                Log.e("SignUpViewModel", e.message.toString())
            }
        }
    }

    fun resetState() {
        _signUpState.value = SignUpState.Idle
    }
}

sealed class SignUpState {
    object Idle : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}