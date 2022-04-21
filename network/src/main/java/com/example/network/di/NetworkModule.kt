package com.example.network.di

import android.app.Application
import androidx.room.Room
import com.example.domain.common.Constants
import com.example.domain.model.CompanyListing
import com.example.domain.model.IntraDayInfo
import com.example.domain.repository.StockRepository
import com.example.network.csv.CSVParser
import com.example.network.csv.CompanyListingParser
import com.example.network.csv.IntraDayInfoParser
import com.example.network.local.StockDatabase
import com.example.network.remote.StockApi
import com.example.network.repository.StockRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFileApi(): StockApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(StockApi::class.java)

    @Provides
    @Singleton
    fun provideStockDatabase(app: Application): StockDatabase = Room
        .databaseBuilder(app, StockDatabase::class.java, "stock.db")
        .build()

    @Provides
    @Singleton
    fun provideCompanyListingParser(): CSVParser<CompanyListing> = CompanyListingParser()

    @Provides
    @Singleton
    fun provideIntraDayInfoParser(): CSVParser<IntraDayInfo> = IntraDayInfoParser()

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
