package com.example.expense_track.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.expense_track.database.Transaction
import com.example.expense_track.databinding.TransactionLayoutBinding
import kotlin.math.abs

class TransactionAdapter(
    private val clickListener: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(val binding: TransactionLayoutBinding,
        val clickAtPosition: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
            init {
                itemView.setOnClickListener {
                    clickAtPosition(adapterPosition)
                }
            }
        }

    private val diffCallback = object : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var transactions : List<Transaction>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            TransactionLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
            )
        ) {
            clickListener(transactions[it])
        }
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val context: Context
        holder.binding.apply {
            val transaction = transactions[position]
            if (transaction.amount >= 0) {
                amount.text = "+ %.2f Ft.".format(transaction.amount)
            } else {
                amount.text = "- %.2f Ft.".format(abs(transaction.amount))
            }
            label.text = transaction.label
            context = amount.context
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }
}