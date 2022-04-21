package com.example.stockmarket.presentation.companyListings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stockmarket.R
import com.example.stockmarket.presentation.companyListings.CompanyListingsEvent.OnSearchQueryChange
import com.example.stockmarket.presentation.companyListings.CompanyListingsEvent.Refresh
import com.example.stockmarket.presentation.destinations.CompanyInfoScreenDestination
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(start = true)
fun CompanyListingsScreen(
    navigator: DestinationsNavigator,
    viewModel: CompanyListingsViewModel = hiltViewModel()
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onEvent(OnSearchQueryChange(it)) },
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            placeholder = { Text(text = stringResource(R.string.search)) },
            maxLines = 1,
            singleLine = true
        )

        Box(
            modifier = Modifier
                .height(4.dp)
                .fillMaxWidth()
        ) {
            if (state.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(Refresh) }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.companies.size) { index ->
                    CompanyItem(
                        company = state.companies[index],
                        modifier = Modifier.clickable {
                            navigator.navigate(CompanyInfoScreenDestination(state.companies[index].symbol))
                        }
                    )
                    if (index < state.companies.size) Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}
