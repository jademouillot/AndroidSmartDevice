package fr.isen.mouillot.androidsmartdevice

//SCANCOMPOSABLEINTERACTION

import android.app.Activity
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.isen.mouillot.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.content.ContextCompat.startActivity

class ScanActivityBLE : ComponentActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val REQUEST_ENABLE_BT = 1
    private val detectedDevices = mutableListOf<BluetoothDevice>()

    private val handler = Handler()
    private var isPlaying by mutableStateOf(false)
    private var scanStarted by mutableStateOf(false)
    private var scanning = false
    private val sCANPERIOD: Long = 10000

    private val detectedDevicesState = mutableStateOf<List<BluetoothDevice>>(emptyList())

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

// Initialise bluetoothAdapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Initialise bluetoothLeScanner
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        // Déclaration de la variable bluetoothAdapter
        //val bluetoothAdapter: BluetoothAdapter? by lazy {
            // Initialisation de bluetoothAdapter en utilisant BluetoothManager
          //  val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            //bluetoothManager.adapter
        //}

        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        val packageManager = this.packageManager
        val hasBluetoothLE = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)

        val bluetoothPermission = Manifest.permission.BLUETOOTH
        val bluetoothAdminPermission = Manifest.permission.BLUETOOTH_ADMIN
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val bluetoothScanPermission = Manifest.permission.BLUETOOTH_SCAN
        val bluetoothConnectPermission = Manifest.permission.BLUETOOTH_CONNECT

        val hasBluetoothPermission = ContextCompat.checkSelfPermission(this, bluetoothPermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothAdminPermission = ContextCompat.checkSelfPermission(this, bluetoothAdminPermission) == PackageManager.PERMISSION_GRANTED
        val hasLocationPermission = ContextCompat.checkSelfPermission(this, locationPermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothScanPermission = ContextCompat.checkSelfPermission(this, bluetoothScanPermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothConnectPermission = ContextCompat.checkSelfPermission(this, bluetoothConnectPermission) == PackageManager.PERMISSION_GRANTED

        val rEQUESTBLUETOOTHPERMISSION = 123 // Remplacez 123 par la valeur de votre choix

        // Vérification des permissions Bluetooth
        if (!hasBluetoothPermission || !hasBluetoothAdminPermission || !hasBluetoothScanPermission || !hasLocationPermission || !hasBluetoothConnectPermission) {
            // Si les permissions Bluetooth nécessaires ne sont pas accordées, demandez-les à l'utilisateur
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    bluetoothPermission,
                    bluetoothAdminPermission,
                    bluetoothScanPermission,
                    bluetoothConnectPermission,
                    locationPermission
                ),
                rEQUESTBLUETOOTHPERMISSION
            )
        }

        setContent {
            AndroidSmartDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopBarBLE("AndroidSmartDevice")

                        val context = LocalContext.current

                        if (hasBluetoothLE) {

                            Toast.makeText(context, "Prise en charge du BLE, vérification s'il est activé et permis", Toast.LENGTH_SHORT).show()

                            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {

                                Toast.makeText(context, "BLE activé", Toast.LENGTH_SHORT).show()

                            } else {
                                Toast.makeText(context, "BLE pas activé", Toast.LENGTH_SHORT).show()

                                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                                ActivityCompat.requestPermissions(context as Activity, arrayOf(bluetoothPermission, bluetoothAdminPermission, bluetoothScanPermission, bluetoothConnectPermission, locationPermission), rEQUESTBLUETOOTHPERMISSION)
                            }

                        } else {
                            // Le dispositif ne prend pas en charge Bluetooth Low Energy
                            Text(text = "Pas de prise en charge du BLE sur cet appareil, vous ne pouvez pas l'utiliser")
                        }

                        Column {
                            Spacer(modifier = Modifier.height(16.dp)) // Ajouter un espace horizontal
                            Text(
                                text = "Commencer le scan BLE",
                                style = TextStyle(
                                    fontSize = 28.sp, // Taille de la police
                                    fontWeight = FontWeight.Bold, // Poids de la police
                                    //color = Color.BLACK // Couleur du texte
                                ),
                                textAlign = TextAlign.Center, // Alignement du texte au centre
                                //modifier = Modifier.align(Alignment.CenterHorizontally) // Alignement du texte au centre
                            )
                            Spacer(modifier = Modifier.height(10.dp)) // Espacement horizontal de 8 DP
                            PlayPauseButton_ScanBLE(onPlayClick = { isPlaying = true }, onStopClick = { isPlaying = false })
                            scanLeDevice()
                            // Afficher la liste des appareils détectés
                            DeviceList(devices = detectedDevicesState.value, isVisible = scanStarted)
                        }
                    }

                }
            }
        }

    }

    // Modifiez votre fonction scanLeDevice() pour démarrer ou arrêter le scan en fonction de la valeur de isPlaying
    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        if (isPlaying) {
            startScan()
            scanStarted = true
        } else {
            stopScan()
        }
    }

    // Fonction pour démarrer le scan
    @SuppressLint("MissingPermission")
    private fun startScan() {
        if (!scanning) {
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, sCANPERIOD)
            scanning = true
            bluetoothLeScanner.startScan(leScanCallback)
            Log.e("ScanCallback", "Scan started")
        }
    }

    // Fonction pour arrêter le scan
    @SuppressLint("MissingPermission")
    private fun stopScan() {
        if (scanning) {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
            Log.e("ScanCallbackbis", "Scan stopped")
        }
    }

    // Device scan callback.
    val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.e("ScanCallbackbiis", "Scancallback fonction")
            val device = result.device
            if (!detectedDevices.contains(device)) {
                detectedDevices.add(device)
                detectedDevicesState.value = detectedDevices.toList() // Met à jour l'état Compose
            }
        }
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            // Ajoutez ici le code pour gérer l'erreur de scan
            Log.e("ScanCallbackbiiis", "Scan failed with error code: $errorCode")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopScan()
    }

    override fun onPause() {
        super.onPause()
        stopScan()
    }
}

