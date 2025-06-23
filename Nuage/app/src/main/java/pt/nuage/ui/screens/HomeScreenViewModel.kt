package pt.nuage.ui.screens

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pt.nuage.R
import pt.nuage.network.DailyData
import pt.nuage.network.HourlyData
import pt.nuage.network.NuageApiService
import pt.nuage.network.SearchData
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.List
import kotlin.collections.indices
import kotlin.collections.listOf
import kotlin.collections.map
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toMutableList
import kotlin.properties.Delegates

/* ui state to send data to the home screen */
sealed interface NuageUiState {
    data class Success(
        val currentTemperature: Double,
        val currentHumidity: Int,
        val currentWeatherCode: HomeScreenViewModel.WeatherCodeEnum,
        val dailyWeather: DailyData.Daily,
        val dailyWeatherCode: List<HomeScreenViewModel.WeatherCodeEnum>,
        val latitude: Double,
        val longitude: Double
    ) : NuageUiState

    data object Loading : NuageUiState
    data object Error : NuageUiState
}

class HomeScreenViewModel : ViewModel() {
    /* initialize the nuage ui state */
    var nuageUiState = mutableStateOf<NuageUiState>(NuageUiState.Loading)
        private set

    /* private values used to send data to the ui state */
    private lateinit var hourlyWeather: HourlyData.Hourly
    private lateinit var dailyWeather: DailyData.Daily

    private var currentTemperature: Double = 0.0
    private var currentHumidity: Int = 1
    private var dailyWeatherCode: List<WeatherCodeEnum> = listOf(WeatherCodeEnum.CLEAR)

    /* location and geocode */
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var localityName: String

    /* public vars used on the daily screen */
    lateinit var currentTime: String
    var temperatureDaily by Delegates.notNull<Double>()
    var minTemperatureDaily by Delegates.notNull<Double>()
    lateinit var dailyWeatherCodeTime: WeatherCodeEnum
    lateinit var dailyHourlyTemperatureMax: List<Double>
    lateinit var dailyHourlyHumidity: List<Int>
    lateinit var dailyHourlyWeatherCode: List<WeatherCodeEnum>

    /* enum for the weather code */
    enum class WeatherCodeEnum(
        val description: String,
        @DrawableRes val icon: Int,
        @DrawableRes val banner: Int
    ) {
        CLEAR("Clear", R.drawable.weather_sunny, R.drawable.clear),
        CLOUDY("Cloudy", R.drawable.weather_cloudy, R.drawable.cloudy),
        RAINY("Rainy", R.drawable.weather_rainy, R.drawable.rainy),
        SNOWY("Snowy", R.drawable.weather_snowy, R.drawable.snow),
        STORMY("Stormy", R.drawable.weather_lightning, R.drawable.lightning),
        FOGGY("Foggy", R.drawable.weather_fog, R.drawable.foggy),
        `PARTY-CLOUDY`("Partly Cloudy", R.drawable.weather_partly_cloudy, R.drawable.partly_cloudy)
    }

    init {
        getWeatherData()
    }

