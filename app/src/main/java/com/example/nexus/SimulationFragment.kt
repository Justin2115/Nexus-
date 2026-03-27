package com.example.nexus

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nexus.data.PatientStatus
import com.example.nexus.data.RoomManager
import com.example.nexus.databinding.FragmentSimulationBinding

class SimulationFragment : Fragment() {

    private var _binding: FragmentSimulationBinding? = null
    private val binding get() = _binding!!
    private var alarmRingtone: Ringtone? = null
    private var bedsListener: com.google.firebase.database.ValueEventListener? = null
    private var bed: com.example.nexus.data.BedStatus? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimulationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        bedsListener = RoomManager.listenToBeds { beds ->
            bed = beds.find { it.bedNumber == "101" }
        }

        // Motion Simulation
        binding.btnLeftBed.setOnClickListener {
            bed?.let {
                it.motionActivity = "Left bed"
                it.status = PatientStatus.ATTENTION_NEEDED
                RoomManager.updateBedStatus(it.bedNumber, mapOf("motionActivity" to it.motionActivity, "status" to it.status.name))
                RoomManager.addLog(it.bedNumber, "Sensor", "Patient left bed")
            }
        }

        binding.btnNoMovement.setOnClickListener {
            bed?.let {
                it.motionActivity = "No movement"
                it.status = PatientStatus.ATTENTION_NEEDED
                RoomManager.updateBedStatus(it.bedNumber, mapOf("motionActivity" to it.motionActivity, "status" to it.status.name))
                RoomManager.addLog(it.bedNumber, "Sensor", "No movement detected for long time")
            }
        }

        binding.btnFallDetected.setOnClickListener {
            bed?.let {
                it.fallAlert = true
                it.status = PatientStatus.EMERGENCY
                RoomManager.updateBedStatus(it.bedNumber, mapOf("fallAlert" to true, "status" to it.status.name))
                RoomManager.addLog(it.bedNumber, "Emergency", "FALL DETECTED")
                startAlarm()
            }
        }

        binding.btnStopAlarm.setOnClickListener {
            stopAlarm()
        }

        // Gesture Simulation
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
                stopAlarm()
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

    private fun startAlarm() {
        if (alarmRingtone == null) {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            alarmRingtone = RingtoneManager.getRingtone(requireContext(), notification)
        }
        alarmRingtone?.play()
        binding.btnStopAlarm.visibility = View.VISIBLE
    }

    private fun stopAlarm() {
        alarmRingtone?.stop()
        binding.btnStopAlarm.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bedsListener?.let { RoomManager.removeListener(it) }
        stopAlarm()
        _binding = null
    }
}
