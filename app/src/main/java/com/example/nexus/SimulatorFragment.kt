package com.example.nexus

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nexus.data.PatientStatus
import com.example.nexus.data.RoomManager
import com.example.nexus.databinding.FragmentSimulatorBinding

class SimulatorFragment : Fragment() {

    private var _binding: FragmentSimulatorBinding? = null
    private val binding get() = _binding!!
    private var alarmRingtone: Ringtone? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimulatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val bed = RoomManager.beds.find { it.bedNumber == "101" } ?: return

        binding.btnLeftBed.setOnClickListener {
            bed.motionActivity = "Left bed"
            bed.status = PatientStatus.ATTENTION_NEEDED
            RoomManager.addLog(bed.bedNumber, "Sensor", "Patient left bed")
        }

        binding.btnNoMovement.setOnClickListener {
            bed.motionActivity = "No movement"
            bed.status = PatientStatus.ATTENTION_NEEDED
            RoomManager.addLog(bed.bedNumber, "Sensor", "No movement detected for long time")
        }

        binding.btnFallDetected.setOnClickListener {
            bed.fallAlert = true
            bed.status = PatientStatus.EMERGENCY
            RoomManager.addLog(bed.bedNumber, "Emergency", "FALL DETECTED")
            startAlarm()
        }

        binding.btnStopAlarm.setOnClickListener {
            stopAlarm()
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
        stopAlarm()
        _binding = null
    }
}
