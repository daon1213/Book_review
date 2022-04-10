package com.daon.book_part3_04.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.daon.book_part3_04.model.History

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM history WHERE keyword = :keyword")
    fun delete(keyword: String)
}