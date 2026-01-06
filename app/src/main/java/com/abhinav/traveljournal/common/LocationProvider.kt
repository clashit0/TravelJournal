package com.abhinav.traveljournal.common

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

class LocationProvider (context: Context){
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onResult: (Double?,Double?) -> Unit
    ){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                onResult(location?.latitude,location?.longitude)
            }
            .addOnFailureListener {
                onResult(null,null)
            }
    }
}