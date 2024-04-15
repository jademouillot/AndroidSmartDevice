package fr.isen.mouillot.androidsmartdevice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.mouillot.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import androidx.appcompat.app.ActionBar;
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        //val actionBar: android.app.ActionBar = getSupportActionBar()

        //if (actionBar != null) {
            //actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#3F51B5"))); // Changer la couleur de fond
            //actionBar.setTitle("Nouveau titre"); // Changer le titre
            //actionBar.setDisplayHomeAsUpEnabled(true); // Afficher le bouton de retour
        //}

        super.onCreate(savedInstanceState)
        setContent {
            AndroidSmartDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopBar("AndroidSmartDevice")
                        //Greeting("Android")
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.TopCenter)
                                .padding(top = 16.dp) // Espace entre le haut de l'écran et le texte
                        ) {
                            Text(
                                text = "Bienvenue dans votre application Smart Device",
                                style = TextStyle(
                                    fontSize = 28.sp, // Taille de la police
                                    fontWeight = FontWeight.Bold, // Poids de la police
                                    //color = Color.BLACK // Couleur du texte
                                ),
                                textAlign = TextAlign.Center, // Alignement du texte au centre
                                modifier = Modifier
                                //.align(Alignment.TopCenter) // Alignement du texte au centre
                            )
                            Spacer(modifier = Modifier.height(30.dp)) // Espacement horizontal de 8 DP
                            Text(
                                text = "Pour démarrer vos interactions avec les appareils BLE environnants cliquer sur Commencer",
                                style = TextStyle(
                                    fontSize = 18.sp, // Taille de la police
                                    //fontWeight = FontWeight.Bold, // Poids de la police
                                    //color = Color.BLACK // Couleur du texte
                                ),
                                textAlign = TextAlign.Center, // Alignement du texte au centre
                                modifier = Modifier
                                //.align(Alignment.TopCenter) // Alignement du texte au centre
                            )
                            Spacer(modifier = Modifier.height(30.dp)) // Espacement horizontal de 8 DP
                            Image(
                                painter = painterResource(id = R.drawable.bluetooth),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(200.dp) // Taille de l'image
                                    .padding(16.dp) // Marge autour de l'image
                                    .align(Alignment.CenterHorizontally) // Aligner l'image au centre horizontalement
                                    //.tint(Color.WHITE) // Modifier la couleur de l'image en blanc
                            )
                            val context = LocalContext.current
                            Spacer(modifier = Modifier.height(200.dp)) // Espacement horizontal de 8 DP
                            Button(
                                onClick = {navigateToActivity(context, ScanActivityBLE::class.java)},
                                modifier = Modifier
                                    .fillMaxWidth() // Prend toute la largeur de la page
                                    .padding(horizontal = 16.dp), // Ajoute une marge horizontale
                                shape = RectangleShape // Bords rectangulaires
                            ) {
                                Text("COMMENCER")
                            }
                    }

                    }
                }
            }
        }
    }

    fun navigateToActivity(context: Context, destinationActivity: Class<*>) {
        val intent = Intent(context, destinationActivity)
        context.startActivity(intent)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {

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


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidSmartDeviceTheme {
        Greeting("Android")
    }
}