package com.exa.android.loctrace

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.exa.android.loctrace.databinding.FragmentUserLocationServiceBinding
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class UserLocationService : Fragment() {

    private  var _binding:FragmentUserLocationServiceBinding ?=null
    private val binding get() = _binding!!
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var debRef: DatabaseReference
    private lateinit var geoFire: GeoFire

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentUserLocationServiceBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        debRef= FirebaseDatabase.getInstance().reference.child("Locations")
        geoFire= GeoFire(debRef)

        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(requireContext())
        if(!isLocationEnable()){
            makeToast("Your location provider is turned off. Please turn it on.")
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }else{
            requestLocationPermission();

        }
    }
    private fun requestLocationPermission(){
        // request the permission in current context
        Dexter.withContext(requireContext())
            // below line is use to request the number of permissions which are required in our app.
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            // after adding permissions we are calling a listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // this method is called when all permissions are granted
                    if(report!!.areAllPermissionsGranted()){
                        makeToast("All the permissions are granted..")
                        requestLocationData()
                    }
                    // check for permanent denial of any permission
                    if(report.isAnyPermissionPermanentlyDenied()){
                        makeToast("You have denied location permission.Please enable them as it is mandatory for the app to work.")
                        // you can consider this : token?.continuePermissionRequest()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // this method is called when user grants some permission and denies some of them.


                }
            })
            //If you want to receive permission listener callbacks on the same thread that fired the permission request, you just need to call onSameThread before checking for permissions
            .onSameThread().check()
    }

    /*
   * @SuppressLint: This is an annotation provided by the Android framework specifically for suppressing lint warnings.
   * lint is a tool that scan code and identify the issue or any bugs.
   * "MissingPermission": This is the specific lint check that you're suppressing. In this case, it's warning about missing permissions. This lint check looks for cases where certain sensitive operations,
      such as accessing the user's location, are performed without the necessary permissions being declared in the manifest file or requested at runtime.
   * you're essentially telling the lint tool to ignore any warnings it might generate for this method related to missing permissions. This is commonly used when you're sure that the necessary permissions
     are already being handled properly elsewhere in the code or when you have some special handling for permissions that the lint tool doesn't recognize.
   */
    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest()
        //This line sets the priority of the location request to high accuracy.
        mLocationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }
    private val TAG="LocationService"
    private val mLocationCallback=object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation : Location? =locationResult.lastLocation
            val latitude=mLastLocation?.latitude
            val longitude=mLastLocation?.longitude
            Log.i("Longitude: ", longitude.toString())
            Log.i("Latitiue: ", latitude.toString())
            if (latitude != null && longitude != null) {
                Log.i(TAG, "$longitude *** $latitude")

                geoFire.setLocation("Kanhaiya", GeoLocation(latitude, longitude))
                geoFire.setLocation("Vishal", GeoLocation(23.258530, 77.407849))
                geoFire.setLocation("Ankit", GeoLocation(23.260587, 77.426248))

            }else{
                makeToast("Latitude and Longitude null found!")

            }
        }
    }
    private fun isLocationEnable():Boolean{
        val locationManager: LocationManager =requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun makeToast( text:String){
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}