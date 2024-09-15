package com.example.bookshelf.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Book(
    @PrimaryKey val id: String,
    val image: String,
    val score: Double,
    val popularity: Int,
    val title: String,
    val publishedChapterDate: Long,
    var isFavorite: Boolean = false
)

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val password: String,
    val location: Location
)

@Entity
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val country: String,
    val region: String,
)

