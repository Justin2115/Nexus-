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

    val beds = mutableListOf<BedStatus>().apply {
        add(BedStatus(1, "101"))
        add(BedStatus(2, "102"))
        add(BedStatus(3, "103"))
        add(BedStatus(4, "104"))
    }
    val logs = mutableListOf<MedicalLog>()

    init {
        bedsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                    val initialBeds = listOf(
                        BedStatus(1, "101"),
                        BedStatus(2, "102"),
                        BedStatus(3, "103"),
                        BedStatus(4, "104")
                    )
                    initialBeds.forEach { bed ->
                        bedsRef.child(bed.bedNumber).setValue(bed)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun updateBedStatus(bedNumber: String, updates: Map<String, Any>) {
        bedsRef.child(bedNumber).updateChildren(updates)
    }

    fun addLog(bedNumber: String, eventType: String, details: String) {
        val log = MedicalLog(System.currentTimeMillis(), bedNumber, eventType, details)
        logsRef.push().setValue(log)
    }
    
    fun listenToBeds(callback: (List<BedStatus>) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
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
        }
        bedsRef.addValueEventListener(listener)
        return listener
    }

    fun removeListener(listener: ValueEventListener) {
        bedsRef.removeEventListener(listener)
    }
}
