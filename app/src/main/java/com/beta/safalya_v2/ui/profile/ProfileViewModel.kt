package com.beta.safalya_v2.ui.profile

import androidx.lifecycle.ViewModel
import com.beta.safalya_v2.data.repository.AuthRepository

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    fun logout() {
        authRepository.logout()
    }
}

