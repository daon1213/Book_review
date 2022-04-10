package com.daon.book_part3_04

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.daon.book_part3_04.dao.HistoryDao
import com.daon.book_part3_04.dao.ReviewDao
import com.daon.book_part3_04.model.History
import com.daon.book_part3_04.model.Review

// Database 가 변경되었을 때마다 version 을 변경해주어야 한다. 잊으면 에러남.
@Database(entities = [History::class, Review::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}

fun getAppDatabase(context: Context) : AppDatabase {
    val migration1to2 = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `Review` (`id` INTEGER,`review` TEXT, " + "PRIMARY KEY(`id`))")
        }
    }

    return Room.databaseBuilder(
        context,
        AppDatabase::class.java, "BookSearchDB"
    ).addMigrations(migration1to2).build()
}