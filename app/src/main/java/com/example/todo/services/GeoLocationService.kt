package com.example.todo.services

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.util.Log
import com.example.todo.MyNotification
import com.example.todo.data.TodoDatabase
import com.example.todo.viewmodels.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@Suppress("StaticFieldLeak")
object GeoLocationService: LocationListener {
    var locationViewModel: LocationViewModel? = null
    private var context: Context? = null
    private var todoDatabase: TodoDatabase? = null

    fun initialiseService(ctx: Context) {
        val appContext = ctx.applicationContext
        context = appContext
        todoDatabase = TodoDatabase.getInstance(appContext)
    }

    override fun onLocationChanged(newLocation: Location) {
        locationViewModel?.updateLocation( newLocation )
        Log.i("geolocation", "Location updated")
        checkNearbyTodos(newLocation)
    }

    fun updateLatestLocation(latestLocation: Location) {
        locationViewModel?.updateLocation( latestLocation )
        Log.i("geolocation", "Location set to latest")
    }

    private fun checkNearbyTodos(currentLocation: Location) {
        val localContext = context
        if (localContext == null) {
            Log.e("GeoLocationService", "Context is not initialized.")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            todoDatabase?.todoDAO()?.getAllTodos()?.collect { todos ->
                todos.forEach { todo ->
                    val latitude = todo.latitude
                    val longitude = todo.longitude
                    if (latitude == null || longitude == null) {
                        return@forEach // skip the current iteration in the forEach loop
                    }
                    // latitude and longitude are not null
                    val todoLocation = Location("").apply {
                        this.latitude = latitude
                        this.longitude = longitude
                    }
                    val distance = currentLocation.distanceTo(todoLocation)
                    if (distance < 500) {
                        context?.let { ctx ->
                            MyNotification(ctx, "Nearby TODO", "You are within 500m of ${todo.title}").forNotification()
                        }
                    }
                }
            }
        }
    }


}
