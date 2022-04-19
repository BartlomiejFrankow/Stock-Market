package com.example.domain.useCase.companyInfo

import com.example.domain.repository.StockRepository
import javax.inject.Inject

class GetCompanyInfoUseCase @Inject constructor(private val repository: StockRepository) {
    suspend operator fun invoke(symbol: String) = repository.getCompanyInfo(symbol)
}
