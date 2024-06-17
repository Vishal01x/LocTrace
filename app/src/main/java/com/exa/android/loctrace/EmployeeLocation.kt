package com.exa.android.loctrace

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exa.android.loctrace.databinding.FragmentEmployesLocationBinding
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator


class EmployeeLocation : Fragment() , OnMapReadyCallback {

    private var _binding: FragmentEmployesLocationBinding ?=null
    private val binding get() = _binding!!
    private lateinit var debRef:DatabaseReference
    private lateinit var geoFire:GeoFire
    private lateinit var mMap: GoogleMap
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

        debRef=FirebaseDatabase.getInstance().reference.child("Locations")
        geoFire= GeoFire(debRef)
        // Get a handle to the fragment and register the callback.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)  // to set up an asynchronous callback.  you register the fragment as a callback to be notified when the map is ready to be used. This means that once the map has been initialized and is ready for use, the onMapReady(GoogleMap) method of this fragment will be called automatically.
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap=googleMap
        Log.d("map","map is ready")
        retrieveAndDisplayLocations()
        // Optionally move the camera to the marker
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, 10f))
    }
    private fun addLocationToMap(key: String, location: GeoLocation) {
        val latLng = LatLng(location.latitude, location.longitude)
        mMap.addMarker(MarkerOptions().position(latLng).title(key))
    }
    private fun retrieveAndDisplayLocations() {
        debRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                handleLocationChange(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                handleLocationChange(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Optionally handle when a location is removed
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Optionally handle when a location is moved
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("GeoFire", "Error retrieving locations: $error")
            }
        })
    }
    private fun handleLocationChange(snapshot: DataSnapshot) {
        val key = snapshot.key
        val location = snapshot.child("l").getValue(object : GenericTypeIndicator<List<Double>>() {})
        if (key != null && location != null && location.size == 2) {
            val geoLocation = GeoLocation(location[0], location[1])
            addLocationToMap(key, geoLocation)
            moveCameraToLocation(geoLocation)
            Log.d("emLoc", "$geoLocation $key")
        }
    }
    private fun moveCameraToLocation(location: GeoLocation) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f) // Adjust zoom level as needed
        mMap.animateCamera(cameraUpdate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}