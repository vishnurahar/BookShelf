package com.example.bookshelf.ui.compose.navigation

sealed class BookShelfRoute(val route: String) {
    data object SplashScreen : BookShelfRoute("splash")
    data object LoginScreen : BookShelfRoute("login")
    data object SignUpScreen : BookShelfRoute("signup")
    data object BookListScreen : BookShelfRoute("bookList")
}