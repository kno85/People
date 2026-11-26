package com.aca.people.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.aca.people.domain.User
import com.aca.people.presentation.home.component.ItemUser
import com.aca.people.presentation.util.ErrorMessage
import com.aca.people.presentation.util.LoadingNextPageItem
import com.aca.people.presentation.util.PageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun UserListView(
    viewModel: HomeViewModel,
    onNavigateToDetails: (String) -> Unit,
    onExitApp: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, viewModel.uiEvents) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                viewModel.uiEvents.collect { event ->
                    when (event) {
                        is HomeUiEvent.NavigateToExit -> onExitApp()
                        is HomeUiEvent.NavigateToDetail -> onNavigateToDetails(event.uuid)
                    }
                }
            }
        }
    }

    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val showExitDialog by viewModel.showExitDialog.collectAsStateWithLifecycle()
    val userPagingItems: LazyPagingItems<User> = viewModel.usersState.collectAsLazyPagingItems()

    BackHandler { viewModel.onBackButtonPressed() }

    if (showExitDialog) {
        ExitConfirmationDialog(
            onConfirm = viewModel::onExitConfirmed,
            onDismiss = viewModel::onDismissDialog
        )
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                searchText = searchText,
                onSearchTextChange = viewModel::onSearchTextChange
            )
        }
    ) { paddingValues ->
        UserListContent(
            paddingValues = paddingValues,
            userPagingItems = userPagingItems,
            onUserClick = viewModel::onUserClicked
        )
    }
}

@Composable
private fun SearchTopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Search") }
        )
    }
}

@Composable
private fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Confirmar salida") },
        text = { Text(text = "¿Estás seguro de que quieres salir?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sí")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
private fun UserListContent(
    paddingValues: PaddingValues,
    userPagingItems: LazyPagingItems<User>,
    onUserClick: (User) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(paddingValues)
    ) {
        item { Spacer(modifier = Modifier.padding(4.dp)) }

        items(
            count = userPagingItems.itemCount,
            key = { index -> "${userPagingItems.peek(index)?.login?.uuid.orEmpty()}_$index" }
        ) { index ->
            userPagingItems[index]?.let { user ->
                ItemUser(
                    user = user,
                    onClick = { onUserClick(user) }
                )
            }
        }

        userPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { PageLoader(modifier = Modifier.fillParentMaxSize()) }
                }
                loadState.refresh is LoadState.Error -> {
                    val error = loadState.refresh as LoadState.Error
                    item {
                        ErrorMessage(
                            modifier = Modifier.fillParentMaxSize(),
                            message = error.error.localizedMessage ?: "Error desconocido",
                            onClickRetry = { retry() }
                        )
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item { LoadingNextPageItem(modifier = Modifier) }
                }
                loadState.append is LoadState.Error -> {
                    val error = loadState.append as LoadState.Error
                    item {
                        ErrorMessage(
                            modifier = Modifier,
                            message = error.error.localizedMessage ?: "Error al cargar más",
                            onClickRetry = { retry() }
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.padding(4.dp)) }
    }
}

