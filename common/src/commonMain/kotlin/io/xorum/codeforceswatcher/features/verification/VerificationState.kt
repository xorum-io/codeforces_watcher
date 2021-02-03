package io.xorum.codeforceswatcher.features.verification

import tw.geothings.rekotlin.StateType

data class VerificationState(
        val verificationCode: String? = null,
        val status: Status = Status.IDLE
) : StateType {

    enum class Status { IDLE, PENDING, DONE }
}
