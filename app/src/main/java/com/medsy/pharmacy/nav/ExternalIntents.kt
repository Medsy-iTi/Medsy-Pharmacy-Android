package com.medsy.pharmacy.nav

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.medsy.data.BuildConfig

fun Context.openDialer(phoneNumber: String) {
    startActivity(
        Intent(
            Intent.ACTION_DIAL,
            "tel:$phoneNumber".toUri(),
        )
    )
}

fun Context.openImage(url: String) {
    val finalUrl = if (url.startsWith("http")) url else BuildConfig.BASE_URL + url

    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            finalUrl.toUri(),
        )
    )
}

fun Context.openLocation(
    latitude: Double,
    longitude: Double,
) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            "geo:0,0?q=$latitude,$longitude(Location)".toUri()
        )
    )
}

fun Context.openDirections(
    latitude: Double,
    longitude: Double,
) {
    val destination = "$latitude,$longitude"
    val googleMapsIntent = Intent(
        Intent.ACTION_VIEW,
        "google.navigation:q=$destination".toUri(),
    ).setPackage(GOOGLE_MAPS_PACKAGE)

    try {
        startActivity(googleMapsIntent)
    } catch (_: ActivityNotFoundException) {
        val fallbackUri = Uri.Builder()
            .scheme("https")
            .authority("www.google.com")
            .appendPath("maps")
            .appendPath("dir")
            .appendPath("")
            .appendQueryParameter("api", "1")
            .appendQueryParameter("destination", destination)
            .build()
        startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
    }
}

private const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"
