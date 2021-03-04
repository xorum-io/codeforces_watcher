package com.bogdan.codeforceswatcher.features

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.components.AddUserBottomSheet
import com.bogdan.codeforceswatcher.components.WebViewActivity
import com.bogdan.codeforceswatcher.features.contests.ContestsFragment
import com.bogdan.codeforceswatcher.features.contests.FiltersActivity
import com.bogdan.codeforceswatcher.features.news.NewsFragment
import com.bogdan.codeforceswatcher.features.problems.ProblemsFragment
import com.bogdan.codeforceswatcher.features.users.UsersFragment
import com.bogdan.codeforceswatcher.util.FeedbackController
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import io.xorum.codeforceswatcher.features.problems.redux.actions.ProblemsActions
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.AnalyticsEvents
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val currentTabFragment: Fragment?
        get() = supportFragmentManager.fragments.lastOrNull()

    private var searchViewItem: MenuItem? = null

    private var selectedHomeTab = HomeTab.USERS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FeedbackController.feedbackController = FeedbackController(this@MainActivity)
        FeedbackController.get().updateCountOpeningScreen()

        initViews()
    }

    private fun updateFragment() {
        val bottomNavSelectedItemId = selectedHomeTab.menuItemId

        tvPageTitle.text = getString(selectedHomeTab.titleId)
        toolbar.collapseActionView()

        if (bottomNavigation.selectedItemId != bottomNavSelectedItemId) {
            bottomNavigation.selectedItemId = bottomNavSelectedItemId
        }

        onNewTabSelected()

        val fragment: Fragment = when (selectedHomeTab) {
            HomeTab.USERS -> {
                currentTabFragment as? UsersFragment ?: UsersFragment()
            }
            HomeTab.CONTESTS -> {
                currentTabFragment as? ContestsFragment ?: ContestsFragment()
            }
            HomeTab.NEWS -> {
                currentTabFragment as? NewsFragment ?: NewsFragment()
            }
            HomeTab.PROBLEMS -> {
                currentTabFragment as? ProblemsFragment ?: ProblemsFragment()
            }
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }

    private fun onNewTabSelected() = when (selectedHomeTab) {
        HomeTab.USERS -> onUsersTabSelected()
        HomeTab.CONTESTS -> onContestsTabSelected()
        HomeTab.NEWS -> onNewsTabSelected()
        HomeTab.PROBLEMS -> onProblemsTabSelected()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        searchViewItem = menu?.findItem(R.id.action_search)
        return super.onCreateOptionsMenu(menu)
    }

    private fun onUsersTabSelected() {
        llSorting.visibility = View.VISIBLE
        ivFilter.visibility = View.GONE
        searchViewItem?.isVisible = false

        fab.setOnClickListener {
            AddUserBottomSheet().show(supportFragmentManager, null)
        }
        fab.setImageDrawable(getDrawable(R.drawable.ic_plus))
    }

    private fun onContestsTabSelected() {
        llSorting.visibility = View.GONE
        ivFilter.visibility = View.VISIBLE
        searchViewItem?.isVisible = false

        fab.setOnClickListener {
            startActivity(
                    WebViewActivity.newIntent(
                            this,
                            CONTESTS_LINK,
                            getString(R.string.upcoming_contests)
                    )
            )
        }
        ivFilter.setOnClickListener {
            startActivity(Intent(this, FiltersActivity::class.java))
        }
        fab.setImageDrawable(getDrawable(R.drawable.ic_eye))
    }

    private fun onNewsTabSelected() {
        llSorting.visibility = View.GONE
        ivFilter.visibility = View.GONE
        searchViewItem?.isVisible = false

        fab.setOnClickListener {
            showShareDialog()
            analyticsController.logEvent(AnalyticsEvents.SHARE_APP)
        }
        fab.setImageDrawable(getDrawable(R.drawable.ic_share))
    }

    private fun onProblemsTabSelected() {
        llSorting.visibility = View.GONE
        ivFilter.visibility = View.GONE
        searchViewItem?.isVisible = true

        var problemsIsFavourite = store.state.problems.isFavourite
        updateProblemsFAB(problemsIsFavourite)

        fab.setOnClickListener {
            problemsIsFavourite = !(problemsIsFavourite)

            store.dispatch(ProblemsActions.ChangeTypeProblems(problemsIsFavourite))
            updateProblemsFAB(problemsIsFavourite)
        }
    }

    private fun updateProblemsFAB(problemsIsFavourite: Boolean) {
        if (problemsIsFavourite) {
            fab.setImageDrawable(getDrawable(R.drawable.ic_all))
        } else {
            fab.setImageDrawable(getDrawable(R.drawable.ic_star))
        }
    }

    private fun showShareDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.share_cw))
                .setMessage(getString(R.string.help_cw_make_more_social))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.share)) { _, _ ->
                    share()
                    analyticsController.logEvent(AnalyticsEvents.APP_SHARED)
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show()
    }

    private fun share() = startActivity(Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, getString(R.string.share_cw_message))
    })

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        spSort.background.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP
        )

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val homeTab = HomeTab.fromMenuItemId(item.itemId)
            if (homeTab != selectedHomeTab) {
                selectedHomeTab = homeTab
                updateFragment()
            }
            true
        }

        updateFragment()

        val menuView = bottomNavigation.getChildAt(0) as? BottomNavigationMenuView ?: return

        for (i in 0 until menuView.childCount) {
            val activeLabel = menuView.getChildAt(i).findViewById<View>(R.id.largeLabel)
            if (activeLabel is TextView) {
                activeLabel.setPadding(0, 0, 0, 0)
            }
        }
    }

    companion object {
        private const val CONTESTS_LINK = "https://clist.by/"
    }

    enum class HomeTab(val titleId: Int, val menuItemId: Int) {

        USERS(R.string.empty, R.id.navUsers),
        CONTESTS(R.string.contests, R.id.navContests),
        NEWS(R.string.news, R.id.navNews),
        PROBLEMS(R.string.problems, R.id.navProblems);

        companion object {

            fun fromMenuItemId(menuItemId: Int): HomeTab =
                    enumValues<HomeTab>().find { it.menuItemId == menuItemId } ?: USERS
        }
    }
}
