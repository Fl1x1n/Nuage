package pt.nuage.ui.screens

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.nuage.R
import pt.nuage.ui.screens.components.ImageBackdrop
import pt.nuage.ui.screens.components.TemperatureHero
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayScreen(
    viewModel: HomeScreenViewModel,
    modifier: Modifier
) {
    val currentTime by remember { mutableStateOf(viewModel.currentTime) }
    val dailyTemperature by remember { mutableStateOf(viewModel.temperatureDaily) }
    val dailyMinTemperature by remember { mutableStateOf(viewModel.minTemperatureDaily) }
    val dailyWeatherCode by remember { mutableStateOf(viewModel.dailyWeatherCodeTime) }
    val hourlyTemperatureMax by remember {
        mutableStateOf(viewModel.dailyHourlyTemperatureMax)
    }
    val hourlyHumidity by remember {
        mutableStateOf(viewModel.dailyHourlyHumidity)
    }
    val hourlyWeatherCode by remember {
        mutableStateOf(viewModel.dailyHourlyWeatherCode)
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = modifier
        ) {
            TemperatureBanner(
                time = currentTime,
                temperature = dailyTemperature,
                minTemperature = dailyMinTemperature,
                weatherCode = dailyWeatherCode,
                viewModel.localityName
            )
            TemperatureList(
                temperatureMax = hourlyTemperatureMax,
                humidity = hourlyHumidity,
                weatherCode = hourlyWeatherCode
            )
        }
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TemperatureBanner(
    time: String,
    temperature: Double,
    minTemperature: Double,
    weatherCode: HomeScreenViewModel.WeatherCodeEnum,
    locality: String
) {
    Box {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        ImageBackdrop(weatherCode.banner, weatherCode.description)
        TemperatureHero(
            heading = stringResource(
                R.string.dailyScreenWelcomeGeocode,
                LocalDate.parse(time, dateFormatter).format(dateFormat).toString(),
                locality
            ),
            icon = weatherCode.icon,
            description = weatherCode.description,
            temperature = temperature,
            secondField = stringResource(R.string.dailyScreenMinTemperatureHero, minTemperature),
            secondFieldIcon = R.drawable.thermometer_minus
        )
    }
}

@Composable
fun TemperatureList(
    temperatureMax: List<Double>,
    humidity: List<Int>,
    weatherCode: List<HomeScreenViewModel.WeatherCodeEnum>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.dayScreenProvisionHeading),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn {
            items(24) { index ->
                Spacer(modifier = Modifier.height(12.dp))
                DayCard(
                    "${index}:00",
                    temperatureMax[index].toString(),
                    "${humidity[index]} %",
                    weatherCode[index].icon,
                    weatherCode[index].description
                )
            }
        }

    }

}

@Composable
fun DayCard(
    hour: String,
    temperatureMax: String,
    humidity: String,
    @DrawableRes weatherCodeIcon: Int,
    weatherCodeDescription: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                    text = hour,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge

                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = temperatureMax,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = humidity,
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
