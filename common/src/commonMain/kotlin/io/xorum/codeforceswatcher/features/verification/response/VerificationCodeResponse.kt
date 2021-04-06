package io.xorum.codeforceswatcher.features.verification.response

import kotlinx.serialization.Serializable

@Serializable
internal data class VerificationCodeResponse(val code: String)