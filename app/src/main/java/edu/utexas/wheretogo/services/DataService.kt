package edu.utexas.wheretogo.services

import com.google.firebase.storage.FirebaseStorage

class DataService {

    private var storage: FirebaseStorage = FirebaseStorage.getInstance()

    var dataReference = "heatmaps/ny.json"

    fun setDataReference(isOptimized: Boolean) {
        dataReference = if (isOptimized) "heatmaps/newyork.json" else "heatmaps/ny.json"
    }

    fun downloadData(success: (String) -> Unit, failure: (Exception) -> Unit) {
        val storageRef = storage.reference.child(dataReference)

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            val dataString = String(bytes, Charsets.UTF_8)
            success(dataString)
        }.addOnFailureListener {
            failure(it)
        }
    }
}