@Composable
fun PlayPauseButton_ScanBLE(onPlayClick: () -> Unit, onStopClick: () -> Unit) {
    var isPlaying by remember { mutableStateOf(false) }
    var scanCompleted by remember { mutableStateOf(false) }
    //var scanResults by remember { mutableStateOf(mutableListOf<String>()) }
    val imageRes =
        if (isPlaying) R.drawable.baseline_arrow_outward_24 else R.drawable.baseline_arrow_forward_24
    val buttonText =
        if (isPlaying) "Scan BLE en cours..." else "Appuyer sur le bouton pour commencer le scan BLE"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Centrer horizontalement le contenu
        modifier = Modifier
            .clickable {
                isPlaying = !isPlaying
                if (isPlaying) {
                    // Début du scan
                    onPlayClick() // Démarrer le scan
                    scanCompleted = false

                } else {
                    // Fin du scan
                    onStopClick() // Arreter le scan
                    scanCompleted = true
                }
            }
            .padding(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth() // Remplir la largeur de la colonne parente
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .padding(start = 30.dp), // Ajouter un padding de 8dp à gauche
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Play/Pause",
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Ajouter un espace horizontal
            Text(text = buttonText)
        }

        if (isPlaying) {
            LinearProgressIndicator(
                color = Color.Black, // Couleur de la barre de progression
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Marge horizontale
            )
        }
        if (!isPlaying) {
            Spacer(modifier = Modifier.height(10.dp)) // Espacement horizontal de 8 DP

            // Barre horizontale
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp) // Hauteur de la barre
                    .background(Color.Black) // Couleur de la barre
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarBLE(title: String) {

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

@SuppressLint("MissingPermission")
@Composable
fun DeviceList(devices: List<BluetoothDevice>, isVisible: Boolean) {
    if (isVisible) {
        val context = LocalContext.current
        Column {
            Text(text = "Appareils détectés :")
            devices.forEach { device ->
                ClickableText(
                    text = buildAnnotatedString {
                        append(device.name ?: "Unknown device")
                    },
                    onClick = {
                            val intent = Intent(context, DeviceDetailsActivity::class.java).apply {
                                putExtra("device_name", device.name)
                            }
                            //startActivity(intent)

                    }
                    //style = SpanStyle(textDecoration = TextDecoration.Underline)
                )
            }
        }
    }
}