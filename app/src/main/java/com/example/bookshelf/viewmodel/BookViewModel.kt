package com.example.bookshelf.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.data.Book
import com.example.bookshelf.data.Location
import com.example.bookshelf.data.User
import com.example.bookshelf.repository.BookShelfRepository
import com.example.bookshelf.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookShelfRepository, private val sessionManager: SessionManager
) : ViewModel() {

    private val _location = MutableStateFlow(emptyList<Location>())
    val location = _location.asStateFlow()

    private val _signUp = MutableStateFlow<Boolean?>(null)
    val signUp = _signUp.asStateFlow()

    private val _loginUser = MutableStateFlow<Pair<Boolean, String>?>(null)
    val loginUser = _loginUser.asStateFlow()

    private val _logoutState = MutableStateFlow<Boolean?>(null)
    val logout = _logoutState.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    init {
        viewModelScope.launch {
            val response = repository.getCountyList()
            _location.value = response
        }

        if (checkUserSession()) {
            getBookList()
        }
    }

    fun signUpUser(user: User) {
        viewModelScope.launch {
            try {
                repository.signUpUser(user.email, user.password, user.location)
                _signUp.value = true
                getBookList()
                sessionManager.saveUserSession(email = user.email)

            } catch (e: Exception) {
                _signUp.value = false
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val isValidLogin = repository.loginUser(email, password)
                if (isValidLogin) {
                    sessionManager.saveUserSession(email)
                    _loginUser.value = Pair(true, "Logged in Successfully")
                    getBookList()
                } else {
                    _loginUser.value = Pair(false, "Invalid Credentials")
                }
            } catch (e: Exception) {
                _loginUser.value = Pair(false, "Something Went wrong while logging in")
            }
        }
    }

    fun checkUserSession(): Boolean {
        return sessionManager.getUserSession() != null
    }

    fun logout() {
        sessionManager.clearSession()
        viewModelScope.launch {
            try {
                repository.deleteAllBooks()
            } catch (e: Exception) {
                Log.e("TAG", "logout", e)
            }
        }
        _books.value = emptyList()
        _logoutState.value = true
        _loginUser.value = null
        Log.i("LOGIN_TAG", "logout: ${books.value.size}")
    }

    fun getBookList() {
        viewModelScope.launch {
            try {
                val allBooks = async { repository.fetchAllBooks() }.await()
                Log.i("LOGIN_TAG", "getBookList: ${allBooks.size}")
                _books.value = allBooks
                Log.i("LOGIN_TAG", "getBookList 2: ${books.value.size}")

            } catch (_: Exception) {

            }
        }
    }

    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(book = book)
                _favorites.value = if (_favorites.value.contains(book.id)) {
                    _favorites.value - book.id
                } else {
                    _favorites.value + book.id
                }
            } catch (_: Exception) {

            }
        }
    }
}