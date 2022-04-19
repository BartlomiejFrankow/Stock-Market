package com.example.stockmarket.di

import com.example.domain.repository.StockRepository
import com.example.network.csv.CompanyListingParser
import com.example.network.csv.IntraDayInfoParser
import com.example.network.local.StockDatabase
import com.example.network.remote.StockApi
import com.example.network.repository.StockRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideStockRepository(
        api: StockApi,
        db: StockDatabase,
        companyListingParser: CompanyListingParser,
        intraDayInfoParser: IntraDayInfoParser
    ): StockRepository = StockRepositoryImpl(
        api = api,
        db = db,
        companyListingParser = companyListingParser,
        intraDayParser = intraDayInfoParser
    )
}
