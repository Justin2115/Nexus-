package com.example.nexus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nexus.data.BedStatus
import com.example.nexus.data.PatientStatus
import com.example.nexus.databinding.ItemBedCardBinding

class BedAdapter(
    private var beds: List<BedStatus>,
    private val onAcknowledge: (BedStatus) -> Unit
) : RecyclerView.Adapter<BedAdapter.BedViewHolder>() {

    inner class BedViewHolder(val binding: ItemBedCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BedViewHolder {
        val binding = ItemBedCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BedViewHolder, position: Int) {
        val bed = beds[position]
        with(holder.binding) {
            tvBedNumber.text = "Bed ${bed.bedNumber}"
            tvPatientStatus.text = bed.status.name
            tvLastRequest.text = bed.lastRequest
            tvMotionActivity.text = bed.motionActivity
            
            tvFallAlert.visibility = if (bed.fallAlert) View.VISIBLE else View.GONE
            
            // Color coding based on status
            val cardColor = when (bed.status) {
                PatientStatus.EMERGENCY -> R.color.emergency_red
                PatientStatus.ATTENTION_NEEDED -> R.color.attention_orange
                else -> R.color.white
            }
            
            val textColor = if (bed.status == PatientStatus.EMERGENCY) R.color.white else R.color.black
            
            bedCard.setCardBackgroundColor(ContextCompat.getColor(root.context, cardColor))
            tvBedNumber.setTextColor(ContextCompat.getColor(root.context, textColor))
            tvPatientStatus.setTextColor(ContextCompat.getColor(root.context, textColor))
            tvLastRequest.setTextColor(ContextCompat.getColor(root.context, textColor))
            tvMotionActivity.setTextColor(ContextCompat.getColor(root.context, textColor))
            tvStatusLabel.setTextColor(ContextCompat.getColor(root.context, textColor))
            tvLastRequestLabel.setTextColor(ContextCompat.getColor(root.context, textColor))
            tvMotionLabel.setTextColor(ContextCompat.getColor(root.context, textColor))

            btnAcknowledge.setOnClickListener { onAcknowledge(bed) }
        }
    }

    override fun getItemCount() = beds.size

    fun updateData(newBeds: List<BedStatus>) {
        this.beds = newBeds
        notifyDataSetChanged()
    }
}
