package com.example.bookshelf.network

import com.example.bookshelf.data.Book
import com.example.bookshelf.data.Location
import retrofit2.http.GET

interface BookShelfApiService {
    @GET("/b/IU1K")
    suspend fun getCountryList(): List<Location>

    @GET("/b/CNGI")
    suspend fun getBooks(): List<Book>
}