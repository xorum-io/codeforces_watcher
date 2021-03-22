package com.bogdan.codeforceswatcher.features.users

import com.google.firebase.auth.FirebaseAuth
import io.xorum.codeforceswatcher.features.auth.IFirebaseController
import kotlin.Exception

class FirebaseController : IFirebaseController {
    private val auth = FirebaseAuth.getInstance()

    override fun signIn(email: String, password: String, callback: (String?, Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            fetchToken { token, e ->
                token?.let { callback(it, null) } ?: callback(null, e)
            }
        }.addOnFailureListener { e ->
            callback(null, e)
        }
    }

    override fun signUp(email: String, password: String, callback: (String?, Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            fetchToken { token, e ->
                token?.let { callback(it, null) } ?: callback(null, e)
            }
        }.addOnFailureListener { e ->
            callback(null, e)
        }
    }

    override fun fetchToken(callback: (String?, Exception?) -> Unit) {
        val firebaseUser = auth.currentUser ?: run {
            callback(null, null)
            return@fetchToken
        }
        firebaseUser.getIdToken(false).addOnCompleteListener { task ->
            callback(task.result?.token, null)
        }.addOnFailureListener { e ->
            callback(null, e)
        }
    }

    override fun logOut(callback: (Exception?) -> Unit) {
        auth.signOut()
        callback(null)
    }

    override fun sendPasswordReset(email: String, callback: (Exception?) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            callback(null)
        }.addOnFailureListener { e ->
            callback(e)
        }
    }
}