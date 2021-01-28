package io.xorum.codeforceswatcher.features.auth

import tw.geothings.rekotlin.StateType

data class AuthState(
        val signInStatus: Status = Status.IDLE,
        val signUpStatus: Status = Status.IDLE
) : StateType {

    enum class Status { IDLE, PENDING, DONE }
}
