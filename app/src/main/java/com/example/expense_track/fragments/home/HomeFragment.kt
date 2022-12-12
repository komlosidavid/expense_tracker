package com.example.expense_track.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.expense_track.R
import com.example.expense_track.adapters.TransactionAdapter
import com.example.expense_track.database.Transaction
import com.example.expense_track.database.TransactionsDatabase
import com.example.expense_track.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions: List<Transaction>
    private lateinit var oldTransactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var database : TransactionsDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val addTransactionButton = view.findViewById<FloatingActionButton>(R.id.addBtn)
        addTransactionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }

        val todoButton = view.findViewById<FloatingActionButton>(R.id.todoBtn)
        todoButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_todoFragment)
        }

        transactions = emptyList()

        fun setUp() = view.findViewById<RecyclerView>(R.id.recyclerview).apply {
            transactionAdapter = TransactionAdapter() {
                val toast = Toast.makeText(container?.context, "valami", Toast.LENGTH_SHORT)
                toast.show()
            }
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(container?.context)
        }

        setUp()

        if (container != null) {
            linearLayoutManager = LinearLayoutManager(container.context)
            database = Room.databaseBuilder(container.context,
                TransactionsDatabase::class.java, "transactions").build()
        }

        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = linearLayoutManager
        recyclerview.adapter = transactionAdapter

        // remove by swipe
        val itemTouchHelper = object : ItemTouchHelper
        .SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerview)

        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchAll() {
        GlobalScope.launch {
            transactions = database.transactionsDatabaseDao.getAll()
            activity?.runOnUiThread {
                updateDashboard()
                transactionAdapter.transactions = transactions
            }
        }
    }

    private fun updateDashboard() {
        val totalAmount = transactions.map { it.amount }.sum()
        val budgetAmount = transactions
            .filter { it.amount > 0 }
            .map { it.amount }
            .sum()
        val expenseAmount = totalAmount - budgetAmount

        val balance = view?.findViewById<TextView>(R.id.balance)
        if (balance != null) {
            balance.text = "%.2f Ft.".format(totalAmount)
        }
        val budget = view?.findViewById<TextView>(R.id.budget)
        if (budget != null) {
            budget.text = "%.2f Ft.".format(budgetAmount)
        }
        val expense = view?.findViewById<TextView>(R.id.expense)
        if (expense != null) {
            expense.text = "%.2f Ft.".format(expenseAmount)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun undoDelete() {
        GlobalScope.launch {
            database.transactionsDatabaseDao.insertAll(deletedTransaction)
            transactions = oldTransactions
            activity?.runOnUiThread {
                updateDashboard()
                transactionAdapter.transactions = transactions
            }
        }
    }

    private fun showSnackBar() {
        val temp = view?.findViewById<View>(R.id.coordinator)
        val snackBar = temp?.let { Snackbar.make(it, "Transaction deleted", Snackbar.LENGTH_LONG) }
        snackBar?.setAction("Undo") {
            undoDelete()
        }?.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            ?.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))?.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteTransaction(transaction: Transaction) {
        deletedTransaction = transaction
        oldTransactions = transactions
        GlobalScope.launch {
            database.transactionsDatabaseDao.delete(transaction)
            transactions = transactions.filter { it.id != transaction.id }
            activity?.runOnUiThread {
                updateDashboard()
                transactionAdapter.transactions = transactions
                showSnackBar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}