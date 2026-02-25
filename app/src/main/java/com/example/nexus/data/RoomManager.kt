package com.example.nexus.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

enum class PatientStatus {
    NORMAL, ATTENTION_NEEDED, EMERGENCY
}

data class BedStatus(
    val id: Int = 0,
    val bedNumber: String = "",
    var status: PatientStatus = PatientStatus.NORMAL,
    var lastRequest: String = "None",
    var motionActivity: String = "No motion",
    var fallAlert: Boolean = false,
    var lightOn: Boolean = false,
    var fanOn: Boolean = false,
    var nightMode: Boolean = false
)

data class MedicalLog(
    val timestamp: Long = 0,
    val bedNumber: String = "",
    val eventType: String = "",
    val details: String = ""
)

object RoomManager {
    private val database = FirebaseDatabase.getInstance().reference
    private val bedsRef = database.child("beds")
    private val logsRef = database.child("logs")

    val beds = mutableListOf<BedStatus>()
    val logs = mutableListOf<MedicalLog>()

    init {
        // Initialize local beds if empty
        if (beds.isEmpty()) {
            val initialBeds = listOf(
                BedStatus(1, "101"),
                BedStatus(2, "102"),
                BedStatus(3, "103"),
                BedStatus(4, "104")
            )
            initialBeds.forEach { bed ->
                bedsRef.child(bed.bedNumber).get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        bedsRef.child(bed.bedNumber).setValue(bed)
                    }
                }
            }
        }
    }

    fun updateBedStatus(bedNumber: String, updates: Map<String, Any>) {
        bedsRef.child(bedNumber).updateChildren(updates)
    }

    fun addLog(bedNumber: String, eventType: String, details: String) {
        val log = MedicalLog(System.currentTimeMillis(), bedNumber, eventType, details)
        logsRef.push().setValue(log)
    }
    
    fun listenToBeds(callback: (List<BedStatus>) -> Unit) {
        bedsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedBeds = mutableListOf<BedStatus>()
                for (bedSnapshot in snapshot.children) {
                    val bed = bedSnapshot.getValue(BedStatus::class.java)
                    bed?.let { updatedBeds.add(it) }
                }
                beds.clear()
                beds.addAll(updatedBeds)
                callback(updatedBeds)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
