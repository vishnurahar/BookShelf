package com.example.bookshelf.repository

import com.example.bookshelf.data.Book
import com.example.bookshelf.data.Location
import com.example.bookshelf.data.User
import com.example.bookshelf.db.dao.BookDao
import com.example.bookshelf.db.dao.UserDao
import com.example.bookshelf.network.BookShelfApiService

interface BookShelfRepository {
    suspend fun getCountyList(): List<Location>
    suspend fun signUpUser(email: String, password: String, location: Location)
    suspend fun loginUser(email: String, password: String): Boolean
    suspend fun fetchAllBooks() : List<Book>
    suspend fun toggleFavorite(book: Book)
    suspend fun deleteAllBooks()
}

class BookShelfRepositoryImpl(
    private val bookShelfApiService: BookShelfApiService,
    private val userDao: UserDao,
    private val bookDao: BookDao,
) : BookShelfRepository {

    override suspend fun getCountyList(): List<Location> {
        return bookShelfApiService.getCountryList()
    }

    override suspend fun signUpUser(email: String, password: String, location: Location) {
        val user = User(email = email, password = password, location = location)
        try {
            userDao.insertUser(user)
        } catch (_: Exception) {
        }
    }

    override suspend fun loginUser(email: String, password: String): Boolean {
        val user = userDao.getUserByEmail(email)
        return user?.password == password
    }

    override suspend fun fetchAllBooks(): List<Book> {
        val remoteBooks = bookShelfApiService.getBooks()
        val localBooks = bookDao.getAllBooks()
        val updatedBooks = remoteBooks.map { remoteBook ->
            val localBook = localBooks.find { it.id == remoteBook.id }
            if (localBook != null) {
                remoteBook.copy(isFavorite = localBook.isFavorite)
            } else {
                remoteBook
            }
        }
        bookDao.insertBooks(updatedBooks)
        return remoteBooks
    }

    override suspend fun toggleFavorite(book: Book) {
        book.isFavorite = !book.isFavorite
        bookDao.updateBook(book)
    }

    override suspend fun deleteAllBooks() {
        bookDao.clearAllBooks()
    }
}