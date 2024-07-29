package com.aca.people.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.aca.people.R
import com.aca.people.domain.User
import com.aca.people.presentation.home.component.ItemUser
import com.aca.people.presentation.main.MainEvent
import com.aca.people.presentation.main.MainViewModel
import com.aca.people.presentation.util.ErrorMessage
import com.aca.people.presentation.util.LoadingNextPageItem
import com.aca.people.presentation.util.PageLoader
import com.aca.people.presentation.util.resource.route.AppScreen
import com.aca.people.presentation.util.resource.theme.AppTheme
import com.aca.people.utils.AppPreferences
import kotlin.system.exitProcess

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    navController: NavController,
    viewmodel: HomeViewModel = hiltViewModel()
) {

    val searchText by viewmodel.searchText.collectAsState()
    val userPagingItems: LazyPagingItems<User> = viewmodel.usersState.collectAsLazyPagingItems()

    val showExitDialog by viewmodel.showExitDialog.collectAsState()

    BackHandler(enabled = true) {
        if (showExitDialog) {
            // Do nothing if the dialog is already showing
        } else {
            viewmodel.setShowExitDialog(true)
        }
    }

    Column {
        // Replace with your user list
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
                        onValueChange = viewmodel::onSearchTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "Search") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(25.dp)
                        )
                    }


                    IconButton(
                        onClick = {
                            if (mainViewModel.stateApp.theme == AppTheme.Light) {
                                AppPreferences.setTheme(AppTheme.Dark)
                                mainViewModel.onEvent(MainEvent.ThemeChange(AppTheme.Dark))
                            } else {
                                AppPreferences.setTheme(AppTheme.Light)
                                mainViewModel.onEvent(MainEvent.ThemeChange(AppTheme.Light))
                            }
                        }
                    ) {
                        Icon(
                            painter = if (mainViewModel.stateApp.theme == AppTheme.Light)
                                painterResource(id = R.drawable.people_icon)
                            else
                                painterResource(id = R.drawable.people_icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                item { Spacer(modifier = Modifier.padding(4.dp)) }
                items(userPagingItems.itemCount) { index ->
                    ItemUser(
                        itemEntity = userPagingItems[index]!!,
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("user",  userPagingItems[index]!!)
                            navController.navigate(AppScreen.DetailsScreen.route)
                        }
                    )
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
                                    onClickRetry = { retry() })
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
                                    onClickRetry = { retry() })
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.padding(4.dp)) }
            }
        }
    }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { /* Do nothing to make it not cancelable */ },
                title = { Text(text = stringResource(id = R.string.atencion)) },
                text = { Text(text = stringResource(id = R.string.accion_salir)) },
                confirmButton = {
                    Button(onClick = {
                        exitProcess(0)
                    }) { Text(text = stringResource(id = R.string.si)) }
                },
                dismissButton = {
                    Button(onClick = {
                        viewmodel.setShowExitDialog(false)
                    }) { Text(text = stringResource(id = R.string.no)) }
                }
            )
        }
}



