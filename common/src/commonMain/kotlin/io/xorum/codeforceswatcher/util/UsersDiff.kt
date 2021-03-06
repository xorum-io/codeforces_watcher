package io.xorum.codeforceswatcher.util

import io.xorum.codeforceswatcher.features.users.models.User

class UsersDiff(
        private val allUsers: List<User>,
        private val newUsers: List<User>
) {

    fun getDiff(): Pair<List<User>, List<User>> {
        val allUsersSet = allUsers.map { it.handle }.toHashSet()
        val newUsersSet = newUsers.toSet()

        val diff = newUsersSet.filterNot { user -> allUsersSet.contains(user.handle) }
        val toUpdateDiff = newUsersSet.filter { user -> allUsersSet.contains(user.handle) }

        return Pair(diff, toUpdateDiff)
    }
}