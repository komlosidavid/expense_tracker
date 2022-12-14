package com.example.expense_track.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.example.expense_track.R
import com.example.expense_track.database.Transaction
import com.example.expense_track.database.TransactionsDatabase
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

class DetailsFragment : Fragment() {

    private lateinit var database : TransactionsDatabase
    private lateinit var transaction : Transaction

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        setUp()
        val args : DetailsFragmentArgs by navArgs()
        val transactionId : Long = args.transactionId
        fetchTransaction(transactionId)

        val closeBtn = view.findViewById<ImageButton>(R.id.closebtn)
        val labelLayout = view.findViewById<TextInputLayout>(R.id.labelLayout)
        val amountLayout = view.findViewById<TextInputLayout>(R.id.amountLayout)
        val updateButton = view.findViewById<Button>(R.id.updateBtn)
        val labelInput = view.findViewById<TextInputEditText>(R.id.labelInput)
        labelInput.setText(transaction.label)
        val amountInput = view.findViewById<TextInputEditText>(R.id.amountInput)
        amountInput.setText(transaction.amount.toString())
        val descriptionInput = view.findViewById<TextInputEditText>(R.id.descriptionInput)
        descriptionInput.setText(transaction.description)

        closeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_homeFragment)
        }

        updateButton.setOnClickListener {
            val label = labelInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()

            if (label.isEmpty()) {
                labelLayout.error = "Please enter a valid label"
            }
            else if (amount == null) {
                amountLayout.error = "Please enter a valid amount"
            }
            else {
                val transaction = Transaction(transaction.id, label, amount, description)
                update(transaction)
                findNavController().navigate(R.id.action_detailsFragment_to_homeFragment)
            }
        }
        return view
    }

    private fun setUp() {
        database = context?.let {
            Room.databaseBuilder(it, TransactionsDatabase::class.java, "transactions")
                .allowMainThreadQueries()
                .build()
        }!!
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchTransaction(id: Long) {
        GlobalScope.apply {
            transaction = database.transactionDatabaseDao()
                .getTransactionById(id)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun update(transaction: Transaction) {
        GlobalScope.apply {
            database.transactionDatabaseDao().update(transaction)
        }
    }
}