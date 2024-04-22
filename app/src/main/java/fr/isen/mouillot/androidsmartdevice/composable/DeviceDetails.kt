package fr.isen.mouillot.androidsmartdevice.composable

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.isen.mouillot.androidsmartdevice.ImageClickListener
import fr.isen.mouillot.androidsmartdevice.ImageId
import fr.isen.mouillot.androidsmartdevice.R

interface CheckboxListener {
    fun onCheckboxChecked(checked: Boolean)
}


@Composable
fun DeviceDetails(deviceName: String, clickListener: ImageClickListener, listenercheckbox: CheckboxListener) {

    var isChecked by remember { mutableStateOf(false) }
    var isChecked1 by remember { mutableStateOf(false) }
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
            var clicked_fimage by remember { mutableStateOf(false) }
            var clicked_simage by remember { mutableStateOf(false) }
            var clicked_timage by remember { mutableStateOf(false) }

            Image(
                painter = imageResource,
                contentDescription = null, // Vous pouvez ajouter une description si nécessaire
                modifier = Modifier
                    .size(80.dp, 80.dp)
                    .padding(start = 8.dp)
                    .clickable {
                        clicked_fimage = !clicked_fimage
                        clickListener.onImageClicked(ImageId.FIRST_IMAGE)
                    },
                colorFilter = if (clicked_fimage) ColorFilter.tint(Color(0xFF87CEEB)) else ColorFilter.tint(Color.White)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = imageResource,
                contentDescription = null, // Vous pouvez ajouter une description si nécessaire
                modifier = Modifier
                    .size(80.dp, 80.dp)
                    .padding(start = 8.dp)
                    .clickable {
                        clicked_simage = !clicked_simage
                        clickListener.onImageClicked(ImageId.SECOND_IMAGE)
                    },
                colorFilter = if (clicked_simage) ColorFilter.tint(Color(0xFF87CEEB)) else ColorFilter.tint(Color.White)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = imageResource,
                contentDescription = null, // Vous pouvez ajouter une description si nécessaire
                modifier = Modifier
                    .size(80.dp, 80.dp)
                    .padding(start = 8.dp)
                    .clickable {
                        clicked_timage = !clicked_timage
                        clickListener.onImageClicked(ImageId.THIRD_IMAGE)
                    },
                colorFilter = if (clicked_timage) ColorFilter.tint(Color(0xFF87CEEB)) else ColorFilter.tint(Color.White)
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
            val context = LocalContext.current
            // CheckBox
            Checkbox(
                checked = isChecked,
                        onCheckedChange = { checked ->
                    isChecked = checked
                    listenercheckbox.onCheckboxChecked(checked)
                    val message = if (checked) "RECEVOIR activé" else "RECEVOIR désactivé"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                },
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
                    text = "",
                    style = TextStyle(fontSize = 22.sp),
                    modifier = Modifier.padding(start = 8.dp) // Ajouter un espace à gauche
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "Recevoir la donnée du compteur",
                    style = TextStyle(fontSize = 17.sp),
                    modifier = Modifier.padding(start = 8.dp) // Ajouter un espace à gauche
                )
            }
            val context = LocalContext.current
            // CheckBox
            Checkbox(
                checked = isChecked1,
                onCheckedChange = { checked1 ->
                    isChecked1 = checked1
                    val message = if (checked1) "S'ABONNER activé" else "S'ABONNER désactivé"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(24.dp) // Taille du carré
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "S'ABONNER",
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