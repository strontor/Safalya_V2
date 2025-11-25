package com.beta.safalya_v2.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beta.safalya_v2.data.model.Contract
import com.beta.safalya_v2.databinding.ItemContractBinding

class ContractsAdapter(
    private val isFarmer: Boolean,
    private val onAction: (Contract, ContractAction) -> Unit
) : ListAdapter<Contract, ContractsAdapter.ContractViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractViewHolder {
        val binding = ItemContractBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContractViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContractViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ContractViewHolder(
        private val binding: ItemContractBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contract: Contract) {
            binding.contractId.text = contract.id
            binding.status.text = contract.status.uppercase()
            binding.listingId.text = contract.listingId
            binding.actionGroup.isVisible = isFarmer && contract.status == "pending"
            binding.acceptButton.setOnClickListener { onAction(contract, ContractAction.ACCEPT) }
            binding.rejectButton.setOnClickListener { onAction(contract, ContractAction.REJECT) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Contract>() {
        override fun areItemsTheSame(oldItem: Contract, newItem: Contract): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Contract, newItem: Contract): Boolean =
            oldItem == newItem
    }
}

enum class ContractAction {
    ACCEPT,
    REJECT
}

