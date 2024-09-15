package com.example.bookshelf.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bookshelf.data.Book

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBooks(books: List<Book>)

    @Query("SELECT * FROM book ORDER BY publishedChapterDate DESC")
    suspend fun getAllBooks(): List<Book>

    @Update
    suspend fun updateBook(book: Book)

    @Query("DELETE FROM book")
    suspend fun clearAllBooks()
}