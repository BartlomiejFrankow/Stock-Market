package com.example.network.mapper

import com.example.domain.common.Constants.DATE_TIME_PATTERN
import com.example.domain.model.IntraDayInfo
import com.example.network.remote.dto.IntraDayInfoDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun IntraDayInfoDto.toIntraDayInfo(): IntraDayInfo {
    val localDateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.getDefault()))
    return IntraDayInfo(date = localDateTime, close = close)
}
