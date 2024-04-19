package fr.isen.mouillot.androidsmartdevice

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.mouillot.androidsmartdevice.composable.DeviceDetails
import fr.isen.mouillot.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class DeviceDetailsActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceName = intent.getStringExtra("device")

        setContent {
            AndroidSmartDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopBarDevice("AndroidSmartDevice")
                        if (deviceName != null) {
                            DeviceDetails(deviceName)
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    AndroidSmartDeviceTheme {
        Greeting3("Android")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarDevice(title: String) {

    val valcolorwhite = colorResource(id = R.color.white)

    Surface(
        color = valcolorwhite, // Couleur de fond de la barre
        modifier = Modifier.fillMaxWidth(),
        contentColor = valcolorwhite // Couleur du texte de la barre
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.secondary,
            ),
            title = {
                Text(
                    text = title,
                    color = valcolorwhite // Couleur du texte du titre de la barre
                )
            },

            )
    }
}