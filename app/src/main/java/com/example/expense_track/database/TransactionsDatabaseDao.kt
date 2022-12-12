package com.example.expense_track.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionsDatabaseDao {

    @Query("SELECT * FROM transactions_table")
    fun getAll() : List<Transaction>

    @Insert
    fun insertAll(vararg transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Update
    fun update(vararg transaction: Transaction)

    @Query("SELECT * FROM transactions_table WHERE id = :key")
    fun getTransactionWithId(key: Long): LiveData<Transaction>
}