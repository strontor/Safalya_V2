package com.beta.safalya_v2.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beta.safalya_v2.data.model.Item
import com.beta.safalya_v2.data.model.ContractState
import com.beta.safalya_v2.data.repository.ListingRepository

class ListingsViewModel : ViewModel() {

    private val repo = ListingRepository()

    val myListings = MutableLiveData<List<Item>>(emptyList())
    val activeListings = MutableLiveData<List<Item>>(emptyList())
    val loading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)

    // Create Listing (Farmer)
    fun createListing(
        cropType: String,
        quantity: String,
        price: String,
        deliveryDate: Long,
        description: String,
        itemType: String,
        onDone: () -> Unit
    ) {
        loading.value = true

        repo.createListing(
            cropType = cropType,
            quantity = quantity,
            price = price,
            deliveryDate = deliveryDate.toString(),
            description = description,
            itemType = itemType,   //👈 THIS FIXES EVERYTHING
            onSuccess = {
                loading.value = false
                onDone()
            },
            onFailure = { err ->
                loading.value = false
                error.value = err
            }
        )

    }

    //farmer Listings
    fun loadSellListings() { //for buyers
        loading.value = true
        repo.loadActiveSellListings(
            onSuccess = {
                activeListings.value = it
                loading.value = false
            },
            onFailure = {
                error.value = it
                loading.value = false
            }
        )
    }

    fun loadBuyOrders() { //for farmers
        loading.value = true
        repo.loadActiveBuyOrders(
            onSuccess = {
                activeListings.value = it
                loading.value = false
            },
            onFailure = {
                error.value = it
                loading.value = false
            }
        )
    }

    fun requestContract( //for buyers
        itemId: String,
        farmerId: String,
        onDone: () -> Unit
    ) {
        loading.value = true

        repo.requestContract(
            itemId,
            farmerId,
            {
                loading.value = false
                onDone()
            },
            {
                loading.value = false
                error.value = it
            }
        )
    }

    fun loadInterestedBuyers( //for farmers
        itemId: String,
        onSuccess: (List<Pair<String, String>>) -> Unit
    ) {
        repo.loadInterestedBuyers(
            itemId,
            onSuccess,
            { error.value = it }
        )
    }

    fun loadMySellListings() { //for farmers
        loading.value = true
        repo.loadActiveSellListings(
            onSuccess = { result ->
                myListings.postValue(result)
                loading.value = false
            },
            onFailure = { err ->
                error.value = err
                loading.value = false
            }
        )
    }
    fun loadMyBuyOrders() {  //for buyers
        loading.value = true
        repo.loadActiveBuyOrders(
            onSuccess = { result ->
                myListings.postValue(result)
                loading.value = false
            },
            onFailure = { err ->
                error.value = err
                loading.value = false
            }
        )
    }
    fun acceptBuyer(
        itemId: String,
        requestId: String,
        farmerId: String,
        buyerId: String,
        onDone: () -> Unit
    ) {
        loading.value = true

        repo.acceptBuyer(
            itemId = itemId,
            acceptedRequestId = requestId,
            farmerId = farmerId,
            buyerId = buyerId,
            onSuccess = {
                loading.value = false
                onDone()
            },
            onFailure = {
                loading.value = false
                error.value = it
            }
        )
    }
    fun checkBuyerRequestStatus(
        itemId: String,
        buyerId: String,
        onResult: (ContractState) -> Unit
    ) {
        repo.getBuyerRequestStatus(
            itemId,
            buyerId,
            onResult = { status ->
                val state = when (status) {
                    "pending" -> ContractState.REQUESTED
                    "accepted" -> ContractState.ACCEPTED
                    "rejected" -> ContractState.REJECTED
                    else -> ContractState.NONE
                }
                onResult(state)
            },
            onError = { error.value = it }
        )
    }
}
