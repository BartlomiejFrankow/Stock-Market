package com.example.stockmarket.presentation.companyListings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.RequestResult
import com.example.domain.useCase.companyListings.GetCompanyListingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val getCompanyListingsUseCase: GetCompanyListingsUseCase
) : ViewModel() {

    var state by mutableStateOf(CompanyListingsState())

    private var searchJob: Job? = null

    init {
        getCompanyListings()
    }

    fun onEvent(event: CompanyListingsEvent) {
        when (event) {
            CompanyListingsEvent.Refresh -> {
                getCompanyListings(fetchFromRemote = true)
            }
            is CompanyListingsEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500)
                    getCompanyListings()
                }
            }
        }
    }

    private fun getCompanyListings(
        query: String = state.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            getCompanyListingsUseCase(query, fetchFromRemote)
                .onEach { result ->
                    when (result) {
                        RequestResult.HttpException -> state = state.copy(error = "HTTP exception error", isLoading = false)
                        RequestResult.IOException -> state = state.copy(error = "IO exception error", isLoading = false)
                        is RequestResult.Loading -> state = state.copy(isLoading = result.isLoading)
                        is RequestResult.Success -> result.body.let { listings -> state = state.copy(companies = listings) }
                    }
                }
                .collect()

        }
    }
}
