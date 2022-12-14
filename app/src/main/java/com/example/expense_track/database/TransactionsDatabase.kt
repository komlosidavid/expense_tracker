package com.example.expense_track.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 2)
abstract class TransactionsDatabase : RoomDatabase() {

    abstract fun transactionDatabaseDao() : TransactionsDatabaseDao
}