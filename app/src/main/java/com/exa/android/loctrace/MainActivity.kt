package com.exa.android.loctrace

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.exa.android.loctrace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding:ActivityMainBinding ?=null
    private val binding get() = _binding!!
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment=supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment

        navController=navHostFragment.navController
        //navController.navigate(R.id.usersEntryFragment)

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}