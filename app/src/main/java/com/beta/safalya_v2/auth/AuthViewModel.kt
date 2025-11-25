// src/main/java/com/yourpackagename/viewmodel/AuthViewModel.kt
package com.safalya_v2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yourpackagename.repo.AuthRepository

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _userRole = MutableLiveData<String?>()
    val userRole: LiveData<String?> = _userRole

    fun register(email: String, pass: String, name: String, role: String, phone: String, onSuccess: () -> Unit) {
        _loading.value = true
        repo.register(email, pass, name, role, phone,
            onSuccess = { uid ->
                _loading.value = false
                onSuccess()
            },
            onError = { err ->
                _loading.value = false
                _error.value = err
            })
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        _loading.value = true
        repo.login(email, pass,
            onSuccess = { uid ->
                // fetch user to get role
                repo.fetchUser(uid,
                    onSuccess = { data ->
                        _loading.value = false
                        val role = data["role"] as? String
                        _userRole.value = role
                        onSuccess()
                    },
                    onError = { e ->
                        _loading.value = false
                        _error.value = e
                    })
            },
            onError = { e ->
                _loading.value = false
                _error.value = e
            })
    }

    fun clearError() { _error.value = null }
}
