package com.example.network.repository

import com.example.domain.common.RequestResult
import com.example.domain.model.CompanyInfo
import com.example.domain.model.CompanyListing
import com.example.domain.model.IntraDayInfo
import com.example.domain.repository.StockRepository
import com.example.network.csv.CSVParser
import com.example.network.local.StockDatabase
import com.example.network.mapper.toCompanyInfo
import com.example.network.mapper.toCompanyListing
import com.example.network.mapper.toCompanyListingEntity
import com.example.network.remote.StockApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingParser: CSVParser<CompanyListing>,
    private val intraDayParser: CSVParser<IntraDayInfo>
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(fetchFromRemote: Boolean, query: String): Flow<RequestResult<List<CompanyListing>>> {
        return flow {
            emit(RequestResult.Loading(true))

            val shouldLoadOnlyFromDatabase = getCompaniesFromDatabase(query, fetchFromRemote)

            if (shouldLoadOnlyFromDatabase) return@flow

            getRemoteListings()?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() })

                emit(RequestResult.Success(getAllDataFromDb()))
                emit(RequestResult.Loading(false))
            }
        }
    }

    private suspend fun FlowCollector<RequestResult<List<CompanyListing>>>.getRemoteListings(): List<CompanyListing>? =
        try {
            val response = api.getListingsCSV()
            companyListingParser.parse(response.byteStream())
        } catch (e: IOException) {
            e.printStackTrace()
            emit(RequestResult.IOException)
            null
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(RequestResult.HttpException)
            null
        }

    private suspend fun FlowCollector<RequestResult<List<CompanyListing>>>.getCompaniesFromDatabase(
        query: String,
        fetchFromRemote: Boolean
    ): Boolean {
        val localListings = dao.searchCompanyListing(query)
        emit(RequestResult.Success(body = localListings.map { it.toCompanyListing() }))

        val isDbEmpty = localListings.isEmpty() && query.isBlank()

        if (!isDbEmpty && !fetchFromRemote) {
            emit(RequestResult.Loading(false))
            return true
        }
        return false
    }

    private suspend fun getAllDataFromDb() = dao.searchCompanyListing("").map { it.toCompanyListing() }

    override suspend fun getIntraDayInfo(symbol: String): RequestResult<List<IntraDayInfo>> =
        try {
            val response = api.getIntraDayInfoCSV(symbol)
            val results = intraDayParser.parse(response.byteStream())
            RequestResult.Success(results)
        } catch (e: IOException) {
            e.printStackTrace()
            RequestResult.IOException
        } catch (e: HttpException) {
            e.printStackTrace()
            RequestResult.HttpException
        }


    override suspend fun getCompanyInfo(symbol: String): RequestResult<CompanyInfo> =
        try {
            val result = api.getCompanyInfo(symbol).toCompanyInfo()
            RequestResult.Success(result)
        } catch (e: IOException) {
            e.printStackTrace()
            RequestResult.IOException
        } catch (e: HttpException) {
            e.printStackTrace()
            RequestResult.HttpException
        }

}
