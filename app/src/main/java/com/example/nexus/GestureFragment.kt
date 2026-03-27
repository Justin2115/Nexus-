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
    private var bedsListener: com.google.firebase.database.ValueEventListener? = null
    private var bed: com.example.nexus.data.BedStatus? = null

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
        
        bedsListener = RoomManager.listenToBeds { beds ->
            bed = beds.find { it.bedNumber == "101" }
        }

        binding.btnSwipeUp.setOnClickListener {
            bed?.let {
                RoomManager.addLog(it.bedNumber, "Gesture", "Swipe Up: Opening Medical Report")
                Toast.makeText(context, "Opening Medical Report...", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSwipeLeft.setOnClickListener {
            bed?.let {
                RoomManager.addLog(it.bedNumber, "Gesture", "Swipe Left: Muting Alarm")
                Toast.makeText(context, "Alarm Muted", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSwipeRight.setOnClickListener {
            bed?.let {
                it.lightOn = true
                RoomManager.updateBedStatus(it.bedNumber, mapOf("lightOn" to true))
                RoomManager.addLog(it.bedNumber, "Gesture", "Swipe Right: Lights ON")
                Toast.makeText(context, "Lights ON", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bedsListener?.let { RoomManager.removeListener(it) }
        _binding = null
    }
}
