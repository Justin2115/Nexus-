package com.example.nexus.data

import android.os.Handler
import android.os.Looper

object AlertManager {
    private val helpRequests = mutableListOf<Long>()
    private val handler = Handler(Looper.getMainLooper())

    fun registerHelpRequest(bedNumber: String) {
        val now = System.currentTimeMillis()
        helpRequests.add(now)
        
        // Remove requests older than 2 minutes
        helpRequests.removeAll { it < now - 120_000 }

        if (helpRequests.size >= 3) {
            triggerEmergency(bedNumber, "3 help requests within 2 minutes")
        }
    }

    private fun triggerEmergency(bedNumber: String, reason: String) {
        val updates = mapOf(
            "status" to PatientStatus.EMERGENCY,
            "lastRequest" to "SYSTEM ALERT: $reason"
        )
        RoomManager.updateBedStatus(bedNumber, updates)
        RoomManager.addLog(bedNumber, "Emergency", "System triggered emergency: $reason")
    }
}
