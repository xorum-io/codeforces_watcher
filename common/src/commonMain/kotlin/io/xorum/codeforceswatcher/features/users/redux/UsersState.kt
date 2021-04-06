package io.xorum.codeforceswatcher.features.users.redux

import io.xorum.codeforceswatcher.features.auth.models.UserAccount
import io.xorum.codeforceswatcher.features.users.models.User
import tw.geothings.rekotlin.StateType

data class UsersState(
        val status: Status = Status.IDLE,
        val users: List<User> = listOf(),
        val sortType: SortType = SortType.DEFAULT,
        val addUserStatus: Status = Status.IDLE,
        val userAccount: UserAccount? = null,
        val currentUser: User? = null
) : StateType {

    enum class Status { IDLE, PENDING, DONE }
    enum class SortType {

        DEFAULT, RATING_UP, RATING_DOWN, UPDATE_UP, UPDATE_DOWN;

        val position: Int
            get() = when (this) {
                DEFAULT -> 0
                RATING_DOWN -> 1
                RATING_UP -> 2
                UPDATE_DOWN -> 3
                UPDATE_UP -> 4
            }

        companion object {
            fun getSortType(sortType: Int) = when (sortType) {
                0 -> DEFAULT
                1 -> RATING_DOWN
                2 -> RATING_UP
                3 -> UPDATE_DOWN
                4 -> UPDATE_UP
                else -> DEFAULT
            }
        }
    }
}
