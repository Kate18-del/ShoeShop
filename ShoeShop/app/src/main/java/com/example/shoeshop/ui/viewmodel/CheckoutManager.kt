package com.example.shoeshop.data

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CheckoutManager {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _address = MutableStateFlow("1082 Аэропорт, Нигерии")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _isEditingEmail = MutableStateFlow(false)
    val isEditingEmail: StateFlow<Boolean> = _isEditingEmail.asStateFlow()

    private val _isEditingPhone = MutableStateFlow(false)
    val isEditingPhone: StateFlow<Boolean> = _isEditingPhone.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _useGPSLocation = MutableStateFlow(false)
    val useGPSLocation: StateFlow<Boolean> = _useGPSLocation.asStateFlow()

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPhone(phone: String) {
        _phone.value = phone
    }

    fun setAddress(address: String) {
        _address.value = address
    }

    fun toggleEditEmail() {
        _isEditingEmail.value = !_isEditingEmail.value
    }

    fun toggleEditPhone() {
        _isEditingPhone.value = !_isEditingPhone.value
    }

    fun saveEmail(newEmail: String) {
        _email.value = newEmail
        _isEditingEmail.value = false
    }

    fun savePhone(newPhone: String) {
        _phone.value = newPhone
        _isEditingPhone.value = false
    }

    fun setLocation(location: Location?) {
        _currentLocation.value = location
    }

    fun setUseGPSLocation(use: Boolean) {
        _useGPSLocation.value = use
    }

    fun getDisplayAddress(): String {
        return if (_useGPSLocation.value && _currentLocation.value != null) {
            val loc = _currentLocation.value!!
            "${loc.latitude}, ${loc.longitude}"
        } else {
            _address.value
        }
    }

    fun reset() {
        _email.value = ""
        _phone.value = ""
        _address.value = "1082 Аэропорт, Нигерии"
        _isEditingEmail.value = false
        _isEditingPhone.value = false
        _useGPSLocation.value = false
    }

    fun startEditingEmail() {
        _isEditingEmail.value = true
    }

    fun startEditingPhone() {
        _isEditingPhone.value = true
    }

    fun cancelEditingEmail() {
        _isEditingEmail.value = false
    }

    fun cancelEditingPhone() {
        _isEditingPhone.value = false
    }
}