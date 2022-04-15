package com.example.network.di

import android.app.Application
import androidx.room.Room
import com.example.domain.common.Constants
import com.example.domain.model.CompanyListing
import com.example.network.csv.CSVParser
import com.example.network.csv.CompanyListingParser
import com.example.network.local.StockDatabase
import com.example.network.remote.StockApi
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

}
