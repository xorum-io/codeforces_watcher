package io.xorum.codeforceswatcher.features.verification.response

import kotlinx.serialization.Serializable

@Serializable
data class VerificationCodeResponse(val code: String)