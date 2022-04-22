package com.example.stockmarket.presentation.companyInfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.Constants.ARG_SYMBOL
import com.example.domain.common.RequestResult
import com.example.domain.useCase.companyInfo.GetCompanyInfoUseCase
import com.example.domain.useCase.companyInfo.GetIntraDayInfoUseCase
import com.example.stockmarket.R
import com.example.stockmarket.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getCompanyInfoUseCase: GetCompanyInfoUseCase,
    private val getIntraDayInfoUseCase: GetIntraDayInfoUseCase
) : ViewModel() {

    var state by mutableStateOf(CompanyInfoState())

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>(ARG_SYMBOL) ?: return@launch

            val companyInfoResult = async { getCompanyInfo(symbol) }
            val intraDayResult = async { getIntraDayInfo(symbol) }

            listOf(companyInfoResult, intraDayResult).awaitAll()
        }
    }

    private fun getCompanyInfo(symbol: String) {
        viewModelScope.launch {
            state = when (val result = getCompanyInfoUseCase(symbol)) {
                RequestResult.HttpException -> state.copy(error = UiText.StringResource(R.string.error_http), isLoading = false)
                RequestResult.IOException -> state.copy(error = UiText.StringResource(R.string.error_io), isLoading = false)
                is RequestResult.Success -> state.copy(company = result.body, isLoading = false)
                is RequestResult.Loading -> state.copy(isLoading = result.isLoading)
            }
        }
    }

    private fun getIntraDayInfo(symbol: String) {
        viewModelScope.launch {
            getIntraDayInfoUseCase(symbol)
            state = when (val result = getIntraDayInfoUseCase(symbol)) {
                RequestResult.HttpException -> state.copy(error = UiText.StringResource(R.string.error_http), isLoading = false)
                RequestResult.IOException -> state.copy(error = UiText.StringResource(R.string.error_io), isLoading = false)
                is RequestResult.Success -> state.copy(stockInfoList = result.body, isLoading = false)
                is RequestResult.Loading -> state.copy(isLoading = result.isLoading)
            }
        }
    }
}
