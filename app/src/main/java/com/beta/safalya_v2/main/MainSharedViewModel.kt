package com.beta.safalya_v2.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beta.safalya_v2.data.model.AppUser
import com.beta.safalya_v2.data.repository.UserRepository
import com.beta.safalya_v2.util.UiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainSharedViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<AppUser>>(UiState.Idle)
    val userState: StateFlow<UiState<AppUser>> = _userState

    fun loadCurrentUser() {
        val userId = auth.currentUser?.uid ?: return
        _userState.value = UiState.Loading
        viewModelScope.launch {
            val result = userRepository.fetchUser(userId)
            _userState.value = if (result != null) {
                UiState.Success(result)
            } else {
                UiState.Error("User profile missing")
            }
        }
    }
}

