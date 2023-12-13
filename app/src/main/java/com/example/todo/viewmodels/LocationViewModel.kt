package com.example.todo.viewmodels

import android.app.Application
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class LocationViewModel (app: Application): AndroidViewModel(app) {
    var location:Location? by mutableStateOf<Location?>(null)
        private set
    var latitude by mutableStateOf<Double?>(null)
        private set
    var longitude by mutableStateOf<Double?>(null)
        private set

    private fun _setLocation(newLocation: Location?) {
        location = newLocation
        latitude = location?.latitude
        longitude = location?.longitude
    }

    fun updateLocation(newLocation: Location) {
        _setLocation(newLocation)
    }

    fun invalidate() {
        _setLocation(null)
    }

    fun valid():Boolean {
        return (location != null)
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val locationA = Location("point A").apply {
            latitude = lat1
            longitude = lon1
        }
        val locationB = Location("point B").apply {
            latitude = lat2
            longitude = lon2
        }
        return locationA.distanceTo(locationB)

    }
}
