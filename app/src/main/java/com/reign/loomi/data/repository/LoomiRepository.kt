package com.reign.loomi.data.repository

import com.reign.loomi.data.local.LoomiPreferencesDataSource
import com.reign.loomi.data.model.LoomiConfig
import com.reign.loomi.data.model.PersistedSnapshot
import com.reign.loomi.data.model.RadioStation
import com.reign.loomi.data.network.RadioBrowserApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class StationScanResult(
    val lofiStations: List<RadioStation>,
    val otherStations: List<RadioStation>,
)

class LoomiRepository(
    private val localDataSource: LoomiPreferencesDataSource,
    private val api: RadioBrowserApi,
) {
    suspend fun loadSnapshot(): PersistedSnapshot = localDataSource.loadSnapshot()

    suspend fun saveSnapshot(snapshot: PersistedSnapshot) {
        localDataSource.saveSnapshot(snapshot)
    }

    suspend fun scanStations(): Result<StationScanResult> {
        var lastError: Throwable? = null

        for (server in LoomiConfig.apiServers) {
            try {
                val result = scanFromServer(server)
                return Result.success(result)
            } catch (error: Throwable) {
                lastError = error
            }
        }

        return Result.failure(lastError ?: IllegalStateException("All API servers failed"))
    }

    private suspend fun scanFromServer(server: String): StationScanResult = coroutineScope {
        val lofiDeferred = async { api.fetchStationsByTag(server, "lofi") }
        val synthwaveDeferred = async { api.fetchStationsByTag(server, "synthwave") }
        val chilloutDeferred = async { api.fetchStationsByTag(server, "chillout") }

        val lofi = deduplicateStations(lofiDeferred.await()).take(20)
        val synthwave = deduplicateStations(synthwaveDeferred.await()).take(10)
        val chillout = deduplicateStations(chilloutDeferred.await()).take(10)

        val lofiIds = lofi.map { it.stationuuid }.toSet()
        val other = deduplicateStations(synthwave + chillout)
            .filterNot { it.stationuuid in lofiIds }

        if (lofi.isEmpty() && other.isEmpty()) {
            throw IllegalStateException("No stations returned from API")
        }

        StationScanResult(lofiStations = lofi, otherStations = other)
    }

    private fun deduplicateStations(stations: List<RadioStation>): List<RadioStation> {
        val seenNames = hashSetOf<String>()
        val seenUrls = hashSetOf<String>()

        return stations.filter { station ->
            val normalizedName = station.name.lowercase().trim()
            val normalizedUrl = station.url_resolved.lowercase().trim()
            if (normalizedName in seenNames || normalizedUrl in seenUrls) {
                false
            } else {
                seenNames.add(normalizedName)
                seenUrls.add(normalizedUrl)
                true
            }
        }
    }
}