    //Search Section/code

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onToogleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }

    private val _searchData = MutableStateFlow<List<SearchData.Results>>(emptyList())
    val searchData: StateFlow<List<SearchData.Results>> = _searchData

     fun getSearchResults(input: String) {
        viewModelScope.launch {
            try {
                val searchResponse = NuageApiService.retrofitGeocodingService.getSearchResults(input)
                _searchData.value = searchResponse.results.toList()
            } catch (e: Exception) {
                Log.e("SearchError", "Failed to fetch results: ${e.message}")
                _searchData.value = emptyList()
            }
        }
    }



    private fun getDateMap(dateHourTemplate: String): Int {
        val dateMap = hourlyWeather.time.map {
            val date = it.substringBefore('T')
            val hour = it.substringAfter('T').substringBefore(':')
            "$date$hour:00" // Include "00" as minutes
        }
        return dateMap.indexOf(dateHourTemplate)
    }

    /* calls the api, sets ui state values to those received by the api, and is called when the viewmodel initializes */
    private fun getWeatherData() {
        viewModelScope.launch {
            // check date and time and compare it to the dates retrieved from API to show the current data (temperature, humidity, and weather code)
            val currentDateTime: LocalDateTime = LocalDateTime.now()
            val timeDateFormat: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            val currentTime: String = currentDateTime.format(timeDateFormat)
            val currentDate = currentTime.substringBefore('T')
            val currentHour = currentTime.substringAfter('T').substringBefore(':')
            val currentDateHourTemplate = "$currentDate$currentHour:00"
            try {
                val hourlyWeatherResponse = NuageApiService.retrofitService.getHourlyWeather()
                val dailyWeatherResponse = NuageApiService.retrofitService.getDailyWeather()
                hourlyWeather = hourlyWeatherResponse.hourly
                dailyWeather = dailyWeatherResponse.daily
                currentTemperature = getHourlyTemperature(currentDateHourTemplate)
                currentHumidity = getHourlyHumidity(currentDateHourTemplate)
                val currentWeatherCode = getCurrentHourlyWeatherCode(currentDateHourTemplate)
                latitude = dailyWeatherResponse.latitude
                longitude = dailyWeatherResponse.longitude
                dailyWeatherCode = getDailyWeatherCode()
                nuageUiState.value =
                    NuageUiState.Success(
                        currentTemperature,
                        currentHumidity,
                        currentWeatherCode,
                        dailyWeather,
                        dailyWeatherCode,
                        latitude,
                        longitude
                    )
            } catch (e: IOException) {
                nuageUiState.value = NuageUiState.Error
                Log.d(
                    "API error:",
                    "Couldn't either retrieve data or connect to the api. IOException: $e"
                )
            }
        }
    }

    /* get the hourly temperature, humidity and the weather code from the api and returns the value for the current hour of the device */
    private fun getHourlyTemperature(dateTemplate: String): Double {
        val indexOfMatch = getDateMap(dateTemplate)
        if (indexOfMatch != -1) {
            return hourlyWeather.temperature_2m[indexOfMatch]
        } else {
            Log.e("getHourlyTemperature", "Couldn't get the current temperature")
            return 0.0
        }
    }

    private fun getHourlyHumidity(dateTemplate: String): Int {
        val indexOfMatch = getDateMap(dateTemplate)
        if (indexOfMatch != -1) {
            return hourlyWeather.relative_humidity_2m[indexOfMatch]
        } else {
            Log.e("getHourlyHumidity", "Couldn't get the current humidity")
            return 0
        }
    }

    private fun getCurrentHourlyWeatherCode(dateTemplate: String): WeatherCodeEnum {
        val indexOfMatch = getDateMap(dateTemplate)
        if (indexOfMatch != 1) {
            return when (hourlyWeather.weather_code[indexOfMatch]) {
                0 -> WeatherCodeEnum.CLEAR
                1, 2 -> WeatherCodeEnum.`PARTY-CLOUDY`
                3 -> WeatherCodeEnum.CLOUDY
                51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> WeatherCodeEnum.RAINY
                45, 48 -> WeatherCodeEnum.FOGGY
                71, 72, 75, 77, 85, 86 -> WeatherCodeEnum.SNOWY
                95, 96, 99 -> WeatherCodeEnum.STORMY
                else -> WeatherCodeEnum.CLEAR
            }
        } else {
            Log.e("getHourlyWeatherCode", "Couldn't get weather code for hourly weather")
            return WeatherCodeEnum.CLEAR
        }
    }

    /* makes a weather code list based on the api response and returns the value  */
    private fun getDailyWeatherCode(): List<WeatherCodeEnum> {
        val weatherCodeMap = mutableMapOf<Int, WeatherCodeEnum>()
        for (code in dailyWeather.weather_code) {
            val mappedCode = when (code) {
                0 -> WeatherCodeEnum.CLEAR
                in listOf(1, 2) -> WeatherCodeEnum.`PARTY-CLOUDY`
                3 -> WeatherCodeEnum.CLOUDY
                in listOf(
                    51,
                    53,
                    55,
                    56,
                    57,
                    61,
                    63,
                    65,
                    66,
                    67,
                    80,
                    81,
                    82
                ) -> WeatherCodeEnum.RAINY

                in listOf(45, 48) -> WeatherCodeEnum.FOGGY
                in listOf(71, 72, 75, 77, 85, 86) -> WeatherCodeEnum.SNOWY
                in listOf(95, 96, 99) -> WeatherCodeEnum.STORMY
                else -> WeatherCodeEnum.CLEAR
            }
            weatherCodeMap[code] = mappedCode
        }
        val mappedWeatherCodes = mutableListOf<WeatherCodeEnum>()
        for (code in dailyWeather.weather_code) {
            val mappedCode = weatherCodeMap[code] ?: WeatherCodeEnum.CLEAR
            mappedWeatherCodes.add(mappedCode)
        }
        return mappedWeatherCodes
    }

    /* code for returning daily data for the daily screen */
    fun executeDailyScreenMapping(time: String) {
        currentTime = time
        val currentTimeIndex = getDailyHourlyTimeIndex(time)
        Log.d("Index with formatting", "$currentTimeIndex | ${currentTimeIndex + 24}")
        temperatureDaily = getDailyTemperature(getDailyCurrentIndex(time))
        minTemperatureDaily = getDailyMinimumTemperature(getDailyCurrentIndex(time))
        dailyWeatherCodeTime = getDailyWeatherCodeTime(getDailyCurrentIndex(time))
        dailyHourlyTemperatureMax =
            hourlyWeather.temperature_2m.subList(currentTimeIndex, currentTimeIndex + 24)
        dailyHourlyHumidity =
            hourlyWeather.relative_humidity_2m.subList(currentTimeIndex, currentTimeIndex + 24)
        dailyHourlyWeatherCode = getDailyHourlyWeatherCode(currentTimeIndex)
    }

    /* get the index of the chosen date from the card and the API*/
    private fun getDailyCurrentIndex(currentDate: String): Int {
        for (index in dailyWeather.time.indices) {
            if (currentDate == dailyWeather.time[index]) {
                return index
            }
        }
        return -1
    }

    /* gets the current temperature for the day based on the given index */
    private fun getDailyTemperature(currentIndex: Int): Double {
        return dailyWeather.temperature_2m_max[currentIndex]
    }

    private fun getDailyMinimumTemperature(currentIndex: Int): Double {
        return dailyWeather.temperature_2m_min[currentIndex]
    }

    private fun getDailyWeatherCodeTime(currentIndex: Int): WeatherCodeEnum {
        val weatherCodeMap = mutableMapOf<Int, WeatherCodeEnum>()

        for (code in dailyWeather.weather_code) {
            val mappedCode = when (code) {
                0 -> WeatherCodeEnum.CLEAR
                in listOf(1, 2) -> WeatherCodeEnum.`PARTY-CLOUDY`
                3 -> WeatherCodeEnum.CLOUDY
                in listOf(
                    51,
                    53,
                    55,
                    56,
                    57,
                    61,
                    63,
                    65,
                    66,
                    67,
                    80,
                    81,
                    82
                ) -> WeatherCodeEnum.RAINY

                in listOf(45, 48) -> WeatherCodeEnum.FOGGY
                in listOf(71, 72, 75, 77, 85, 86) -> WeatherCodeEnum.SNOWY
                in listOf(95, 96, 99) -> WeatherCodeEnum.STORMY
                else -> WeatherCodeEnum.CLEAR
            }
            weatherCodeMap[code] = mappedCode
        }

        val mappedWeatherCodes = mutableListOf<WeatherCodeEnum>()

        for (code in dailyWeather.weather_code) {
            val mappedCode = weatherCodeMap[code] ?: WeatherCodeEnum.CLEAR
            mappedWeatherCodes.add(mappedCode)
        }
        return mappedWeatherCodes[currentIndex]
    }

    /* get the first match of the chosen time from the card */
    private fun getDailyHourlyTimeIndex(time: String): Int {
        val chosenDateWithoutTime = time.substringBefore("T")
        for (i in hourlyWeather.time.indices) {
            val dateWithoutTime = hourlyWeather.time[i].substringBefore("T")
            if (dateWithoutTime == chosenDateWithoutTime) {
                Log.d("Current chosen date", chosenDateWithoutTime)
                Log.d("Current index", i.toString())
                Log.d("Current time with match index", hourlyWeather.time[i])
                return i
            }
        }
        return -1
    }

    private fun getDailyHourlyWeatherCode(index: Int): List<WeatherCodeEnum> {
        val weatherCodeMap = mutableMapOf<Int, WeatherCodeEnum>()
        for (code in hourlyWeather.weather_code.subList(index, index + 24)) {
            val mappedCode = when (code) {
                0 -> WeatherCodeEnum.CLEAR
                in listOf(1, 2) -> WeatherCodeEnum.`PARTY-CLOUDY`
                3 -> WeatherCodeEnum.CLOUDY
                in listOf(
                    51,
                    53,
                    55,
                    56,
                    57,
                    61,
                    63,
                    65,
                    66,
                    67,
                    80,
                    81,
                    82
                ) -> WeatherCodeEnum.RAINY

                in listOf(45, 48) -> WeatherCodeEnum.FOGGY
                in listOf(71, 72, 75, 77, 85, 86) -> WeatherCodeEnum.SNOWY
                in listOf(95, 96, 99) -> WeatherCodeEnum.STORMY
                else -> WeatherCodeEnum.CLEAR
            }
            weatherCodeMap[code] = mappedCode
        }
        val mappedWeatherCodes = mutableListOf<WeatherCodeEnum>()
        for (code in hourlyWeather.weather_code.subList(index, index + 24)) {
            val mappedCode = weatherCodeMap[code] ?: WeatherCodeEnum.CLEAR
            mappedWeatherCodes.add(mappedCode)
        }
        return mappedWeatherCodes
    }
}


