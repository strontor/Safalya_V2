package com.beta.safalya_v2.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beta.safalya_v2.data.model.Transaction
import com.beta.safalya_v2.databinding.ItemTransactionBinding

class TransactionsAdapter(private val list: List<Transaction>) :
    RecyclerView.Adapter<TransactionsAdapter.TViewHolder>() {

    inner class TViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TViewHolder {
        return TViewHolder(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TViewHolder, position: Int) {
        val tx = list[position]
        holder.binding.txId.text = tx.id
        holder.binding.txAmount.text = "₹${tx.amount}"
        holder.binding.txType.text = tx.type
    }

    override fun getItemCount() = list.size
}
