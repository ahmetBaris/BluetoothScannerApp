package com.example.bluetooth1devam

//noinspection SuspiciousImport
import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetooth1devam.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var devices = mutableListOf<BluetoothDevice>()
    private val TAG = "MAIN_ACTIVITY"
    lateinit var binding: ActivityMainBinding
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothManager: BluetoothManager
    lateinit var receiver: BluetoothReceiver
    val REQUEST_ACCESS_COARSE_LOCATION = 1


    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Android 11de konum ile alakalı tarama için gerekli olan izin kısımları
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        //konum izni için çağrılan sınıf
        //konum sınıfımız yazmamızın sebebi android 11de konumsuz bluetooth tarama(scan) işlemi yapmıyor
        if (android.os.Build.VERSION.SDK_INT==11){
            GPSUtils(this).turnOnGPS()
        }


        //bluetooth işlemleri manager , adapter
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        //val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))


        // android 12 ve 13 için nearby devices izni
        // bu kısım olmazsa app anında çöküyor.

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 2)
                return
            }
        }




        /*
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED){
            if (!bluetoothAdapter.isEnabled) {
                bluetoothAdapter.enable()
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(intent)

            }
        }

         */

            //bu kısımda uygulama açılır açılmaz bluetooth scan işlemimizi yapıyoruz.
                when (ContextCompat.checkSelfPermission(
                    baseContext, Manifest.permission.ACCESS_COARSE_LOCATION
                )) {
                    PackageManager.PERMISSION_DENIED -> androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Runtime Permission")
                        .setMessage("Give Permission")
                        .setNeutralButton("Okay", DialogInterface.OnClickListener { dialog, which ->
                            if (ContextCompat.checkSelfPermission(
                                    baseContext,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) !=
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                                    REQUEST_ACCESS_COARSE_LOCATION
                                )
                            }
                        })
                        .show()
                        .findViewById<TextView>(R.id.message)!!.movementMethod =
                        LinkMovementMethod.getInstance()

                    PackageManager.PERMISSION_GRANTED -> {
                        Log.d("discoverDevices", "Permission Granted")
                    }
                }
                //bluetooth tarama için gerekli fonksiyon
                discoverDevices()

        recyclerview.layoutManager =  LinearLayoutManager(this)
}

    //Bu kısımda konum iznimizde hayır kısmına tıklarsak onu tekrar tekrar çağırıyor.
    //Konuma izin vermeden işlem olmadığı için burası yapılabilir.
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            if (resultCode == IConstant.DEFAULTS.GPS_CODE){
                Log.d(TAG,"onActivityResult: Success")
            }else{
                GPSUtils(this).turnOnGPS()
            }
        }else{
            GPSUtils(this).turnOnGPS()
        }
    }

    //bluetooh gerekli fonksiyon
    @SuppressLint("MissingPermission")
    private fun discoverDevices() {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(discoverDeviceReceiver,filter)
        bluetoothAdapter.startDiscovery()
    }
    //bluetooth gerekli fonksiyon
    private val discoverDeviceReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            var action = ""
            if(intent!=null){
                action = intent.action.toString()
            }
            when(action){
                BluetoothAdapter.ACTION_STATE_CHANGED ->{
                    Log.d("discoverDevices1","STATE CHANGED")
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED ->{
                    Log.d("discoverDevices2","Discovery Started")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->{
                    Log.d("discoverDevices3","Discovery Finished")
                }
                BluetoothDevice.ACTION_FOUND ->{
                    val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if(device!=null){
                        //Log.e("discoverDevices4","${device.name}  ${device.address}")

                        //Bluetooth adapter kısmımız device işlemleri
                        devices.add(device)
                        recyclerview.adapter = BluetoothAdapter2(devices)
                        Log.e("discoverDevices4","$device")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}