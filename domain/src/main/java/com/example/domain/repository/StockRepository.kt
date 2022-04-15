package com.example.domain.repository

import com.example.domain.common.RequestResult
import com.example.domain.model.CompanyListing
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(fetchFromRemote: Boolean, query: String): Flow<RequestResult<List<CompanyListing>>>
}
