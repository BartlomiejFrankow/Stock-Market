package com.example.domain.repository

import com.example.domain.common.RequestResult
import com.example.domain.model.CompanyInfo
import com.example.domain.model.CompanyListing
import com.example.domain.model.IntraDayInfo
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(fetchFromRemote: Boolean, query: String): Flow<RequestResult<List<CompanyListing>>>

    suspend fun getIntraDayInfo(symbol: String): RequestResult<List<IntraDayInfo>>

    suspend fun getCompanyInfo(symbol: String): RequestResult<CompanyInfo>
}
