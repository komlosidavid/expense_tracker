package com.example.expense_track.database

import androidx.room.*

@Dao
interface TransactionsDatabaseDao {

    @Query("SELECT * FROM transactions")
    fun getAll() : List<Transaction>

    @Insert
    fun insertAll(vararg transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Update
    fun update(vararg transaction: Transaction)

    @Query("DELETE FROM transactions")
    fun deleteAll()

    @Query("SELECT * FROM transactions t WHERE t.id=:id")
    infix fun getTransactionById(id: Long) : com.example.expense_track.database.Transaction
}