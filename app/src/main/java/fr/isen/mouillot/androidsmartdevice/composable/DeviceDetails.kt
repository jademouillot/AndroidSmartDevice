package fr.isen.mouillot.androidsmartdevice.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.isen.mouillot.androidsmartdevice.R

@Composable
fun DeviceDetails(deviceName: String) {
    val isCheckedState = remember { mutableStateOf(false) }
    Column {
        // Ajoutez d'autres détails de l'appareil ici si nécessaire
        Spacer(modifier = Modifier.height(16.dp))
        // Texte du nom de l'appareil centré
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$deviceName",
                style = TextStyle(fontSize = 20.sp),
                color = Color(0xFF87CEEB),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = "Affichage des differentes LEDs",
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(start = 8.dp) // Ajouter un espace à gauche
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row{
            val imageResource = painterResource(id = R.drawable.lamp)

            // Affichage de l'image
            Image(
                painter = imageResource,
                contentDescription = null, // Vous pouvez ajouter une description si nécessaire
                modifier = Modifier.size(80.dp, 80.dp)
                        .padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = imageResource,
                contentDescription = null, // Vous pouvez ajouter une description si nécessaire
                modifier = Modifier.size(80.dp, 80.dp)
                    .padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = imageResource,
                contentDescription = null, // Vous pouvez ajouter une description si nécessaire
                modifier = Modifier.size(80.dp, 80.dp)
                    .padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "Abonnez-vous pour recevoir le nombre d'incrémentation",
                    style = TextStyle(fontSize = 17.sp),
                    modifier = Modifier.padding(start = 8.dp) // Ajouter un espace à gauche
                )
            }
            // CheckBox
            Checkbox(
                checked = isCheckedState.value,
                onCheckedChange = { isCheckedState.value = it },
                modifier = Modifier
                    .size(24.dp) // Taille du carré
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "RECEVOIR",
                    style = TextStyle(fontSize = 17.sp),
                    modifier = Modifier.padding(start = 8.dp) // Ajouter un espace à gauche
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "Nombre : ",
                    style = TextStyle(fontSize = 17.sp),
                    modifier = Modifier.padding(start = 8.dp) // Ajouter un espace à gauche
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "nombre",
                    style = TextStyle(fontSize = 22.sp),
                    modifier = Modifier.padding(start = 8.dp) // Ajouter un espace à gauche
                )
            }
        }
    }
}