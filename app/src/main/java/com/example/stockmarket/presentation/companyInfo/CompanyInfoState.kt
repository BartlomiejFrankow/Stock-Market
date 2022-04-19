package com.example.stockmarket.presentation.companyInfo

import com.example.domain.model.CompanyInfo
import com.example.domain.model.IntraDayInfo

data class CompanyInfoState(
    val stockInfoList: List<IntraDayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = true,
    val error: String? = ""
)
