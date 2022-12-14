package com.example.expense_track.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.expense_track.R
import com.example.expense_track.database.Transaction
import kotlin.math.abs

class TransactionAdapter(
    private var transactions: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {


    private lateinit var clickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(transaction: Int)
    }

    fun setClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    class ViewHolder(view: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(view) {
            val label: TextView = view.findViewById(R.id.label)
            val amount: TextView = view.findViewById(R.id.amount)
            init {
                itemView.setOnClickListener {
                    listener.onItemClick(adapterPosition)
                }
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_layout, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction: Transaction = transactions[position]
        val context: Context = holder.amount.context

        if (transaction.amount >= 0) {
            holder.amount.text = "+ %.2f Ft.".format(transaction.amount)
        } else {
            holder.amount.text = "- %.2f Ft.".format(abs(transaction.amount))
        }

        holder.label.text = transaction.label
    }

    override fun getItemCount(): Int = transactions.size

    fun setData(transactionsList: List<Transaction>) {
        val diffUtil = TransactionsDiffUtil(transactions, transactionsList)
        val result = DiffUtil.calculateDiff(diffUtil)
        transactions = transactionsList
        result.dispatchUpdatesTo(this)
    }

}

class TransactionsDiffUtil(private val oldList : List<Transaction>,
private val newList : List<Transaction>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].id == newList[newItemPosition].id -> false
            oldList[oldItemPosition].label == newList[newItemPosition].label -> false
            oldList[oldItemPosition].amount == newList[newItemPosition].amount -> false
            oldList[oldItemPosition].description == newList[newItemPosition].description -> false
            else -> true
        }
    }

}