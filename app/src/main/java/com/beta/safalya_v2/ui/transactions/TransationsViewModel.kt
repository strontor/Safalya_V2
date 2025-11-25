package com.beta.safalya_v2.ui.transactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beta.safalya_v2.data.model.Transaction
import com.beta.safalya_v2.data.repository.TransactionRepository

class TransactionsViewModel : ViewModel() {

    private val repo = TransactionRepository()

    val transactions = MutableLiveData<List<Transaction>>(emptyList())
    val loading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)

    fun loadTransactions(isFarmer: Boolean) {
        loading.value = true

        repo.loadTransactions(isFarmer,
            { list ->
                loading.value = false
                transactions.value = list
            },
            {
                loading.value = false
                error.value = it
            }
        )
    }
}
