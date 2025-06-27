package pt.nuage.ui.screens.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TemperatureHero(
    heading: String,
    @DrawableRes icon: Int,
    @StringRes description: Int,
    temperature: Int,
    secondField: String,
    @DrawableRes secondFieldIcon: Int,
    thirdField: String = "N/A",
    @DrawableRes thirdFieldIcon: Int = 0,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 48.dp, start = 16.dp, end = 16.dp),

        ) {
        Text(
            text = heading,
            fontSize = 32.sp,
            style = TextStyle(
            lineHeight = 32.sp
            ),
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = stringResource(description) + "icon",
                        modifier = Modifier.size(78.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                    Text(
                        text = stringResource(description),
                        fontSize = 16.sp,
                    )
                }
            }
            VerticalDivider(
                thickness = 3.dp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .height(94.dp),
            )
            Column {
                Text(
                    text = "$temperature ºC",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
                Row {
                    Icon(
                        painterResource(secondFieldIcon),
                        "tempMin",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        secondField,
                        fontSize = 18.sp,
                    )
                }

                Row {
                    Icon(
                        painterResource(thirdFieldIcon),
                        secondField + "icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Text(thirdField)
                }
            }
        }
    }
}