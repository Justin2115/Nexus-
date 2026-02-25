package com.example.nexus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nexus.data.PatientStatus
import com.example.nexus.data.RoomManager
import com.example.nexus.databinding.FragmentRoomControlBinding

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
        
        val bed = RoomManager.beds.find { it.bedNumber == "101" } ?: return

        // Sync initial state
        binding.switchLights.isChecked = bed.lightOn
        binding.switchFan.isChecked = bed.fanOn
        binding.switchNightMode.isChecked = bed.nightMode

        binding.switchLights.setOnCheckedChangeListener { _, isChecked ->
            bed.lightOn = isChecked
            RoomManager.addLog(bed.bedNumber, "IoT Control", "Lights toggled: $isChecked")
        }

        binding.switchFan.setOnCheckedChangeListener { _, isChecked ->
            bed.fanOn = isChecked
            RoomManager.addLog(bed.bedNumber, "IoT Control", "Fan toggled: $isChecked")
        }

        binding.switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            bed.nightMode = isChecked
            RoomManager.addLog(bed.bedNumber, "IoT Control", "Night Mode toggled: $isChecked")
        }

        binding.btnEmergencyMode.setOnClickListener {
            bed.status = PatientStatus.EMERGENCY
            bed.lastRequest = "Manual Emergency Button"
            RoomManager.addLog(bed.bedNumber, "Emergency", "Manual Emergency Mode Triggered")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
