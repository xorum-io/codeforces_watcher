package io.xorum.codeforceswatcher.db

import io.xorum.codeforceswatcher.features.problems.models.Problem
import io.xorum.codeforceswatcher.features.users.models.RatingChange
import io.xorum.codeforceswatcher.features.users.models.User
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

internal object DatabaseQueries {

    internal object Users {

        fun getAll() = database.userQueries.getAll().executeAsList().map { User.fromDB(it) }

        fun get(handle: String) = database.userQueries.getByHandle(handle).executeAsList().map { User.fromDB(it) }.first()

        fun insert(user: User) {
            val serializer = Json(from = Json.Default) { ignoreUnknownKeys = true }
            val ratingChangesJson = serializer.encodeToString(ListSerializer(RatingChange.serializer()), user.ratingChanges)
            database.userQueries.insert(
                    user.id,
                    user.avatar,
                    user.rank,
                    user.handle,
                    user.rating?.toLong(),
                    user.maxRating?.toLong(),
                    user.firstName,
                    user.lastName,
                    ratingChangesJson,
                    user.maxRank,
                    user.contribution
            )
        }

        private fun merge(oldUser: User, newUser: User): User {
            val ratingChanges = (oldUser.ratingChanges + newUser.ratingChanges).distinct().sortedBy { it.ratingUpdateTimeSeconds }
            return newUser.copy(ratingChanges = ratingChanges)
        }

        fun update(user: User) {
            val oldUser = get(user.handle)
            val mergedUser = merge(oldUser, user)

            val serializer = Json(from = Json.Default) { ignoreUnknownKeys = true }
            val ratingChangesJson = serializer.encodeToString(ListSerializer(RatingChange.serializer()), mergedUser.ratingChanges)

            database.userQueries.update(
                    user.avatar,
                    user.rank,
                    user.rating?.toLong(),
                    user.maxRating?.toLong(),
                    user.firstName,
                    user.lastName,
                    ratingChangesJson,
                    user.maxRank,
                    user.contribution,
                    user.handle
            )
        }

        fun update(users: List<User>) {
            database.userQueries.transaction {
                users.forEach { user ->
                    update(user)
                }
            }
        }

        fun insert(users: List<User>) = database.userQueries.transaction {
            users.forEach { user ->
                insert(user)
            }
        }

        fun delete(handle: String) = database.userQueries.delete(handle)

        fun delete(users: List<User>) = database.userQueries.transaction {
            users.forEach {
                delete(it.handle)
            }
        }

        fun deleteAll() = database.userQueries.deleteAll()
    }

    internal object Problems {

        fun getAll() = database.problemQueries.getAll().executeAsList().map { Problem.fromDB(it) }

        fun insert(problems: List<Problem>) {
            database.problemQueries.transaction {
                for (problem in problems) {
                    insert(problem)
                }
            }
        }

        fun insert(problem: Problem) = with(problem) {
            database.problemQueries.insert(
                    id = id,
                    title = title,
                    subtitle = subtitle,
                    platform = platform.toString(),
                    link = link,
                    createdAtMillis = createdAtMillis,
                    tags = tags.joinToString(separator = ","),
                    isFavourite = isFavourite
            )
        }

        fun update(problems: List<Problem>) = database.problemQueries.transaction {
            problems.forEach {
                update(it)
            }
        }

        fun update(problem: Problem) = with(problem) {
            database.problemQueries.update(
                    title = title,
                    subtitle = subtitle,
                    platform = platform.toString(),
                    link = link,
                    createdAtMillis = createdAtMillis,
                    tags = tags.joinToString(separator = ","),
                    isFavourite = isFavourite,
                    id = id
            )
        }
    }
}
