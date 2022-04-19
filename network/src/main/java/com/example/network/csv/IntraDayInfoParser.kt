package com.example.network.csv

import com.example.domain.model.IntraDayInfo
import com.example.network.mapper.toIntraDayInfo
import com.example.network.remote.dto.IntraDayInfoDto
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import javax.inject.Inject

class IntraDayInfoParser @Inject constructor() : CSVParser<IntraDayInfo> {

    override suspend fun parse(stream: InputStream): List<IntraDayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))

        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    IntraDayInfoDto(
                        timestamp = line.getOrNull(0) ?: return@mapNotNull null,
                        close = (line.getOrNull(4) ?: return@mapNotNull null).toDouble(),
                    ).toIntraDayInfo()
                }
                .filter {
                    it.date.dayOfMonth == LocalDateTime.now().minusDays(1).dayOfMonth
                }
                .sortedBy {
                    it.date.hour
                }
                .also {
                    csvReader.close()
                }
        }
    }
}
