package com.myapplication.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
        } catch (e: Exception) {
            null
        }
    }

    fun isLocationInSchool(location: Location): Boolean {
        // Coordenadas de ejemplo de la escuela
        val schoolLat = 19.4326
        val schoolLng = -99.1332
        val radiusInMeters = 200.0

        val results = FloatArray(1)
        Location.distanceBetween(
            location.latitude, location.longitude,
            schoolLat, schoolLng,
            results
        )
        return results[0] <= radiusInMeters
    }
}
