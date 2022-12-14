package com.example.expense_track.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val label: String,
    val amount: Double,
    val description: String) : java.io.Serializable
