package fr.isen.mouillot.androidsmartdevice

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.content.ContextCompat.startActivity
import java.security.AllPermission

class ScanActivityBLE : ComponentActivity() {

    private val leScanCallback= object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            //Log.e("ScanCallbackbiis", "Scancallback fonction : ${result.device.name}")
            Log.e("ScanCallbackbiis", "Scancallback fonction : ")
            val device = result.device
            if (!detectedDevices.contains(device)) {
                detectedDevices.add(device)
            }

        }
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            // Ajoutez ici le code pour gérer l'erreur de scan
            Log.e("ScanCallbackbiiis", "Scan failed with error code: $errorCode")
            Toast.makeText(this@ScanActivityBLE, "erreur callback", Toast.LENGTH_LONG).show()
        }
    }

    private fun isAllPermissionsGranted() = getAllPermissionsForBLE().all{
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getAllPermissionsForBLE(): Array<String> {
        var AllPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AllPermissions = AllPermissions.plus(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADMIN
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AllPermissions = AllPermissions.plus(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        return AllPermissions
    }

    // Demande les autorisations nécessaires
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissions() {
        requestPermissionLauncher.launch(
/*
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
*/
            getAllPermissionsForBLE()
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                // Toutes les autorisations sont accordées
                hasPermission = true
            } else {
                // Au moins une autorisation a été refusée
            }
        }

    private var hasPermission by mutableStateOf(false)
    private var isPlaying by mutableStateOf(false)
    private val REQUEST_ENABLE_BT = 1

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bluetoothLeScanner: BluetoothLeScanner? by lazy(LazyThreadSafetyMode.NONE) {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private var detectedDevices: MutableList<BluetoothDevice> = mutableListOf()

    private val SCANPERIOD: Long = 10000

    private val handler = Handler()

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {

        val packageManager = this.packageManager
        val hasBluetoothLE = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)

        super.onCreate(savedInstanceState)

        // Demander les autorisations si elles ne sont pas déjà accordées
        if (!isAllPermissionsGranted()) {
            requestPermissions()
        } else {
            // Autorisations déjà accordées
            hasPermission = true
        }

        if (hasBluetoothLE) {
            Toast.makeText(this, "Prise en charge du BLE, vérification s'il est activé et permis", Toast.LENGTH_SHORT).show()
            if (bluetoothAdapter != null && bluetoothAdapter?.isEnabled == true) {
                Toast.makeText(this, "BLE activé", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "BLE pas activé", Toast.LENGTH_SHORT).show()
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        } else {
            Toast.makeText(this, "Pas de prise en charge du BLE sur cet appareil, vous ne pouvez pas l'utiliser", Toast.LENGTH_SHORT).show()
        }

        setContent {
            AndroidSmartDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

                    Column {
                        TopBarBLE("AndroidSmartDevice")

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
                        }
                        if (hasPermission) {
                            Log.e("Permission", "Permission accordées")

                            PlayPauseButton_ScanBLE(
                                isPlaying,
                                onPlayClick = { isPlaying = true },
                                onStopClick = { isPlaying = false }
                            )

                            if (isPlaying) {
                                scanLeDeviceWithPermission(isPlaying)
                            }
                            DeviceList(devices = detectedDevices)

                        } else {
                            Log.e("Permission", "Pas de permission, pas de Scan BLE")
                        }
                    }
                }
            }
        }
    }

    // Device scan callback.

    @Composable
    @SuppressLint("MissingPermission")
    private fun scanLeDeviceWithPermission(enable: Boolean) {
        bluetoothAdapter?.bluetoothLeScanner?.apply {
            //if (enable) {
                //if (!isPlaying) {
                    if (enable) {
                        handler.postDelayed({
                            isPlaying = false
                            Log.e("handler","apres le false")
                            bluetoothLeScanner?.stopScan(leScanCallback)
                            Log.e("handler","apres le stopscan")
                        }, SCANPERIOD)
                        isPlaying = true
                        Log.e("handler","sortie du handler")
                        bluetoothLeScanner?.startScan(leScanCallback)
                        //DeviceList(devices = detectedDevices)
                        Log.e("handler","apres le startscan")
                        detectedDevices.clear()
                        //Log.e("ScanCallback", "Scan started")
                        //DeviceList(devices = detectedDevices)
                    } else {
                        isPlaying = false
                        Log.e("else","else")
                        bluetoothLeScanner?.stopScan(leScanCallback)
                    }
            DeviceList(devices = detectedDevices)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        bluetoothLeScanner?.stopScan(leScanCallback)
    }

    @SuppressLint("MissingPermission")
    override fun onPause() {
        super.onPause()
        bluetoothLeScanner?.stopScan(leScanCallback)
    }

}

@Composable
fun PlayPauseButton_ScanBLE(isPlaying: Boolean, onPlayClick: () -> Unit, onStopClick: () -> Unit) {
    var scanCompleted by remember { mutableStateOf(false) }
    val imageRes =
        if (isPlaying) R.drawable.baseline_arrow_outward_24 else R.drawable.baseline_arrow_forward_24
    val buttonText =
        if (isPlaying) "Scan BLE en cours..." else "Appuyer sur le bouton pour commencer le scan BLE"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Centrer horizontalement le contenu
        modifier = Modifier
            .clickable {
                if (isPlaying) {
                    onStopClick() // Si c'est en lecture, arrêtez-la
                    scanCompleted = true
                } else {
                    onPlayClick() // Si ce n'est pas en lecture, commencez
                    scanCompleted = false
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
fun DeviceList(devices: List<BluetoothDevice>) {
    var detectedDevices by remember { mutableStateOf(devices) } // Initialisation de detectedDevices avec remember
    val context = LocalContext.current
    Text(text = "Appareils détectés :")
    LazyColumn {
        items(detectedDevices) { device ->
            Text(
                text = device.address ?: "Unknown device",
                modifier = Modifier.clickable {
                    val intent = Intent(context, DeviceDetailsActivity::class.java).apply {
                        putExtra("device", device.address)
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}