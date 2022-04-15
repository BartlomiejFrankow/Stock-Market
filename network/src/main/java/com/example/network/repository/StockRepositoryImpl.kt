package com.example.network.repository

import com.example.domain.common.RequestResult
import com.example.domain.model.CompanyListing
import com.example.domain.repository.StockRepository
import com.example.network.csv.CSVParser
import com.example.network.local.StockDatabase
import com.example.network.mapper.toCompanyListing
import com.example.network.mapper.toCompanyListingEntity
import com.example.network.remote.StockApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val parser: CSVParser<CompanyListing>
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(fetchFromRemote: Boolean, query: String): Flow<RequestResult<List<CompanyListing>>> {
        return flow {
            emit(RequestResult.Loading(true))

            val localListings = dao.searchCompanyListing(query)
            emit(RequestResult.Success(body = localListings.map { it.toCompanyListing() }))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldLoadFromCache = !isDbEmpty && !fetchFromRemote

            if (shouldLoadFromCache) {
                emit(RequestResult.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                parser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(RequestResult.IOException)
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(RequestResult.HttpException)
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() })

                emit(RequestResult.Success(getAllDataFromDb()))
                emit(RequestResult.Loading(false))
            }
        }
    }

    private suspend fun getAllDataFromDb() = dao.searchCompanyListing("").map { it.toCompanyListing() }
}
