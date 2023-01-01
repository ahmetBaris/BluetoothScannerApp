package com.example.bluetooth1devam

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

// Konum izni sınıfı
class GPSUtils(context: Context) {
    private val TAG = "GPS"
    private val mContext : Context = context

    private var mSettingClient : SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    private var mLocationManager : LocationManager? = null
    private var mLocationRequest : LocationRequest? = null

    init {
        mLocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

        mSettingClient = LocationServices.getSettingsClient(mContext)


        mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000)
            .build();

        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = 1000
        mLocationRequest?.fastestInterval = 500

        if(mLocationRequest != null){
            val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest!!)
            mLocationSettingsRequest = builder.build()
        }
    }

    fun turnOnGPS(){
        if(mLocationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == false){
            mLocationSettingsRequest?.let {
                mSettingClient?.checkLocationSettings(it)
                    ?.addOnSuccessListener(mContext as Activity){
                        Log.d(TAG,"turnOnGPS already enabled")
                    }
                    ?.addOnFailureListener { ex ->
                        if((ex as ApiException).statusCode
                            == LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                            try {
                                val resolvableApiException = ex as ResolvableApiException
                                resolvableApiException.startResolutionForResult(mContext,IConstant.DEFAULTS.GPS_CODE)
                            }catch(_: java.lang.Exception){
                                Log.d(TAG,"TurnONGPS: Unable to start default functionality of GPS")
                            }
                        }else{
                            if(ex.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE){
                                val errorMessage = "Location settings are inadequate, and cannot be fixed here fix in settings"
                                Log.d(TAG, errorMessage)
                                Toast.makeText(mContext,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
            }
        }
    }
}