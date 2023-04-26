package com.krisna.storycircle.presentation.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.krisna.storycircle.R
import com.krisna.storycircle.databinding.FragmentMapsBinding
import com.krisna.storycircle.presentation.viewmodel.StoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : Fragment() {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var mapView: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val storyViewModel: StoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mapView = googleMap

            try {
                val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.custom_map_style
                    )
                )
                if (!success) {
                    Log.e("mapsError : ", "Custom map style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Log.e("mapsError : ", "Can't find style. Error: ", e)
            }

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mapView.isMyLocationEnabled = true
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    mapView.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM_LEVEL)
                    )
                }
            }

            vmObserver()
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Map"
            setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun vmObserver() {
        val bearerToken = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
            .getString("bearerToken", "") ?: ""
        storyViewModel.getAllStoryWithLoc(bearerToken, null, null, 1)
        storyViewModel.listStory.observe(viewLifecycleOwner) { storyData ->
            storyData?.forEach { story ->
                story?.lat?.let { lat ->
                    story.lon.let { lon ->
                        val latLng = LatLng(lat, lon)
                        mapView.addMarker(MarkerOptions().position(latLng).title(story.name))
                    }
                }
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val DEFAULT_ZOOM_LEVEL = 5f
    }
}