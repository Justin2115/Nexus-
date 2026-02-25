package com.example.nexus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.nexus.data.PatientStatus
import com.example.nexus.data.RoomManager
import com.example.nexus.databinding.FragmentRoomControlBinding
import com.google.firebase.auth.FirebaseAuth

class RoomControlFragment : Fragment() {

    private var _binding: FragmentRoomControlBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val bedNumber = "101" // Simulation for Bed 101
        
        // Sync with Firebase
        RoomManager.listenToBeds { beds ->
            val bed = beds.find { it.bedNumber == bedNumber } ?: return@listenToBeds
            if (_binding == null) return@listenToBeds
            
            binding.switchLights.isChecked = bed.lightOn
            binding.switchFan.isChecked = bed.fanOn
        }

        binding.switchLights.setOnCheckedChangeListener { _, isChecked ->
            RoomManager.updateBedStatus(bedNumber, mapOf("lightOn" to isChecked))
            RoomManager.addLog(bedNumber, "IoT Control", "Lights toggled: $isChecked")
        }

        binding.switchFan.setOnCheckedChangeListener { _, isChecked ->
            RoomManager.updateBedStatus(bedNumber, mapOf("fanOn" to isChecked))
            RoomManager.addLog(bedNumber, "IoT Control", "Fan toggled: $isChecked")
        }

        binding.btnEmergencyMode.setOnClickListener {
            val updates = mapOf(
                "status" to PatientStatus.EMERGENCY,
                "lastRequest" to "Manual Emergency Button"
            )
            RoomManager.updateBedStatus(bedNumber, updates)
            RoomManager.addLog(bedNumber, "Emergency", "Manual Emergency Mode Triggered")
            Toast.makeText(context, "Emergency Alert Sent!", Toast.LENGTH_SHORT).show()
        }

        // --- Settings Logic ---
        
        val sharedPref = requireActivity().getSharedPreferences("NexusSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)
        binding.darkModeSwitch.isChecked = isDarkMode

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        binding.profileButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(context, "User: ${user?.email}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
