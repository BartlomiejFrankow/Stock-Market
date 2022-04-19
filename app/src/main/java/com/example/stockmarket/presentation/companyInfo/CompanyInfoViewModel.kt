package com.example.stockmarket.presentation.companyInfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.RequestResult
import com.example.domain.useCase.companyInfo.GetCompanyInfoUseCase
import com.example.domain.useCase.companyInfo.GetIntraDayInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch

            val companyInfoResult = async { getCompanyInfoUseCase(symbol) }
            val intraDayResult = async { getIntraDayInfoUseCase(symbol) }

            state = when (val result = companyInfoResult.await()) {
                RequestResult.HttpException -> state.copy(error = "HTTP exception error", isLoading = false)
                RequestResult.IOException -> state.copy(error = "IO exception error", isLoading = false)
                is RequestResult.Success -> state.copy(company = result.body, isLoading = false)
                is RequestResult.Loading -> state.copy(isLoading = result.isLoading)
            }

            state = when (val result = intraDayResult.await()) {
                RequestResult.HttpException -> state.copy(error = "HTTP exception error", isLoading = false)
                RequestResult.IOException -> state.copy(error = "IO exception error", isLoading = false)
                is RequestResult.Success -> state.copy(stockInfoList = result.body, isLoading = false)
                is RequestResult.Loading -> state.copy(isLoading = result.isLoading)
            }
        }
    }
}
