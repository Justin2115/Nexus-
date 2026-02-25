package com.example.nexus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nexus.data.MedicalLog
import com.example.nexus.databinding.ItemLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogAdapter(private var logs: List<MedicalLog>) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    inner class LogViewHolder(val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val time = sdf.format(Date(log.timestamp))
        
        with(holder.binding) {
            tvLogTime.text = time
            tvLogBed.text = "Bed ${log.bedNumber}"
            tvLogType.text = log.eventType
            tvLogDetails.text = log.details
        }
    }

    override fun getItemCount() = logs.size

    fun updateData(newLogs: List<MedicalLog>) {
        this.logs = newLogs
        notifyDataSetChanged()
    }
}
