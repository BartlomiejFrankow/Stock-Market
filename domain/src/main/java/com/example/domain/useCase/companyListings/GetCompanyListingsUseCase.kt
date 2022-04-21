package com.example.domain.useCase.companyListings

import com.example.domain.common.RequestResult
import com.example.domain.model.CompanyListing
import com.example.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompanyListingsUseCase @Inject constructor(private val repository: StockRepository) {

    suspend operator fun invoke(query: String, fetchFromRemote: Boolean = false): Flow<RequestResult<List<CompanyListing>>> =
        repository.getCompanyListings(query = query, fetchFromRemote = fetchFromRemote)
}
