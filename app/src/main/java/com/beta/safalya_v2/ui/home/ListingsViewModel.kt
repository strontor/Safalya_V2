package com.beta.safalya_v2.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beta.safalya_v2.data.model.Listing
import com.beta.safalya_v2.data.repository.ListingRepository

class ListingsViewModel : ViewModel() {

    private val repo = ListingRepository()

    val myListings = MutableLiveData<List<Listing>>(emptyList())
    val activeListings = MutableLiveData<List<Listing>>(emptyList())
    val loading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)

    // Create Listing (Farmer)
    fun createListing(
        cropType: String,
        quantity: String,
        price: String,
        deliveryDate: String,
        description: String,
        onDone: () -> Unit
    ) {
        loading.value = true

        repo.createListing(
            cropType,
            quantity,
            price,
            deliveryDate,
            description,
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

    // Farmer Listings
    fun loadMyListings() {
        loading.value = true
        repo.loadMyListings(
            {
                myListings.value = it
                loading.value = false
            },
            {
                loading.value = false
                error.value = it
            }
        )
    }

    // Buyer Listings
    fun loadActiveListings() {
        loading.value = true
        repo.loadActiveListings(
            {
                activeListings.value = it
                loading.value = false
            },
            {
                loading.value = false
                error.value = it
            }
        )
    }
}
