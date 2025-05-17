package edu.utexas.wheretogo.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseService {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(email: String, password: String, completion: (FirebaseUser?, Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    completion(user, null)
                } else {
                    completion(null, task.exception)
                }
            }
    }

    fun loginUser(email: String, password: String, completion: (FirebaseUser?, Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    completion(user, null)
                } else {
                    completion(null, task.exception)
                }
            }
    }

    fun sendPasswordResetEmail(email: String, completion: (Boolean, Exception?) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                completion(true, null)
            } else {
                completion(false, task.exception)
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
