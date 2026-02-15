package com.beta.safalya_v2.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beta.safalya_v2.databinding.ItemInterestedBuyerBinding

class InterestedBuyersAdapter(
    private val onAcceptClick: (String) -> Unit
) : ListAdapter<Pair<String, String>, InterestedBuyersAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemInterestedBuyerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(
        private val binding: ItemInterestedBuyerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pair<String, String>) {
            val requestId = item.first
            val buyerId = item.second

            binding.tvBuyer.text = buyerId
            binding.btnAccept.setOnClickListener {
                onAcceptClick(requestId)
            }
        }
    }

    object Diff : DiffUtil.ItemCallback<Pair<String, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>
        ) = oldItem.first == newItem.first

        override fun areContentsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>
        ) = oldItem == newItem
    }
}
