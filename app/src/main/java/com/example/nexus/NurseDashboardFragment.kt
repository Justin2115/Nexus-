package com.example.nexus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nexus.data.PatientStatus
import com.example.nexus.data.RoomManager
import com.example.nexus.databinding.FragmentNurseDashboardBinding

class NurseDashboardFragment : Fragment() {

    private var _binding: FragmentNurseDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BedAdapter
    private var bedsListener: com.google.firebase.database.ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNurseDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBeds.layoutManager = LinearLayoutManager(context)
        
        // Initialize adapter immediately with existing data (or empty) to avoid "No adapter attached"
        adapter = BedAdapter(RoomManager.beds) { bed ->
            val updates = mapOf(
                "status" to PatientStatus.NORMAL.name,
                "fallAlert" to false
            )
            RoomManager.updateBedStatus(bed.bedNumber, updates)
            RoomManager.addLog(bed.bedNumber, "Acknowledge", "Nurse acknowledged status for Bed ${bed.bedNumber}")
        }
        binding.rvBeds.adapter = adapter
        
        bedsListener = RoomManager.listenToBeds { beds ->
            if (_binding == null) return@listenToBeds
            adapter.updateData(beds)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this screen if adapter is ready
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bedsListener?.let { RoomManager.removeListener(it) }
        _binding = null
    }
}
