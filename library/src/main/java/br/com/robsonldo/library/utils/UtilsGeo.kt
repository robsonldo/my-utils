package br.com.robsonldo.library.utils

import kotlin.math.cos
import kotlin.math.sin

object UtilsGeo {
    fun distanceBetweenTwoPointsUnitSystem(lat1: Double, lon1: Double, lat2: Double,
                                           lon2: Double): Double {

        return distanceBetweenTwoPoints(lat1, lon1, lat2, lon2, 'K')
    }

    fun distanceBetweenTwoPoints(lat1: Double, lon1: Double, lat2: Double, lon2: Double,
                                 unit: Char): Double {

        val theta = lon1 - lon2
        var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2)) * Math.cos(deg2rad(theta)))

        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        if (unit == 'K') {
            dist *= 1.609344
        } else if (unit == 'N') {
            dist *= 0.8684
        }

        return dist
    }

    /* This function converts decimal degrees to radians */
    fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    /* This function converts radians to decimal degrees */
    fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}