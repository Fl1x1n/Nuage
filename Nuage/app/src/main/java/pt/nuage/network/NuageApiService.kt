package pt.nuage.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.open-meteo.com/v1/"
private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/v1/"
const val latitude: Double = 40.1403
const val longitude: Double = -7.5014

class NuageApiService {

    interface NuageApiService {

        @GET("forecast?latitude=${latitude}&longitude=${longitude}&hourly=temperature_2m,relative_humidity_2m,weather_code")
        suspend fun getHourlyWeather(): HourlyData

        @GET("forecast?latitude=${latitude}&longitude=${longitude}&daily=weather_code,temperature_2m_max,temperature_2m_min")
        suspend fun getDailyWeather(): DailyData

        @GET("search?")
        suspend fun getSearchResults(@Query("name") name: String): SearchData
    }

    companion object {
        val retrofitService: NuageApiService by lazy {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .baseUrl(BASE_URL)
                .build()
            retrofit.create(NuageApiService::class.java)
        }
        val retrofitGeocodingService: NuageApiService by lazy {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .baseUrl(GEOCODING_BASE_URL)
                .build()
            retrofit.create(NuageApiService::class.java)
        }
    }
}