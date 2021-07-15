package com.bogdan.codeforceswatcher.features.contests

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bogdan.codeforceswatcher.R
import io.xorum.codeforceswatcher.features.contests.models.Contest
import io.xorum.codeforceswatcher.redux.store
import kotlinx.android.synthetic.main.activity_filters.*

class FiltersActivity : AppCompatActivity() {

    private val filtersAdapter: FiltersAdapter = FiltersAdapter(this, buildFiltersList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        recyclerView.adapter = filtersAdapter
    }

    private fun buildFiltersList(): List<FilterItem> {
        val filters = store.state.contests.filters
        return listOf(
                FilterItem(R.drawable.codeforces, "Codeforces", Contest.Platform.CODEFORCES, filters.contains(Contest.Platform.CODEFORCES)),
                FilterItem(R.drawable.codeforces, "Codeforces Gym", Contest.Platform.CODEFORCES_GYM, filters.contains(Contest.Platform.CODEFORCES_GYM)),
                FilterItem(R.drawable.atcoder, "AtCoder", Contest.Platform.ATCODER, filters.contains(Contest.Platform.ATCODER)),
                FilterItem(R.drawable.leetcode, "LeetCode", Contest.Platform.LEETCODE, filters.contains(Contest.Platform.LEETCODE)),
                FilterItem(R.drawable.topcoder, "TopCoder", Contest.Platform.TOPCODER, filters.contains(Contest.Platform.TOPCODER)),
                FilterItem(R.drawable.csacademy, "CS Academy", Contest.Platform.CS_ACADEMY, filters.contains(Contest.Platform.CS_ACADEMY)),
                FilterItem(R.drawable.codechef, "CodeChef", Contest.Platform.CODECHEF, filters.contains(Contest.Platform.CODECHEF)),
                FilterItem(R.drawable.hackerrank, "HackerRank", Contest.Platform.HACKERRANK, filters.contains(Contest.Platform.HACKERRANK)),
                FilterItem(R.drawable.hackerearth, "HackerEarth", Contest.Platform.HACKEREARTH, filters.contains(Contest.Platform.HACKEREARTH)),
                FilterItem(R.drawable.kickstart, "Kick Start", Contest.Platform.KICK_START, filters.contains(Contest.Platform.KICK_START)),
                FilterItem(R.drawable.toph, "Toph", Contest.Platform.TOPH, filters.contains(Contest.Platform.TOPH))
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
