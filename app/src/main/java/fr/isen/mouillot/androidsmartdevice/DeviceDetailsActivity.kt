package fr.isen.mouillot.androidsmartdevice

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
import android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.lifecycle.ViewModel
import fr.isen.mouillot.androidsmartdevice.composable.CheckboxListener
import fr.isen.mouillot.androidsmartdevice.composable.DeviceDetails
import fr.isen.mouillot.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import java.util.UUID

enum class ImageId {
    FIRST_IMAGE,
    SECOND_IMAGE,
    THIRD_IMAGE
}

interface ImageClickListener {
    fun onImageClicked(imageId: ImageId)
}

private const val TAG = "BluetoothLeService"

class DeviceDetailsActivity : ComponentActivity(), ImageClickListener, CheckboxListener {

    private fun BluetoothGattCharacteristic.isIndicatable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE)

    private fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

    fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean =
        properties and property != 0

    //private var bluetoothAdapter: BluetoothAdapter? = null

    private var gatt: BluetoothGatt? = null
    private var isConnecting by mutableStateOf(true) // État de la connexion

    private var characteristicValue: ByteArray? = byteArrayOf()
    private var characteristic: BluetoothGattCharacteristic? = null

    private var services: List<BluetoothGattService>? = null
    private var service: BluetoothGattService? = null

    private var characteristicrec: BluetoothGattCharacteristic? = null

    private var isFirstImageClicked: Boolean = false // Variable pour suivre si la première image a été cliquée
    private var isSecondImageClicked: Boolean = false // Variable pour suivre si la première image a été cliquée
    private var isThirdImageClicked: Boolean = false // Variable pour suivre si la première image a été cliquée

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
                            DeviceDetails(device.name!!, this@DeviceDetailsActivity, this@DeviceDetailsActivity)
                        }
                        if (device != null) {
                            connectToDevice(device)
                        }
                        //gatt?.discoverServices()
                    }
                }
            }
        }
    }

    override fun onImageClicked(imageId: ImageId) {
        // Logique à exécuter lorsque l'image est cliquée
        when (imageId) {
            ImageId.FIRST_IMAGE -> {
                if (!isFirstImageClicked) {
                    // Logique pour la première image
                    val valueToWrite1 = byteArrayOf(0x01)
                    Log.e("write", "write")
                    writeValueToCharacteristic(valueToWrite1)
                    isFirstImageClicked = true
                }else{
                    val valueToWrite4 = byteArrayOf(0x00)
                    writeValueToCharacteristic(valueToWrite4)
                    isFirstImageClicked = false
                }
            }
            ImageId.SECOND_IMAGE -> {
                if (!isSecondImageClicked) {
                    // Logique pour la première image
                    val valueToWrite2 = byteArrayOf(0x02)
                    Log.e("write", "write")
                    writeValueToCharacteristic(valueToWrite2)
                    isSecondImageClicked = true
                }else{
                    val valueToWrite4 = byteArrayOf(0x00)
                    writeValueToCharacteristic(valueToWrite4)
                    isSecondImageClicked = false
                }
            }
            ImageId.THIRD_IMAGE -> {
                if (!isThirdImageClicked) {
                    // Logique pour la première image
                    val valueToWrite3 = byteArrayOf(0x03)
                    Log.e("write", "write")
                    writeValueToCharacteristic(valueToWrite3)
                    isThirdImageClicked = true
                }else{
                    val valueToWrite4 = byteArrayOf(0x00)
                    writeValueToCharacteristic(valueToWrite4)
                    isThirdImageClicked = false
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        gatt = device.connectGatt(this, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.e("callbackgatt","Callback gatt")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e("callbackgatt","Callback gatt connected")
                isConnecting = false // La connexion est établie
                gatt?.discoverServices() // Découvrir les services après la connexion
            }
        }
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.e("onservices","OnServiceDiscovered")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                services = gatt?.services
                if (services != null && services!!.isNotEmpty()) {
                    Log.e("ServiceListSize", "Nombre de services découverts : ${services!!.size}")
                    if(services!!.size >= 3){
                        val thirdService = services!![2] // Troisième service (index 2)
                        val characteristics = thirdService.characteristics
                        if (characteristics.isNotEmpty()) {
                            val firstCharacteristic = characteristics[0] // Première caractéristique du troisième service
                            Log.e("charac","Acces à la première caractéristique du troisième service")
                            // Vous pouvez maintenant interagir avec la première caractéristique

                            // Activer les notifications pour cette caractéristique
                            gatt?.setCharacteristicNotification(firstCharacteristic, true)

                            // Rechercher le descripteur de notification pour activer les notifications
                            val descriptor = firstCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                            // Activer les notifications pour ce descripteur
                            descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt?.writeDescriptor(descriptor)


                        } else {
                            // Aucune caractéristique dans le troisième service
                            Log.e("charac","Aucune caractéristique dans le troisième service")
                        }
                    }
                    else{
                        Log.e("charac","sors du if")
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
        @OptIn(ExperimentalStdlibApi::class)
        @Deprecated("Deprecated for Android 13+")
        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            with(characteristic) {
                Log.i("BluetoothGattCallback", "Characteristic $uuid changed | value: ${value.toHexString()}")
            }
        }

        @OptIn(ExperimentalStdlibApi::class)
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            val newValueHex = value.toHexString()
            with(characteristic) {
                Log.i("BluetoothGattCallback", "Characteristic $uuid changed | value: $newValueHex")
            }
        }
    }

    // Méthode pour écrire dans la caractéristique une fois la connexion établie et les services découverts
    @SuppressLint("MissingPermission")
    private fun writeValueToCharacteristic(value: ByteArray) {
        // Vérifiez si la connexion Bluetooth est établie et que gatt n'est pas null
        if (gatt != null) {
            // Récupérez le troisième service
            Log.e("write1","write1")
            service = gatt?.services?.get(2)
            // Troisième service (index 2)
            Log.e("write2","write2")

            // Vérifiez si le service et ses caractéristiques sont valides
            if (service != null && service!!.characteristics.isNotEmpty()) {
                Log.e("write3","write3")
                // Récupérez la première caractéristique du troisième service
                val characteristic = service!!.characteristics[0] // Première caractéristique du troisième service
                characteristicrec = service!!.characteristics[1]
                // Vérifiez si la caractéristique est valide
                if (characteristic != null) {
                    // Écrivez la valeur dans la caractéristique
                    Log.e("write4","write4")
                    characteristic.value = value
                    //Log.d("CharacteristicValue", "Valeur de la value : $value")
                    //Log.d("CharacteristicValue", "Valeur de la caractéristique : ${characteristic.value?.contentToString()}")
                    gatt?.writeCharacteristic(characteristic)
                } else {
                    Log.e("writeValueToCharacteristic", "Caractéristique non valide")
                }
            } else {
                Log.e("writeValueToCharacteristic", "Aucune caractéristique dans le troisième service")
            }
        } else {
            Log.e("writeValueToCharacteristic", "Connexion Bluetooth non établie")
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

    @SuppressLint("MissingPermission")
    fun writeDescriptor(descriptor: BluetoothGattDescriptor, payload: ByteArray) {
        gatt?.let { gatt ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeDescriptor(descriptor, payload)
            } else {
                // Fall back to deprecated version of writeDescriptor for Android <13
                gatt.legacyDescriptorWrite(descriptor, payload)
            }
        } ?: error("Not connected to a BLE device!")
    }
    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.S)
    @Suppress("DEPRECATION")
    private fun BluetoothGatt.legacyDescriptorWrite(
        descriptor: BluetoothGattDescriptor,
        value: ByteArray
    ) {
        descriptor.value = value
        writeDescriptor(descriptor)
    }

    @SuppressLint("MissingPermission")
    fun enableNotifications(characteristic: BluetoothGattCharacteristic) {
        Log.e("enable","enablenotif")
        val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        val payload = when {
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> {
                Log.e("ConnectionManager", "${characteristic.uuid} doesn't support notifications/indications")
                return
            }
        }

        characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
            if (gatt?.setCharacteristicNotification(characteristic, true) == false) {
                Log.e("ConnectionManager", "setCharacteristicNotification failed for ${characteristic.uuid}")
                return
            }
            Log.e("writedescr","writedescriptor")
            writeDescriptor(cccDescriptor, payload)
        } ?: Log.e("ConnectionManager", "${characteristic.uuid} doesn't contain the CCC descriptor!")
    }

    @SuppressLint("MissingPermission")
    fun disableNotifications(characteristic: BluetoothGattCharacteristic) {
        if (!characteristic.isNotifiable() && !characteristic.isIndicatable()) {
            Log.e("ConnectionManager", "${characteristic.uuid} doesn't support indications/notifications")
            return
        }

        val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
            if (gatt?.setCharacteristicNotification(characteristic, false) == false) {
                Log.e("ConnectionManager", "setCharacteristicNotification failed for ${characteristic.uuid}")
                return
            }
            writeDescriptor(cccDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        } ?: Log.e("ConnectionManager", "${characteristic.uuid} doesn't contain the CCC descriptor!")
    }

    override fun onCheckboxChecked(checked: Boolean) {
        Log.e("checkbox","checkboxchecked")
        // Appeler cette fonction lorsque la checkbox est cochée ou décochée
        if (checked) {
            characteristicrec?.let { enableNotifications(it) }
        } else {
            characteristic = gatt?.services?.get(2)?.characteristics?.get(1)
            characteristic?.let { disableNotifications(it) }
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