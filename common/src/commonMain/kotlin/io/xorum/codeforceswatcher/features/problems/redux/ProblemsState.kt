package io.xorum.codeforceswatcher.features.problems.redux

import io.xorum.codeforceswatcher.features.problems.models.Problem
import tw.geothings.rekotlin.StateType

data class ProblemsState(
        val problems: List<Problem> = listOf(),
        val status: Status = Status.IDLE,
        val isFavourite: Boolean = false,
        val tags: List<String> = listOf()
) : StateType {

    enum class Status { IDLE, PENDING }
}
