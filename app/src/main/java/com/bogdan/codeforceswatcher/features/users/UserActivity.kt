package com.bogdan.codeforceswatcher.features.users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.CwApp
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.util.CustomMarkerView
import com.bogdan.codeforceswatcher.util.colorSubstring
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.features.users.redux.states.UsersState
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.avatar
import kotlinx.android.synthetic.main.activity_user.*
import tw.geothings.rekotlin.StoreSubscriber
import java.text.SimpleDateFormat
import java.util.*

class UserActivity : AppCompatActivity(), StoreSubscriber<UsersState> {

    private val handle
        get() = intent.getStringExtra(HANDLE)
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setupChart()

        store.dispatch(UsersRequests.FetchUser(handle))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        store.dispatch(UsersRequests.ClearCurrentUser)
    }

    private fun displayUser() {
        tvRank.text = user.buildRank()
        tvUserRating.text = user.buildRating()
        tvUserHandle.text = user.buildHandle()
        tvContribution.text = user.buildContribution()
        (ivUserAvatar as CircleImageView).borderColor = ContextCompat.getColor(this, getColorByUserRank(user.rank))

        Picasso.get().load(avatar(user.avatar)).into(ivUserAvatar)
        title = user.buildFullName()
    }

    private fun User.buildRating() = SpannableString(
            getString(
                    R.string.rating,
                    rating?.toString() ?: getString(R.string.none),
                    maxRating?.toString() ?: getString(R.string.none)
            )
    ).apply {
        rating?.let {
            val startIndex = indexOf(it.toString())
            val color = getColorByUserRank(rank)
            colorSubstring(startIndex, startIndex + it.toString().length, color)
        }

        maxRating?.let {
            val startIndex = lastIndexOf(it.toString())
            val color = getColorByUserRank(maxRank)
            colorSubstring(startIndex, startIndex + it.toString().length, color)
        }
    }

    private fun setupChart() {
        val xAxis = chart.xAxis
        chart.setTouchEnabled(true)
        chart.markerView = CustomMarkerView(this, R.layout.chart)
        chart.isDragEnabled = true
        chart.axisRight.setDrawLabels(false)
        xAxis.setDrawAxisLine(true)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = 3

        xAxis.valueFormatter = IAxisValueFormatter { value, _ ->
            val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            dateFormat.format(Date(value.toLong() * 1000)).toString()
        }
    }

    private fun displayChart() {
        val entries = user.ratingChanges.map {
            Entry(it.ratingUpdateTimeSeconds.toFloat(), it.newRating.toFloat(), it.toChartItem())
        }

        val lineDataSet = LineDataSet(entries, user.handle)
        lineDataSet.setDrawValues(false)
        chart.data = LineData(lineDataSet)
        chart.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_user_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.delete_user))
                        .setMessage(getString(R.string.delete_user_explanation, user.handle))
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            deleteUser()
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .create()
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteUser() {
        store.dispatch(UsersRequests.DeleteUser(user))
        finish()
    }

    companion object {

        private const val HANDLE = "handle"

        fun newIntent(context: Context, handle: String): Intent {
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(HANDLE, handle)
            return intent
        }
    }

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.users == newState.users
            }.select { it.users }
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    override fun onNewState(state: UsersState) {
        user = state.currentUser ?: return

        displayUser()

        if (user.ratingChanges.isNotEmpty()) {
            displayChart()
            tvRatingChanges.visibility = View.VISIBLE
        } else {
            tvRatingChanges.visibility = View.INVISIBLE
        }

        spinner.visibility = if (state.status == UsersState.Status.PENDING) View.VISIBLE else View.INVISIBLE
    }
}

fun User.buildRank() = rank?.let { colorTextByUserRank(it.capitalize(), it) }
        ?: CwApp.app.applicationContext.getString(R.string.none)

fun User.buildHandle() = colorTextByUserRank(handle, rank)

fun User.buildFullName() = when {
    firstName == null && lastName == null -> handle
    firstName == null -> lastName.orEmpty()
    lastName == null -> firstName.orEmpty()
    else -> "$firstName $lastName"
}

fun User.buildContribution() = contribution?.let { contribution ->
    val contributionString = if (contribution > 0) "+$contribution" else contribution.toString()
    SpannableString(
            CwApp.app.getString(R.string.contribution, contributionString)
    ).apply {
        val startIndex = indexOf(contributionString)
        val color = if (contribution >= 0) R.color.bright_green else R.color.red
        colorSubstring(startIndex, startIndex + contributionString.length, color)
    }
} ?: CwApp.app.getString(R.string.none)