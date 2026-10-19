package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import com.example.model.CalculationMethod
import com.example.model.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone

class LocationHelper(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData? = withContext(Dispatchers.IO) {
        try {
            val cts = CancellationTokenSource()
            val task = fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)

            var location: Location? = null
            try {
                // Wait for task completion
                var attempts = 0
                while (!task.isComplete && attempts < 30) {
                    kotlinx.coroutines.delay(100)
                    attempts++
                }
                if (task.isSuccessful) {
                    location = task.result
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Fallback to LocationManager if fusedClient returned null
            if (location == null) {
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                val gpsLoc = lm?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val netLoc = lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                location = gpsLoc ?: netLoc
            }

            if (location != null) {
                return@withContext parseLocationToData(location)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

    private fun parseLocationToData(location: Location): LocationData {
        var cityNameAr = "موقعي الحالي"
        var cityNameEn = "Current Location"
        var countryNameAr = "موقعي"
        var countryNameEn = "Current"

        try {
            val geocoder = Geocoder(context, Locale("ar"))
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                cityNameAr = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "موقعي الحالي"
                countryNameAr = addr.countryName ?: "العالم"
            }

            val geocoderEn = Geocoder(context, Locale.ENGLISH)
            val addressesEn = geocoderEn.getFromLocation(location.latitude, location.longitude, 1)
            if (!addressesEn.isNullOrEmpty()) {
                val addrEn = addressesEn[0]
                cityNameEn = addrEn.locality ?: addrEn.subAdminArea ?: addrEn.adminArea ?: "Current Location"
                countryNameEn = addrEn.countryName ?: "Global"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val tz = TimeZone.getDefault().id

        // Auto select recommended calculation method based on country or coordinates
        val method = when {
            countryNameEn.contains("Saudi", true) -> CalculationMethod.UMM_AL_QURA
            countryNameEn.contains("Egypt", true) -> CalculationMethod.EGYPTIAN
            countryNameEn.contains("United Arab Emirates", true) -> CalculationMethod.DUBAI
            countryNameEn.contains("Kuwait", true) -> CalculationMethod.KUWAIT
            countryNameEn.contains("United States", true) || countryNameEn.contains("Canada", true) -> CalculationMethod.ISNA
            countryNameEn.contains("Pakistan", true) || countryNameEn.contains("India", true) -> CalculationMethod.KARACHI
            else -> CalculationMethod.MUSLIM_WORLD_LEAGUE
        }

        return LocationData(
            cityNameArabic = cityNameAr,
            cityNameEnglish = cityNameEn,
            countryNameArabic = countryNameAr,
            countryNameEnglish = countryNameEn,
            latitude = location.latitude,
            longitude = location.longitude,
            timeZoneId = tz,
            isAutoGps = true,
            defaultMethod = method
        )
    }
}
