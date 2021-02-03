package io.xorum.codeforceswatcher.features.verification

import kotlinx.serialization.Serializable

@Serializable
data class VerificationCodeResponse(val code: String)