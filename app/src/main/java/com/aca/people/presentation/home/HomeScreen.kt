package com.aca.people.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.aca.people.R
import com.aca.people.domain.User
import com.aca.people.domain.model.UserFilterCriteria
import com.aca.people.presentation.home.component.FilterBar
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
    val filterCriteria by viewModel.filterCriteria.collectAsStateWithLifecycle()
    val showExitDialog by viewModel.showExitDialog.collectAsStateWithLifecycle()
    val favoriteUuids by viewModel.favoriteUuids.collectAsStateWithLifecycle()
    val notedUuids by viewModel.notedUuids.collectAsStateWithLifecycle()
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
            Column {
                SearchTopBar(
                    searchText = searchText,
                    onSearchTextChange = viewModel::onSearchTextChange,
                    onClearSearch = viewModel::onClearSearch
                )
                FilterBar(
                    criteria = filterCriteria,
                    onCategorySelected = viewModel::onCategoryFilterSelected,
                    onGenderSelected = viewModel::onGenderFilterSelected
                )
            }
        }
    ) { paddingValues ->
        UserListContent(
            paddingValues = paddingValues,
            userPagingItems = userPagingItems,
            userMarks = HomeUserMarks(favoriteUuids = favoriteUuids, notedUuids = notedUuids),
            filterCriteria = filterCriteria,
            actions = HomeListActions(
                onUserClick = viewModel::onUserClicked,
                onToggleFavorite = viewModel::onToggleFavorite,
                onClearFilters = viewModel::onClearFilters
            )
        )
    }
}

private data class HomeListActions(
    val onUserClick: (User) -> Unit,
    val onToggleFavorite: (User) -> Unit,
    val onClearFilters: () -> Unit
)

@Composable
private fun SearchTopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onClearSearch: () -> Unit
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
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(id = R.string.cd_clear_search)
                        )
                    }
                }
            },
            placeholder = {
                Text(text = stringResource(id = R.string.search_placeholder))
            }
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
    userMarks: HomeUserMarks,
    filterCriteria: UserFilterCriteria,
    actions: HomeListActions
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
                val uuid = user.login?.uuid
                ItemUser(
                    user = user,
                    isFavorite = uuid in userMarks.favoriteUuids,
                    hasNote = uuid in userMarks.notedUuids,
                    onToggleFavorite = { actions.onToggleFavorite(user) },
                    onClick = { actions.onUserClick(user) }
                )
            }
        }

        val showEmptyFiltered = userPagingItems.itemCount == 0 &&
            userPagingItems.loadState.refresh is LoadState.NotLoading &&
            filterCriteria.hasActiveFilters
        if (showEmptyFiltered) {
            item {
                EmptyFilteredState(
                    modifier = Modifier.fillParentMaxSize(),
                    onClearFilters = actions.onClearFilters
                )
            }
        }

        pagingLoadStateItems(userPagingItems)
        item { Spacer(modifier = Modifier.padding(4.dp)) }
    }
}

private fun LazyListScope.pagingLoadStateItems(userPagingItems: LazyPagingItems<User>) {
    when {
        userPagingItems.loadState.refresh is LoadState.Loading -> {
            item { PageLoader(modifier = Modifier.fillParentMaxSize()) }
        }
        userPagingItems.loadState.refresh is LoadState.Error -> {
            val error = userPagingItems.loadState.refresh as LoadState.Error
            item {
                ErrorMessage(
                    modifier = Modifier.fillParentMaxSize(),
                    message = error.error.localizedMessage ?: "Error desconocido",
                    onClickRetry = { userPagingItems.retry() }
                )
            }
        }
        userPagingItems.loadState.append is LoadState.Loading -> {
            item { LoadingNextPageItem(modifier = Modifier) }
        }
        userPagingItems.loadState.append is LoadState.Error -> {
            val error = userPagingItems.loadState.append as LoadState.Error
            item {
                ErrorMessage(
                    modifier = Modifier,
                    message = error.error.localizedMessage ?: "Error al cargar más",
                    onClickRetry = { userPagingItems.retry() }
                )
            }
        }
    }
}

@Composable
private fun EmptyFilteredState(
    modifier: Modifier = Modifier,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.empty_filtered_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onClearFilters) {
            Text(text = stringResource(id = R.string.empty_filtered_action))
        }
    }
}
