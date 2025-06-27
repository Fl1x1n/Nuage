package pt.nuage.network

import kotlinx.serialization.Serializable


@Serializable
data class HourlyData(
    val latitude: Float,
    val longitude: Float,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Float,
    val hourly_units: HourlyUnits,
    val hourly: Hourly
) {
    @Serializable
    data class HourlyUnits(
        val time: String,
        val temperature_2m: String,
        val apparent_temperature: String,
        val relative_humidity_2m: String,
        val weather_code: String
    )

    @Serializable
    data class Hourly(
        val time: List<String>,
        val temperature_2m: List<Double>,
        val apparent_temperature: List<Double>,
        val relative_humidity_2m: List<Int>,
        val weather_code: List<Int>
    )
}

@Serializable
data class DailyData(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Float,
    val daily_units: DailyUnits,
    val daily: Daily
) {
    @Serializable
    data class DailyUnits(
        val time: String,
        val weather_code: String,
        val temperature_2m_max: String,
        val temperature_2m_min: String
    )

    @Serializable
    data class Daily(
        val time: List<String>,
        val weather_code: List<Int>,
        val temperature_2m_max: List<Double>,
        val temperature_2m_min: List<Double>
    )
}

@Serializable
data class SearchData (
    val results: List<Results>,
    val generationtime_ms: Double
) {
    @Serializable
    class Results (
        val id: Int = 1,
        val name: String = "N/A",
        val latitude: Double = 0.1,
        val longitude: Double = 0.2,
        val elevation: Double = 11.1,
        val feature_code: String = "6230",
        val country_code: String = "pt",
        val admin1_id: Int = 1,
        val admin2_id: Int = 2,
        val admin3_id: Int = 2,
        val timezone: String = "Europe/London",
        val population: Int = 2,
        val country_id: Int = 351,
        val country: String = "Portugal",
        val admin1: String = "disc",
        val admin2: String = "disc",
        val admin3: String = "disc",
    )
}

