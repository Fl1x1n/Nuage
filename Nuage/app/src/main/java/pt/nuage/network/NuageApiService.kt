package pt.nuage.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.open-meteo.com/v1/"
private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/v1/"

class NuageApiService {

    interface NuageApiService {
        @GET("forecast")
        suspend fun getHourlyWeather(
            @Query("latitude") latitude: Double?,
            @Query("longitude") longitude: Double?,
            @Query("hourly") hourly: String = "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code"
        ): HourlyData

        @GET("forecast")
        suspend fun getDailyWeather(
            @Query("latitude") latitude: Double?,
            @Query("longitude") longitude: Double?,
            @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min"
        ): DailyData

        @GET("search")
        suspend fun getSearchResults(@Query("name") name: String, @Query("countryCode") countryCode: String = "PT"): SearchData
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