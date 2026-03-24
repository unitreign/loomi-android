package com.reign.loomi.data.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.reign.loomi.data.model.RadioStation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

data class RadioStationDto(
    val stationuuid: String?,
    val name: String?,
    val url_resolved: String?,
)

class RadioBrowserApi(
    private val client: OkHttpClient = OkHttpClient(),
    private val gson: Gson = Gson(),
) {
    private val stationListType = object : TypeToken<List<RadioStationDto>>() {}.type

    suspend fun fetchStationsByTag(serverBaseUrl: String, tag: String): List<RadioStation> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$serverBaseUrl/json/stations/bytag/$tag")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("HTTP ${response.code} for tag $tag")
                }

                val body = response.body?.string().orEmpty()
                val dtoList: List<RadioStationDto> = gson.fromJson(body, stationListType) ?: emptyList()

                dtoList.mapNotNull { dto ->
                    val uuid = dto.stationuuid?.trim().orEmpty()
                    val name = dto.name?.trim().orEmpty()
                    val streamUrl = dto.url_resolved?.trim().orEmpty()
                    if (uuid.isBlank() || name.isBlank() || streamUrl.isBlank()) {
                        null
                    } else {
                        RadioStation(
                            stationuuid = uuid,
                            name = name,
                            url_resolved = streamUrl,
                        )
                    }
                }
            }
        }
    }
}
