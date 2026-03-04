package com.example.shoeshop.data.repository

import Profile
import android.util.Log
import com.example.shoeshop.data.RetrofitInstance
import com.example.shoeshop.data.model.UpdateProfileRequest

class ProfileRepository {

    private val service = RetrofitInstance.userManagementService
    private val tag = "ProfileRepository"



    // Создать новый профиль
    suspend fun createProfile(profile: Profile, token: String): Profile? {
        return try {
            Log.d(tag, "Creating profile for user: ${profile.user_id}")
            val response = service.createProfile(profile, "Bearer $token")

            if (response.isSuccessful) {
                val createdProfiles = response.body()
                Log.d(tag, "Profile created successfully: $createdProfiles")
                createdProfiles?.firstOrNull()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in createProfile", e)
            null
        }
    }

    // Обновить существующий профиль
    suspend fun updateProfile(userId: String, token: String, updates: Map<String, Any>): Profile? {
        return try {
            Log.d(tag, "Updating profile for user: $userId with updates: $updates")

            // Правильный формат фильтра для Supabase
            val filter = "eq.$userId"

            val request = UpdateProfileRequest(
                firstname = updates["firstname"] as? String,
                lastname = updates["lastname"] as? String,
                address = updates["address"] as? String,
                phone = updates["phone"] as? String
            )

            val response = service.updateProfile(filter, request, "Bearer $token")

            if (response.isSuccessful) {
                val updatedProfiles = response.body()
                Log.d(tag, "Profile updated successfully: $updatedProfiles")
                updatedProfiles?.firstOrNull()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in updateProfile", e)
            null
        }
    }

    // Получить профиль пользователя
    suspend fun getProfile(userId: String, token: String): Profile? {
        return try {
            Log.d(tag, "Getting profile for user: $userId")

            // Правильный формат фильтра для Supabase
            val filter = "eq.$userId"

            val response = service.getProfile(filter, "Bearer $token")

            if (response.isSuccessful) {
                val profiles = response.body()
                Log.d(tag, "Profile loaded: $profiles")
                profiles?.firstOrNull()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in getProfile", e)
            null
        }
    }
}