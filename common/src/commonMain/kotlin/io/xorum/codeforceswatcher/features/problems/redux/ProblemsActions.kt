package io.xorum.codeforceswatcher.features.problems.redux

import tw.geothings.rekotlin.Action

class ProblemsActions {

    data class ChangeTypeProblems(val isFavourite: Boolean) : Action
}
