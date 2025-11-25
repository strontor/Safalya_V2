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
    val actionState = MutableLiveData<Boolean>(false)

    fun loadContracts(isFarmer: Boolean) {
        loading.value = true

        repo.loadContracts(
            isFarmer,
            { list ->
                contracts.value = list
                loading.value = false
            },
            { err ->
                loading.value = false
                error.value = err
            }
        )
    }

    fun acceptContract(contract: Contract) {
        actionState.value = true

        repo.acceptContract(
            contract,
            {
                actionState.value = false
            },
            {
                actionState.value = false
                error.value = it
            }
        )
    }

    fun rejectContract(contract: Contract) {
        actionState.value = true

        repo.rejectContract(
            contract,
            {
                actionState.value = false
            },
            {
                actionState.value = false
                error.value = it
            }
        )
    }
}
