package pt.nuage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.nuage.R

@Composable
fun AboutScreen(
    modifier: Modifier
) {
    AboutContent(
        modifier = modifier
    )
}

@Composable
fun AboutContent(modifier: Modifier) {
    Row(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color(0x00000000))
                    ) {
                        Image(
                            painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = "Nuage app logo branding",
                            modifier = Modifier.size(156.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("v1.0.0")
                }
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        stringResource(R.string.aboutProgrammerHeading),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text("Vítor Veríssimo")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.aboutTechHeading),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(stringResource(R.string.aboutTechApi))
                    Text(stringResource(R.string.aboutTechIcons))
                    Text(stringResource(R.string.aboutTechImages))
                }
            }
        }
    }
}