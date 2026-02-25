package com.example.nexus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nexus.data.RoomManager
import com.example.nexus.databinding.FragmentGestureBinding

class GestureFragment : Fragment() {

    private var _binding: FragmentGestureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val bed = RoomManager.beds.find { it.bedNumber == "101" } ?: return

        binding.btnSwipeUp.setOnClickListener {
            RoomManager.addLog(bed.bedNumber, "Gesture", "Swipe Up: Opening Medical Report")
            Toast.makeText(context, "Opening Medical Report...", Toast.LENGTH_SHORT).show()
            // We'll show a simple dialog or toast for report
        }

        binding.btnSwipeLeft.setOnClickListener {
            RoomManager.addLog(bed.bedNumber, "Gesture", "Swipe Left: Muting Alarm")
            Toast.makeText(context, "Alarm Muted", Toast.LENGTH_SHORT).show()
            // Logic to mute alarm could go here if we had a global alarm manager
        }

        binding.btnSwipeRight.setOnClickListener {
            bed.lightOn = true
            RoomManager.addLog(bed.bedNumber, "Gesture", "Swipe Right: Lights ON")
            Toast.makeText(context, "Lights ON", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
