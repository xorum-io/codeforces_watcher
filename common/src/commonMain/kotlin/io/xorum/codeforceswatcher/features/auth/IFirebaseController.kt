package io.xorum.codeforceswatcher.features.auth

interface IFirebaseController {

    fun signIn(email: String, password: String, callback: (String?, Exception?) -> Unit)

    fun signUp(email: String, password: String, callback: (String?, Exception?) -> Unit)

    fun fetchToken(callback: (String?, Exception?) -> Unit)
}