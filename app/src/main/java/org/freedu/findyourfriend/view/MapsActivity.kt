package org.freedu.findyourfriend.view

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.freedu.findyourfriend.R
import org.freedu.findyourfriend.databinding.ActivityMapsBinding
import org.freedu.findyourfriend.viewmodel.FirestoreViewModel
import java.io.IOException
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var firestoreViewModel: FirestoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel::class.java)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnZoomIn.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomIn())

        }
        binding.btnZoomOut.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomOut())
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        firestoreViewModel.getAllUsers(this) {
            for (user in it) {
                val userLocation = user.location
                val latLng = parseLocation(userLocation)
                if (userLocation.isEmpty()||userLocation == "Don't found any location yet"||userLocation == "Location not available"||userLocation == "Unknown Location") {
                    LatLng(37.4220936, -122.083922)
                }
                val markerOptions = MarkerOptions().position(latLng).title(user.displayName)
                googleMap.addMarker(markerOptions)

                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                googleMap.animateCamera(cameraUpdate)
            }
        }
    }

    private fun parseLocation(location: String): LatLng {
        val coordinates = getLatLongFromLocationName(location)
        if (coordinates != null) {
            val latitude = coordinates.first
            val longitude = coordinates.second
            return LatLng(latitude, longitude)
        } else {
            return LatLng(37.4220936, -122.083922)
        }

    }

    private fun getLatLongFromLocationName(locationName: String): Pair<Double, Double>? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList: List<Address>? = geocoder.getFromLocationName(locationName, 1)
            if (!addressList.isNullOrEmpty()) {
                val address: Address = addressList[0]
                return Pair(address.latitude, address.longitude)  // Return latitude and longitude as a Pair
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null  // Return null if no coordinates were found
    }
}