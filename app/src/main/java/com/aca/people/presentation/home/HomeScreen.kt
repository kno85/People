package com.aca.people.presentation.home

import ItemUser
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.aca.people.domain.User
import com.aca.people.presentation.util.ErrorMessage
import com.aca.people.presentation.util.LoadingNextPageItem
import com.aca.people.presentation.util.PageLoader
import com.aca.people.presentation.util.resource.route.AppScreen

@Composable
fun UserListView(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val searchText by viewModel.searchText.collectAsState()
    val userPagingItems: LazyPagingItems<User> = viewModel.usersState.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
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
                            navController.currentBackStackEntry?.savedStateHandle?.set("user", user)
                            navController.navigate(AppScreen.DetailsScreen.route)
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
                                message = error.error.localizedMessage!!,
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
                                message = error.error.localizedMessage!!,
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

@Preview(showBackground = true)
@Composable
fun UserListViewPreview() {
    UserListView(
        navController = rememberNavController()
    )
}
