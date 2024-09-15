package com.example.bookshelf.di

import com.example.bookshelf.db.dao.BookDao
import com.example.bookshelf.db.dao.UserDao
import com.example.bookshelf.network.BookShelfApiService
import com.example.bookshelf.repository.BookShelfRepository
import com.example.bookshelf.repository.BookShelfRepositoryImpl
import com.example.bookshelf.util.BookShelfConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit
            .Builder()
            .baseUrl(BookShelfConstants.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): BookShelfApiService = retrofit.create(BookShelfApiService::class.java)

    @Provides
    @Singleton
    fun provideRepository(
        api: BookShelfApiService,
        userDao: UserDao,
        bookDao: BookDao,
    ): BookShelfRepository = BookShelfRepositoryImpl(api, userDao, bookDao)
}