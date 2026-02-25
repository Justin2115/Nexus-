package com.example.nexus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nexus.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigation

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_nurse -> {
                    loadFragment(NurseDashboardFragment())
                    true
                }
                R.id.navigation_patient -> {
                    loadFragment(PatientAssistantFragment())
                    true
                }
                R.id.navigation_controls -> {
                    loadFragment(RoomControlFragment())
                    true
                }
                R.id.navigation_simulator -> {
                    loadFragment(SimulationFragment())
                    true
                }
                R.id.navigation_logs -> {
                    loadFragment(LogFragment())
                    true
                }
                else -> false
            }
        }

        // Set default fragment
        if (savedInstanceState == null) {
            navView.selectedItemId = R.id.navigation_nurse
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
