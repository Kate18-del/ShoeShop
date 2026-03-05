package com.example.shoeshop.ui.viewmodel



import Profile
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoeshop.data.AuthManager
import com.example.shoeshop.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val profileId: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val phone: String = "",
    val originalFirstName: String = "",
    val originalLastName: String = "",
    val originalAddress: String = "",
    val originalPhone: String = "",
    val imageUri: String? = null
) {
    val hasChanges: Boolean
        get() = firstName != originalFirstName ||
                lastName != originalLastName ||
                address != originalAddress ||
                phone != originalPhone ||
                imageUri != null
}

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private var currentUserId: String = ""
    private var currentToken: String = ""

    fun initData(userId: String, token: String) {
        currentUserId = userId
        currentToken = token
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val profile = repository.getProfile(currentUserId, currentToken)
                if (profile != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            profileId = profile.id,
                            firstName = profile.firstname ?: "",
                            lastName = profile.lastname ?: "",
                            address = profile.address ?: "",
                            phone = profile.phone ?: "",
                            originalFirstName = profile.firstname ?: "",
                            originalLastName = profile.lastname ?: "",
                            originalAddress = profile.address ?: "",
                            originalPhone = profile.phone ?: ""
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile", e)
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateField(field: String, value: String) {
        _state.update { state ->
            when (field) {
                "firstName" -> state.copy(firstName = value)
                "lastName" -> state.copy(lastName = value)
                "address" -> state.copy(address = value)
                "phone" -> state.copy(phone = value)
                else -> state
            }
        }
    }

    fun saveProfile(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            try {
                val currentState = _state.value

                if (currentState.profileId != null) {
                    // Обновляем существующий профиль
                    val updates = mapOf(
                        "firstname" to currentState.firstName,
                        "lastname" to currentState.lastName,
                        "address" to currentState.address,
                        "phone" to currentState.phone
                    )
                    val updatedProfile = repository.updateProfile(currentUserId, currentToken, updates)

                    if (updatedProfile != null) {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                profileId = updatedProfile.id,
                                originalFirstName = currentState.firstName,
                                originalLastName = currentState.lastName,
                                originalAddress = currentState.address,
                                originalPhone = currentState.phone
                            )
                        }
                        onComplete(true, "Профиль обновлен")
                    } else {
                        _state.update { it.copy(isSaving = false) }
                        onComplete(false, "Ошибка при обновлении")
                    }
                } else {
                    // Создаем новый профиль - нужно получить email из AuthManager
                    val userEmail = AuthManager.email.value ?: ""

                    val newProfile = Profile(
                        user_id = currentUserId,
                        firstname = currentState.firstName,
                        lastname = currentState.lastName,
                        address = currentState.address,
                        phone = currentState.phone
                    )
                    val createdProfile = repository.createProfile(newProfile, currentToken)

                    if (createdProfile != null) {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                profileId = createdProfile.id,
                                originalFirstName = currentState.firstName,
                                originalLastName = currentState.lastName,
                                originalAddress = currentState.address,
                                originalPhone = currentState.phone
                            )
                        }
                        onComplete(true, "Профиль создан")
                    } else {
                        _state.update { it.copy(isSaving = false) }
                        onComplete(false, "Ошибка при создании")
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving profile", e)
                _state.update { it.copy(isSaving = false) }
                onComplete(false, e.message ?: "Ошибка")
            }
        }
    }

    fun resetToOriginal() {
        _state.update { state ->
            state.copy(
                firstName = state.originalFirstName,
                lastName = state.originalLastName,
                address = state.originalAddress,
                phone = state.originalPhone,
                imageUri = null
            )
        }
    }

    fun setImageUri(uri: String) {
        _state.update { it.copy(imageUri = uri) }
    }
}