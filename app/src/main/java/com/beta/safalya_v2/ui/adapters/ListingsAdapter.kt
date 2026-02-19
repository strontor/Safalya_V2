package com.beta.safalya_v2.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beta.safalya_v2.data.model.Item
import com.beta.safalya_v2.databinding.ItemListingBinding
import com.beta.safalya_v2.util.toFormattedDate

class ListingsAdapter(
    private val onItemClick: (Item) -> Unit
) : ListAdapter<Item, ListingsAdapter.ListingViewHolder>(Diff) {

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

        fun bind(listing: Item) {
            binding.tvCrop.text = listing.cropType
            val formattedDeliveryDate = listing.deliveryDate
                .toLongOrNull()
                ?.toFormattedDate()
                ?: listing.deliveryDate
            binding.tvQuantity.text = "${listing.quantity} | $formattedDeliveryDate"
            binding.tvPrice.text = listing.price
            binding.root.setOnClickListener { onItemClick(listing) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem == newItem
    }
}
