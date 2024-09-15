package com.example.bookshelf.ui.compose.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookshelf.data.User
import com.example.bookshelf.ui.compose.screens.BookListScreen
import com.example.bookshelf.ui.compose.screens.LoginScreen
import com.example.bookshelf.ui.compose.screens.SignupScreen
import com.example.bookshelf.ui.compose.screens.SplashScreen
import com.example.bookshelf.viewmodel.BookViewModel
import kotlinx.coroutines.delay

@Composable
fun BookShelfNavigation(bookViewModel: BookViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = BookShelfRoute.SplashScreen.route) {
        composable(route = BookShelfRoute.SplashScreen.route) {
            LaunchedEffect(Unit) {
                delay(2000)
                val isLoggedIn = bookViewModel.checkUserSession()
                if (isLoggedIn) {
                    navController.navigate(BookShelfRoute.BookListScreen.route) {
                        popUpTo(it.id) {inclusive = true}
                    }
                } else {
                    navController.navigate(BookShelfRoute.LoginScreen.route) {
                        popUpTo(it.id) {inclusive = true}
                    }
                }
            }
            SplashScreen()
        }

        composable(BookShelfRoute.LoginScreen.route) {
            val loginState by bookViewModel.loginUser.collectAsState()
            val context = LocalContext.current
            LaunchedEffect(loginState?.first) {
                if (loginState?.first == true) {
                    navController.navigate(route = BookShelfRoute.BookListScreen.route) {
                        popUpTo(popUpToId) {inclusive = false}
                    }
                } else if (loginState?.first == false) {
                    loginState?.second?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            LoginScreen(
                onLoginClick = { email, password ->
                    bookViewModel.loginUser(email, password)
                },
                onSignupClick = {
                    navController.navigate(BookShelfRoute.SignUpScreen.route)
                }
            )
        }

        composable(BookShelfRoute.SignUpScreen.route) {
            val context = LocalContext.current
            val signUpState by bookViewModel.signUp.collectAsState()
            LaunchedEffect(signUpState) {
                if (signUpState == true) {
                    navController.navigate(BookShelfRoute.BookListScreen.route)
                } else if (signUpState == false) {
                    Toast.makeText(
                        context,
                        "Unable to Sign Up! Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            SignupScreen(
                onSignupClick = { email, password, confirmPassword, location ->
                    bookViewModel.signUpUser(
                        user = User (
                            email = email,
                            password = password,
                            location = location
                        )
                    )
                },
                onLoginClick = { navController.popBackStack() },
                viewModel = bookViewModel
            )
        }

        composable(BookShelfRoute.BookListScreen.route) {
            val logoutState by bookViewModel.logout.collectAsState()
            LaunchedEffect(logoutState) {
                if (logoutState == true) {
                    navController.navigate(BookShelfRoute.LoginScreen.route) {
                        popUpTo(popUpToId) {inclusive = false}
                    }
                }
            }

            BookListScreen(bookViewModel)
        }
    }
}