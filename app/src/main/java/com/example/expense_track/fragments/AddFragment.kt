package com.example.expense_track.fragments

import android.os.Bundle
import android.provider.SyncStateContract.Helpers.insert
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.expense_track.R
import com.example.expense_track.database.Transaction
import com.example.expense_track.database.TransactionsDatabase
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddFragment : Fragment() {

    private lateinit var database : TransactionsDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val button = view.findViewById<ImageButton>(R.id.closebtn)
        button.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }

        val labelInput = view.findViewById<TextInputEditText>(R.id.labelInput)
        val labelLayout = view.findViewById<TextInputLayout>(R.id.labelLayout)
        val amountInput = view.findViewById<TextInputEditText>(R.id.amountInput)
        val amountLayout = view.findViewById<TextInputLayout>(R.id.amountLayout)
        val addTransactionButton = view.findViewById<Button>(R.id.addtransactionbtn)
        val descriptionInput = view.findViewById<TextInputEditText>(R.id.descriptionInput)

        labelInput.addTextChangedListener {
            if (it != null && it.isNotEmpty()) {
                labelLayout.error = null
            }
        }
        amountInput.addTextChangedListener {
            if (it != null && it.isNotEmpty()) {
                amountLayout.error = null
            }
        }
        addTransactionButton.setOnClickListener {
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
                val transaction = Transaction(0, label, amount, description)
                insert(transaction)
            }
        }

        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun insert(transaction: Transaction) {
        database = Room.databaseBuilder(requireContext(), TransactionsDatabase::class.java, "transactions")
            .build()
        GlobalScope.launch {
            database.transactionsDatabaseDao.insertAll(transaction)
        }
    }
}