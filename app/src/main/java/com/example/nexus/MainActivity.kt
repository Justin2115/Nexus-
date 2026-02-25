package com.example.nexus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nexus.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

import android.view.Menu
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigation

        // Handle role-based visibility
        val userEmail = auth.currentUser?.email ?: ""
        val isNurse = userEmail.contains("admin") || userEmail.contains("nurse")
        
        if (!isNurse) {
            // If not a nurse/admin, hide Nurse Dashboard and Logs
            navView.menu.findItem(R.id.navigation_nurse).isVisible = false
            navView.menu.findItem(R.id.navigation_logs).isVisible = false
            navView.selectedItemId = R.id.navigation_patient
            loadFragment(PatientAssistantFragment())
        } else {
            // Default for Nurse
            navView.selectedItemId = R.id.navigation_nurse
            loadFragment(NurseDashboardFragment())
        }

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
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
