package com.example.bookshelf.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(BookShelfConstants.SHARED_PREF_KEY, Context.MODE_PRIVATE)

    fun saveUserSession(email: String) {
        sharedPreferences.edit().putString(BookShelfConstants.USER_EMAIL_KEY, email).apply()
    }

    fun getUserSession(): String? {
        return sharedPreferences.getString(BookShelfConstants.USER_EMAIL_KEY, null)
    }

    fun clearSession() {
        sharedPreferences.edit().remove(BookShelfConstants.USER_EMAIL_KEY).apply()
    }
}