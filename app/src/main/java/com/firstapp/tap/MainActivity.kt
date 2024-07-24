package com.firstapp.tap

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.Manifest.permission.CHANGE_WIFI_STATE
import android.Manifest.permission.INTERNET
import android.Manifest.permission.NFC
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.firstapp.tap.ui.theme.TAPTheme

class MainActivity : ComponentActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothManager: BluetoothManager

    private lateinit var wifiManager: WifiManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ permissions ->
        permissions.entries.forEach{
            val permissionName = it.key
            val isGranted = it.value
            if(isGranted){
                Toast.makeText(this, "$permissionName granted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "$permissionName denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_activity)

        val bluetoothActivity = findViewById<ImageView>(R.id.GoToBluetoothActivity)
        val wifiActivity = findViewById<ImageView>(R.id.GoToWifiActivity)
        val nfcActivity = findViewById<ImageView>(R.id.GoToNFCActivity)

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager


        if (bluetoothAdapter == null){
            Toast.makeText(this, "This device does not support Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        val permissionsToRequestForBluetooth = arrayOf(
            BLUETOOTH,
            BLUETOOTH_CONNECT,
            BLUETOOTH_ADMIN,
            BLUETOOTH_SCAN,
            BLUETOOTH_ADVERTISE,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )

        val permissionsToRequestForWifi = arrayOf(
            ACCESS_WIFI_STATE,
            CHANGE_WIFI_STATE,
            ACCESS_NETWORK_STATE,
            INTERNET,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )

        val permissionToRequestForNFC = arrayOf(
            NFC,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )

        bluetoothActivity.setOnClickListener {
            requestPermissionLauncher.launch(permissionsToRequestForBluetooth)
            toggleBluetooth()
//            startActivity(Intent(this, BluetoothActivity::class.java))
        }

        wifiActivity.setOnClickListener{
            requestPermissionLauncher.launch(permissionsToRequestForWifi)
            toggleWiFi()
        }

        nfcActivity.setOnClickListener{
            requestPermissionLauncher.launch(permissionToRequestForNFC)
        }
    }

    private fun toggleBluetooth(){
        if(bluetoothAdapter.isEnabled){
            Toast.makeText(this, "Bluetooth is already enabled", Toast.LENGTH_SHORT).show()
        }else{
            val state = bluetoothAdapter.state
            Toast.makeText(this, "$state is state", Toast.LENGTH_SHORT).show()
            if(ContextCompat.checkSelfPermission(this, BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
            }else{
                Toast.makeText(this, "Bluetooth connect permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleWiFi() {
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
            Toast.makeText(this, "Wi-Fi Enabled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wi-Fi is already enabled", Toast.LENGTH_SHORT).show()
        }
    }

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if(result.resultCode == RESULT_OK){
            Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show()
        }
    }

}
