package com.exa.android.loctrace

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exa.android.loctrace.databinding.FragmentEmployesLocationBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class EmployeeLocation : Fragment() , OnMapReadyCallback {

    private var _binding: FragmentEmployesLocationBinding ?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentEmployesLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get a handle to the fragment and register the callback.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)  // to set up an asynchronous callback.  you register the fragment as a callback to be notified when the map is ready to be used. This means that once the map has been initialized and is ready for use, the onMapReady(GoogleMap) method of this fragment will be called automatically.
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("map","map is ready")
        val markerLocation = LatLng(0.0, 0.0)
        googleMap.addMarker(
            MarkerOptions()
                .position(markerLocation)
                .title("Marker")
        )

        // Optionally move the camera to the marker
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, 10f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}