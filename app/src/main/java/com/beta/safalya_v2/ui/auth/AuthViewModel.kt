package com.beta.safalya_v2.ui.auth

//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.beta.safalya_v2.data.repository.AuthRepository
//
//class AuthViewModel : ViewModel() {
//
//    private val repo = AuthRepository()
//
//    val loading = MutableLiveData(false)
//    val error = MutableLiveData<String?>(null)
//    val role = MutableLiveData<String?>(null)
//
//    fun register(name: String, email: String, pass: String, phone: String, role: String, onDone: () -> Unit) {
//        loading.value = true
//
//        repo.register(name, email, pass, phone, role, {
//            loading.value = false
//            onDone()
//        }, {
//            loading.value = false
//            error.value = it
//        })
//    }
//
//    fun login(email: String, password: String, onDone: () -> Unit) {
//        loading.value = true
//
//        repo.login(email, password, { uid ->
//            repo.fetchUserRole(uid, { fetchedRole ->
//                loading.value = false
//                role.value = fetchedRole
//                onDone()
//            }, {
//                loading.value = false
//                error.value = it
//            })
//        }, {
//            loading.value = false
//            error.value = it
//        })
//    }
//}
