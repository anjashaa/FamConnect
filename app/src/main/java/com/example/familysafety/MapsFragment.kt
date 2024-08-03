package com.example.familysafety

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.util.Log

class MapsFragment : Fragment() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private val callback = OnMapReadyCallback { googleMap ->
        val mitManipal = LatLng(13.3525, 74.7925) // MIT, Manipal coordinates
        googleMap.addMarker(MarkerOptions().position(mitManipal).title("Marker in MIT, Manipal"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mitManipal, 16f)) // Zoom level set to 16

        // Check if the ACCESS_FINE_LOCATION permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Permission is granted, try enabling the My Location feature
                googleMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                // Permission is granted but a SecurityException occurred while enabling My Location
                // Handle the SecurityException here, log a message, or take appropriate action
                Log.e("MapsFragment", "SecurityException occurred while enabling My Location: ${e.message}")
            }
        } else {
            // Permission is not granted, request the permission
            requestLocationPermission()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mapss, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun requestLocationPermission() {
        // Request the ACCESS_FINE_LOCATION permission
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, refresh the map to enable My Location
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                mapFragment?.getMapAsync(callback)
            } else {
                // Permission is denied, handle it appropriately
                // For demonstration, let's just log a message
                Log.e("MapsFragment", "Location permission is denied.")
            }
        }
    }
}