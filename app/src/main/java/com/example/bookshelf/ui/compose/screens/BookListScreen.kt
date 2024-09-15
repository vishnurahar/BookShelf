package com.example.bookshelf.ui.compose.screens

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookshelf.data.Book
import com.example.bookshelf.viewmodel.BookViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(viewModel: BookViewModel) {
    val books by viewModel.books.collectAsState()
    val favorite by viewModel.favorites.collectAsState()

    // Grouping years and books in a sorted map
    val groupedBooks = remember(books) {
        books.groupBy { book ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = book.publishedChapterDate * 1000
            }

            val year = calendar.get(Calendar.YEAR)
            year
        }.toSortedMap(reverseOrder())
    }

    val years = groupedBooks.keys.toList() // years which have a at least 1 book
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var currentYearIndex by remember { mutableIntStateOf(0) }

    // Launched effect will check while user scroll if we need to switch tha tab or not
    LaunchedEffect(lazyListState.isScrollInProgress) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { visibleItemIndex ->
                if (visibleItemIndex >= 0 && visibleItemIndex < books.size) {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = books[visibleItemIndex].publishedChapterDate * 1000
                    }
                    val year = calendar.get(Calendar.YEAR)
                    val newYearIndex = years.indexOf(year)
                    if (newYearIndex != -1 && newYearIndex != currentYearIndex) {
                        currentYearIndex = newYearIndex
                    }
                }
            }
    }
    var showMenu by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Books") },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }


                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                viewModel.logout()
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (years.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = currentYearIndex,
                    edgePadding = 16.dp
                ) {
                    years.forEachIndexed { index, year ->
                        Tab(
                            text = { Text(year.toString()) },
                            selected = currentYearIndex == index,
                            onClick = {
                                // scrolling to the item for a respective year
                                coroutineScope.launch {
                                    val yearBooksIndex = books.indexOfFirst {
                                        val calendar = Calendar.getInstance().apply {
                                            timeInMillis = it.publishedChapterDate * 1000
                                        }
                                        calendar.get(Calendar.YEAR) == year
                                    }

                                    if (yearBooksIndex >= 0) {
                                        lazyListState.animateScrollToItem(yearBooksIndex)
                                        currentYearIndex = index
                                    }
                                }
                            }
                        )
                    }
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(books) { book ->
                    BookItem(
                        book = book,
                        isFavorite = book.isFavorite || favorite.contains(book.id),
                        publishedYearDate = book.publishedChapterDate,
                        onFavoriteClick = { viewModel.toggleFavorite(book) }
                    )
                }
            }
        }
    }

}

@Composable
fun BookItem(
    book: Book,
    isFavorite: Boolean,
    publishedYearDate: Long,
    onFavoriteClick: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = publishedYearDate * 1000
    }
    val publishedYear = calendar.get(Calendar.YEAR)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {

        AsyncImage(
            model = book.image,
            contentDescription = "Book Cover",
            modifier = Modifier.size(84.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.fillMaxHeight().weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Score ${book.score}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Published in $publishedYear",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }


        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite) "Unmark Favorite" else "Mark Favorite",
                tint = if (isFavorite) Color.Red else Color.Gray
            )
        }
    }
}
