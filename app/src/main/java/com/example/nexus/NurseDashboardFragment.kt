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

        adapter = BedAdapter(RoomManager.beds) { bed ->
            // Acknowledge logic
            bed.status = PatientStatus.NORMAL
            bed.fallAlert = false
            RoomManager.addLog(bed.bedNumber, "Acknowledge", "Nurse acknowledged status for Bed ${bed.bedNumber}")
            adapter.notifyDataSetChanged()
        }

        binding.rvBeds.layoutManager = LinearLayoutManager(context)
        binding.rvBeds.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this screen
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
