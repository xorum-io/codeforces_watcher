package io.xorum.codeforceswatcher.features.problems.redux.requests

import io.xorum.codeforceswatcher.db.DatabaseQueries
import io.xorum.codeforceswatcher.features.problems.models.Problem
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.util.RefreshScreen
import kotlinx.coroutines.delay
import tw.geothings.rekotlin.Action

class ProblemsRequests {

    class FetchProblems(
            private val isInitializedByUser: Boolean
    ) : Request() {

        override suspend fun execute() {
            if (!isInitializedByUser) delay(1000)

            val problemsEn = codeforcesRepository.getProblems("en")?.result?.problems
            val problemsRu = codeforcesRepository.getProblems("ru")?.result?.problems

            if (problemsEn == null || problemsRu == null) {
                dispatchFailure()
            } else {
                if (isProblemsMatching(problemsEn, problemsRu)) {
                    val problems = mergeProblems(problemsEn, problemsRu)

                    boundProblemsToContests(problems)
                    updateDatabase(problems)

                    store.dispatch(Success(problems))
                } else {
                    dispatchFailure()
                }
            }
        }

        private fun dispatchFailure() {
            val noConnectionError = if (isInitializedByUser) Message.NoConnection else Message.None
            store.dispatch(Failure(noConnectionError))
        }

        private fun mergeProblems(problemsEn: List<Problem>, problemsRu: List<Problem>): List<Problem> {
            val problems: MutableList<Problem> = mutableListOf()
            for ((index, problem) in problemsEn.withIndex()) {
                problem.ruName = problemsRu[index].name
                problem.enName = problem.name
                problems.add(problem)
            }
            return problems
        }

        private fun boundProblemsToContests(problems: List<Problem>) {
            val contests = store.state.contests.contests
            val mapContests = contests.associateBy { contest -> contest.id }
            problems.forEach { problem ->
                problem.contestName = mapContests[problem.contestId]?.name.orEmpty()
                problem.contestTime = mapContests[problem.contestId]?.startTimeSeconds ?: 0
            }
        }

        private fun isProblemsMatching(problemsEn: List<Problem>, problemsRu: List<Problem>): Boolean {
            for ((index, problem) in problemsEn.withIndex()) {
                if (problem.contestId != problemsRu[index].contestId ||
                        problem.index != problemsRu[index].index) {
                    return false
                }
            }
            return true
        }

        private fun updateDatabase(newProblems: List<Problem>) {
            val problems = DatabaseQueries.Problems.getAll()
            val favouriteProblemsMap = problems.associate { problem -> problem.identify() to problem.isFavourite }
            DatabaseQueries.Problems.deleteAll()

            newProblems.forEach { problem ->
                problem.isFavourite = favouriteProblemsMap[problem.identify()] ?: false
            }

            val identifiers = DatabaseQueries.Problems.insert(newProblems)
            newProblems.forEachIndexed { index, problem -> problem.id = identifiers[index] }
        }

        data class Success(val problems: List<Problem>) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class ChangeStatusFavourite(private val problem: Problem) : Request() {

        override suspend fun execute() {
            val newProblem = problem.copy(isFavourite = !problem.isFavourite)
            DatabaseQueries.Problems.insert(newProblem)
            store.dispatch(Success(newProblem))
        }

        data class Success(val problem: Problem) : Action
    }
}
