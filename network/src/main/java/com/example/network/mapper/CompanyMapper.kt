package com.example.network.mapper

import com.example.domain.model.CompanyListing
import com.example.network.local.CompanyListingEntity

fun CompanyListingEntity.toCompanyListing() = CompanyListing(
    name = name,
    symbol = symbol,
    exchange = exchange
)

fun CompanyListing.toCompanyListingEntity() = CompanyListingEntity(
    name = name,
    symbol = symbol,
    exchange = exchange
)
