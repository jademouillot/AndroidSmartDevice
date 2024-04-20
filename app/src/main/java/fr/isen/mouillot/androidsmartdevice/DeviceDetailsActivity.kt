package fr.isen.mouillot.androidsmartdevice

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import fr.isen.mouillot.androidsmartdevice.composable.DeviceDetails
import fr.isen.mouillot.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class DeviceDetailsActivity : ComponentActivity() {

    private var gatt: BluetoothGatt? = null

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
                        if (device?.name != null) {
                            DeviceDetails(device.name!!)
                        }
                        if (device != null) {
                            connectToDevice(device)
                        }

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
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // La connexion est établie
                Toast.makeText(this@DeviceDetailsActivity, "Connecté à l'appareil", Toast.LENGTH_SHORT).show()
                // Vous pouvez commencer à découvrir les services ici
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        gatt?.disconnect()
        gatt?.close()
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