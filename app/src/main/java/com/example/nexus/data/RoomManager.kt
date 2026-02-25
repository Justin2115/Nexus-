package com.example.nexus.data

enum class PatientStatus {
    NORMAL, ATTENTION_NEEDED, EMERGENCY
}

data class BedStatus(
    val id: Int,
    val bedNumber: String,
    var status: PatientStatus = PatientStatus.NORMAL,
    var lastRequest: String = "None",
    var motionActivity: String = "No motion",
    var fallAlert: Boolean = false,
    var lightOn: Boolean = false,
    var fanOn: Boolean = false,
    var nightMode: Boolean = false
)

data class MedicalLog(
    val timestamp: Long,
    val bedNumber: String,
    val eventType: String,
    val details: String
)

object RoomManager {
    val beds = mutableListOf(
        BedStatus(1, "101"),
        BedStatus(2, "102"),
        BedStatus(3, "103"),
        BedStatus(4, "104")
    )
    
    val logs = mutableListOf<MedicalLog>()
    
    fun addLog(bedNumber: String, eventType: String, details: String) {
        logs.add(0, MedicalLog(System.currentTimeMillis(), bedNumber, eventType, details))
    }
}
