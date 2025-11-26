package com.beta.safalya_v2.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beta.safalya_v2.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _userRole = MutableLiveData<String?>()
    val userRole: LiveData<String?> = _userRole

    /** REGISTER USER **/
    fun register(
        name: String,
        phone: String,
        email: String,
        pass: String,
        role: String,
        onSuccess: () -> Unit
    ) {
        _loading.value = true

        repo.register(
            name = name,
            email = email,
            password = pass,
            phone = phone,
            role = role,
            onSuccess = {
                _loading.value = false
                onSuccess()
            },
            onFailure = { err ->
                _loading.value = false
                _error.value = err
            }
        )
    }

    /** LOGIN USER **/
    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        _loading.value = true

        repo.login(
            email = email,
            password = pass,
            onSuccess = { uid ->

                repo.fetchUserRole(
                    uid = uid,
                    onSuccess = { role ->
                        _loading.value = false
                        _userRole.value = role
                        onSuccess()
                    },
                    onFailure = { err ->
                        _loading.value = false
                        _error.value = err
                    }
                )
            },
            onFailure = { err ->
                _loading.value = false
                _error.value = err
            }
        )
    }

    fun clearError() {
        _error.value = null
    }
}
