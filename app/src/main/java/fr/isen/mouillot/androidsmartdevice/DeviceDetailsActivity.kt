package fr.isen.mouillot.androidsmartdevice

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import fr.isen.mouillot.androidsmartdevice.composable.DeviceDetails
import fr.isen.mouillot.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import java.util.UUID

class DeviceDetailsActivity : ComponentActivity() {

    private var gatt: BluetoothGatt? = null
    private var isConnecting by mutableStateOf(true) // État de la connexion

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceAddress = intent.getStringExtra("deviceAddress")
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)

        setContent {
            AndroidSmartDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopBarDevice("AndroidSmartDevice")
                        DeviceDetailsScreen()
                        if (device?.name != null) {
                            DeviceDetails(device.name!!)
                        }
                        if (device != null) {
                            connectToDevice(device)
                        }
                        gatt?.discoverServices()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        gatt = device.connectGatt(this, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.e("callbackgatt","Callback gatt")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e("callbackgatt","Callback gatt connected")
                isConnecting = false // La connexion est établie
                // La connexion est établie
                //Toast.makeText(this@DeviceDetailsActivity, "Connecté à l'appareil", Toast.LENGTH_LONG).show()
                // Vous pouvez commencer à découvrir les services ici
            }
        }
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val services = gatt?.services
                if (services != null && services.size >= 3) {
                    val thirdService = services[2] // Troisième service (index 2)
                    val characteristics = thirdService.characteristics
                    if (characteristics.isNotEmpty()) {
                        val firstCharacteristic = characteristics[0] // Première caractéristique du troisième service
                        Log.e("charac","Acces à la première caractéristique du troisième service")
                        // Vous pouvez maintenant interagir avec la première caractéristique

                        // Activer les notifications pour cette caractéristique
                        gatt.setCharacteristicNotification(firstCharacteristic, true)

                        // Rechercher le descripteur de notification pour activer les notifications
                        val descriptor = firstCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                        // Activer les notifications pour ce descripteur
                        descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                    } else {
                        // Aucune caractéristique dans le troisième service
                        Log.e("charac","Aucune caractéristique dans le troisième service")
                    }
                } else {
                    // L'appareil ne dispose pas de suffisamment de services
                    Log.e("charac","L'appareil ne dispose pas de suffisamment de services")
                }
            } else {
                // Erreur lors de la découverte des services
                Log.e("charac","Erreur lors de la découverte des services")
            }
        }
        // 1. Fonction pour écrire la valeur à la caractéristique
        @SuppressLint("MissingPermission")
        private fun writeValueToCharacteristic(value: ByteArray) {
            val gatt = gatt // Récupérez votre objet BluetoothGatt ici
            val service = gatt?.services?.get(2) // Troisième service (index 2)
            val characteristic = service?.characteristics?.get(0) // Première caractéristique du troisième service

            // Vérifiez si la caractéristique est valide
            if (characteristic != null) {
                characteristic.value = value
                gatt.writeCharacteristic(characteristic)
            } else {
                Log.e("writeValueToCharacteristic", "Caractéristique non valide")
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        gatt?.disconnect()
        gatt?.close()
    }

    @Composable
    fun DeviceDetailsScreen() {
        // Votre code actuel pour la mise en page de l'écran
        // Ajoutez ce composant Text en bas de la page
        if (!isConnecting) {
            Text(
                text = "Connexion établie",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    //.align(Alignment.BottomCenter),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    //color = MaterialTheme.colors.secondary
                )
            )
        }
        else{
            Text(
                text = "Connexion en cours",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                //.align(Alignment.BottomCenter),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    //color = MaterialTheme.colors.secondary
                )
            )
        }
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

