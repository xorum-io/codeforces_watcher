package io.xorum.codeforceswatcher.util

import io.xorum.codeforceswatcher.features.problems.models.Problem

class ProblemsDiff(
        private val allProblems: List<Problem>,
        private val newProblems: List<Problem>
) {

    fun getDiff(): Pair<List<Problem>, List<Problem>> {
        val allProblemsSet = allProblems.map { it.id }.toHashSet()
        val newProblemsSet = newProblems.toSet()

        val diff = newProblemsSet.filterNot { problem -> allProblemsSet.contains(problem.id) }
        val toUpdateDiff = newProblemsSet.filter { problem -> allProblemsSet.contains(problem.id) }

        return Pair(diff, toUpdateDiff)
    }
}