package com.example.bookshelf.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bookshelf.data.Book
import com.example.bookshelf.data.User
import com.example.bookshelf.data.typeConverters.TypeConverter
import com.example.bookshelf.db.dao.BookDao
import com.example.bookshelf.db.dao.UserDao

@Database(entities = [User::class, Book::class], version = 1)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
}