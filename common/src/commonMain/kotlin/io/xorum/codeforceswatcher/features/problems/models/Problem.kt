package io.xorum.codeforceswatcher.features.problems.models

import io.xorum.codeforceswatcher.DbProblem
import kotlinx.serialization.Serializable

@Serializable
data class Problem(
        val id: String,
        val title: String,
        val subtitle: String,
        val platform: Platform,
        val link: String,
        val createdAtMillis: Long,
        val tags: List<String>,
        var isFavourite: Boolean = false // TODO: make val
) {

    enum class Platform {
        CODEFORCES
    }

    companion object {

        fun fromDB(dbProblem: DbProblem) = Problem(
                id = dbProblem.id,
                title = dbProblem.title,
                subtitle = dbProblem.subtitle,
                platform = Platform.valueOf(dbProblem.platform),
                link = dbProblem.link,
                createdAtMillis = dbProblem.createdAtMillis,
                tags = dbProblem.tags.split(","),
                isFavourite = dbProblem.isFavourite
        )
    }
}
