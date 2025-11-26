package com.beta.safalya_v2.ui.contracts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beta.safalya_v2.data.model.Contract
import com.beta.safalya_v2.data.repository.ContractsRepository

class ContractsViewModel : ViewModel() {

    private val repo = ContractsRepository()

    val contracts = MutableLiveData<List<Contract>>(emptyList())
    val loading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)

    // Whether accept/reject is in progress
    val actionLoading = MutableLiveData(false)

    /**
     * Load contracts depending on user type.
     * For MVP we do NOT use userId here. Only isFarmer matters.
     */
    fun loadContracts(isFarmer: Boolean) {
        loading.value = true

        repo.loadContracts(
            isFarmer = isFarmer,
            onSuccess = { list ->
                contracts.value = list
                loading.value = false
            },
            onFailure = { err ->
                loading.value = false
                error.value = err
            }
        )
    }

    /** Accept Contract **/
    fun acceptContract(contract: Contract) {
        actionLoading.value = true

        repo.acceptContract(
            contract = contract,
            onSuccess = {
                actionLoading.value = false
            },
            onFailure = { err ->
                actionLoading.value = false
                error.value = err
            }
        )
    }

    /** Reject Contract **/
    fun rejectContract(contract: Contract) {
        actionLoading.value = true

        repo.rejectContract(
            contract = contract,
            onSuccess = {
                actionLoading.value = false
            },
            onFailure = { err ->
                actionLoading.value = false
                error.value = err
            }
        )
    }
}
