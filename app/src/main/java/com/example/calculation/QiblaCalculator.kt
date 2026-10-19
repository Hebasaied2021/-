package com.example.calculation

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object QiblaCalculator {
    // Holy Kaaba in Makkah, Saudi Arabia
    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206

    /**
     * Calculates the Qibla bearing in degrees (0..360) clockwise from true North
     */
    fun calculateBearing(latitude: Double, longitude: Double): Double {
        val userLatRad = Math.toRadians(latitude)
        val userLngRad = Math.toRadians(longitude)
        val kaabaLatRad = Math.toRadians(KAABA_LATITUDE)
        val kaabaLngRad = Math.toRadians(KAABA_LONGITUDE)

        val deltaLng = kaabaLngRad - userLngRad

        val y = sin(deltaLng) * cos(kaabaLatRad)
        val x = cos(userLatRad) * sin(kaabaLatRad) - sin(userLatRad) * cos(kaabaLatRad) * cos(deltaLng)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360.0) % 360.0
        return bearing
    }

    /**
     * Calculates distance to the Holy Kaaba in kilometers using Haversine formula
     */
    fun calculateDistanceKm(latitude: Double, longitude: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(KAABA_LATITUDE - latitude)
        val dLng = Math.toRadians(KAABA_LONGITUDE - longitude)

        val lat1 = Math.toRadians(latitude)
        val lat2 = Math.toRadians(KAABA_LATITUDE)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) * sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }

    /**
     * Checks if current compass heading aligns with the Qibla within tolerance (default ±3 degrees)
     */
    fun isAligned(currentHeading: Float, qiblaBearing: Double, toleranceDegrees: Float = 3.5f): Boolean {
        val diff = Math.abs(currentHeading - qiblaBearing.toFloat()) % 360f
        val shortestDiff = if (diff > 180f) 360f - diff else diff
        return shortestDiff <= toleranceDegrees
    }
}
