package com.bogdan.codeforceswatcher.features.users

import com.google.firebase.auth.FirebaseAuth
import io.xorum.codeforceswatcher.features.auth.IFirebaseController
import kotlin.Exception

class FirebaseController : IFirebaseController {
    private val auth = FirebaseAuth.getInstance()

    override fun signIn(email: String, password: String, callback: (Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            callback(null)
        }.addOnFailureListener { e ->
            callback(e)
        }
    }

    override fun signUp(email: String, password: String, callback: (Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            callback(null)
        }.addOnFailureListener { e ->
            callback(e)
        }
    }

    override fun fetchToken(callback: (String?, Exception?) -> Unit) {
        val firebaseUser = auth.currentUser ?: run {
            callback(null, null)
            return@fetchToken
        }
        firebaseUser.getIdToken(false).addOnSuccessListener { task ->
            callback(task.token, null)
        }.addOnFailureListener { e ->
            callback(null, e)
        }
    }

    override fun logOut(callback: (Exception?) -> Unit) {
        auth.signOut()
        callback(null)
    }

    override fun sendPasswordReset(email: String, callback: (Exception?) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            callback(null)
        }.addOnFailureListener { e ->
            callback(e)
        }
    }
}