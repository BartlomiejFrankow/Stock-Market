package com.example.stockmarket.presentation.companyInfo

import com.example.domain.model.CompanyInfo
import com.example.domain.model.IntraDayInfo
import com.example.stockmarket.util.UiText

data class CompanyInfoState(
    val stockInfoList: List<IntraDayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = true,
    val error: UiText? = null
)
