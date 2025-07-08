package pt.nuage.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.nuage.R
import pt.nuage.network.DailyData
import pt.nuage.ui.screens.components.ImageBackdrop
import pt.nuage.ui.screens.components.TemperatureHero
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    nuageUiState: MutableState<NuageUiState>,
    modifier: Modifier,
    onDayClicked: (String) -> Unit
) {
    when (nuageUiState.value) {
        is NuageUiState.Loading -> LoadingScreen(
            message = stringResource(R.string.homeScreenLoadingStateMessage)
        )

        is NuageUiState.Success -> HomeScreenApp(
            modifier = modifier,
            onDayClicked = onDayClicked,
            hourlyTempMin = (nuageUiState.value as NuageUiState.Success).currentMinTemperature,
            hourlyTemperature = (nuageUiState.value as NuageUiState.Success).currentTemperature,
            hourlyWeatherCode = (nuageUiState.value as NuageUiState.Success).currentWeatherCode,
            hourlyHumidity = (nuageUiState.value as NuageUiState.Success).currentHumidity,
            dailyWeather = (nuageUiState.value as NuageUiState.Success).dailyWeather,
            dailyWeatherCode = (nuageUiState.value as NuageUiState.Success).dailyWeatherCode,
            locality = (nuageUiState.value as NuageUiState.Success).locality
        )

        is NuageUiState.Error -> LoadingScreen(
            message = stringResource(R.string.homeScreenErrorStateMessage)
        )
    }
}

@Composable
fun LoadingScreen(message: String) {
    Row {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x00000000)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "A vector of a cloud raining"
            )
            Text(text = message)
        }
    }
}

@Composable
fun HomeScreenApp(
    modifier: Modifier,
    hourlyTemperature: Int,
    hourlyHumidity: Int,
    hourlyTempMin: Int,
    dailyWeather: DailyData.Daily,
    hourlyWeatherCode: HomeScreenViewModel.WeatherCodeEnum,
    dailyWeatherCode: List<HomeScreenViewModel.WeatherCodeEnum>,
    onDayClicked: (String) -> Unit,
    locality: String,
) {
    Column(modifier) {
        TemperatureBanner(
            hourlyTemperature,
            hourlyTempMin,
            hourlyHumidity,
            hourlyWeatherCode,
            locality
        )
        TemperatureList(dailyWeather, dailyWeatherCode, onDayClicked = onDayClicked)
    }
}

@Composable
fun TemperatureBanner(
    temperature: Int,
    tempMin: Int,
    humidity: Int,
    weatherCode: HomeScreenViewModel.WeatherCodeEnum,
    locality: String
) {
    Box {
        ImageBackdrop(
            weatherCode.banner,
            weatherCode.description
        )
        TemperatureHero(
            stringResource(R.string.homeScreenWelcomeGeocode, locality),
            weatherCode.icon,
            weatherCode.description,
            temperature = temperature,
            secondField = stringResource(R.string.dailyScreenMinTemperatureHero, tempMin.toString()),
            R.drawable.thermometer,
            "$humidity %",
            R.drawable.water,
        )
    }
}

@Composable
fun TemperatureList(
    dailyWeather: DailyData.Daily,
    weatherCode: List<HomeScreenViewModel.WeatherCodeEnum>,
    onDayClicked: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.nextDaysHeading),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            items(dailyWeather.time.size) { index ->
                Spacer(modifier = Modifier.height(12.dp))
                DayCard(
                    day = LocalDate.parse(
                        dailyWeather.time[index],
                        dateFormatter
                    ).dayOfWeek.toString().substring(0, 3),
                    date = LocalDate.parse(
                        dailyWeather.time[index],
                        dateFormatter
                    ).dayOfMonth.toString(),
                    maxTemp = stringResource(
                        R.string.dailyScreenMinTemperatureHero,
                        dailyWeather.temperature_2m_max[index].roundToInt().toString()
                    ),
                    minTemp = stringResource(
                        R.string.dailyScreenMinTemperatureHero,
                        dailyWeather.temperature_2m_min[index].roundToInt().toString()
                    ),
                    weatherCodeIcon = weatherCode[index].icon,
                    weatherCodeDescription = stringResource(weatherCode[index].description),
                    onDayCardClicked = {
                        onDayClicked(dailyWeather.time[index])
                    }
                )
            }
        }
    }
}

@Composable
fun DayCard(
    day: String,
    date: String,
    maxTemp: String,
    minTemp: String,
    @DrawableRes weatherCodeIcon: Int,
    weatherCodeDescription: String,
    onDayCardClicked: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(), onClick = onDayCardClicked,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = date,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = maxTemp,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = minTemp,
                        fontSize = 16.sp
                    )

                }
                VerticalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .height(56.dp)
                )
                Image(
                    painter = painterResource(weatherCodeIcon),
                    contentDescription = "$weatherCodeDescription Icon",
                    modifier = Modifier.size(56.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )

            }
        }
    }
}