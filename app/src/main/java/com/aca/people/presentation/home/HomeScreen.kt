package com.aca.people.presentation.home

import ItemUser
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.aca.people.domain.User
import com.aca.people.presentation.util.ErrorMessage
import com.aca.people.presentation.util.LoadingNextPageItem
import com.aca.people.presentation.util.PageLoader

@Composable
fun UserListView(
    // CAMBIO 1: La firma de la función ahora es correcta y consistente.
    viewModel: HomeViewModel,
    onNavigateToDetails: (User) -> Unit,
    onExitApp: () -> Unit
) {
    // CAMBIO 2: Conectamos la acción de salida con el ViewModel.
    // Esto "inyecta" la función para salir en el ViewModel.
    // LaunchedEffect se asegura que esto se haga solo una vez de forma segura.
    LaunchedEffect(onExitApp) {
        viewModel.onExitAction = onExitApp
    }

    val searchText by viewModel.searchText.collectAsState()
    val showExitDialog by viewModel.showExitDialog.collectAsState()
    val userPagingItems: LazyPagingItems<User> = viewModel.usersState.collectAsLazyPagingItems()

    // Intercepta el evento del botón de retroceso del sistema
    BackHandler {
        viewModel.onBackButtonPressed()
    }

    // Muestra el diálogo si el estado es true
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDialog() },
            title = { Text(text = "Confirmar salida") },
            text = { Text(text = "¿Estás seguro de que quieres salir?") },
            confirmButton = {
                TextButton(onClick = { viewModel.onExitConfirmed() }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDialog() }) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        topBar = {
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
                    onValueChange = viewModel::onSearchTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Search") }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item { Spacer(modifier = Modifier.padding(4.dp)) }

            items(userPagingItems.itemCount) { index ->
                userPagingItems[index]?.let { user ->
                    ItemUser(user,
                        onClick = {
                            onNavigateToDetails(user)
                        }
                    )
                }
            }

            userPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { PageLoader(modifier = Modifier.fillParentMaxSize()) }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val error = userPagingItems.loadState.refresh as LoadState.Error
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
                        val error = userPagingItems.loadState.append as LoadState.Error
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
}
