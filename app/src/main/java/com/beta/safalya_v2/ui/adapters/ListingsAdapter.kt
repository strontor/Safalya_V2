package com.beta.safalya_v2.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beta.safalya_v2.data.model.Listing
import com.beta.safalya_v2.databinding.ItemListingBinding

class ListingsAdapter(
    private val onItemClick: (Listing) -> Unit
) : ListAdapter<Listing, ListingsAdapter.ListingViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val binding = ItemListingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    inner class ListingViewHolder(
        private val binding: ItemListingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listing: Listing) {

            binding.cropType.text = listing.cropType.replaceFirstChar { it.uppercase() }
            binding.quantity.text = "${listing.quantity} kg" // with unit
            binding.price.text = "₹${listing.price}" //with rupee symbol
            binding.root.setOnClickListener { onItemClick(listing) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Listing>() {
        override fun areItemsTheSame(oldItem: Listing, newItem: Listing): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Listing, newItem: Listing): Boolean =
            oldItem == newItem
    }
}
