package com.bogdan.codeforceswatcher.features.users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.util.CustomMarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.avatar
import kotlinx.android.synthetic.main.activity_user.*
import java.text.SimpleDateFormat
import java.util.*

class UserActivity : AppCompatActivity() {

    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        userId = intent.getLongExtra(ID, -1)

        val user = store.state.users.users.find { it.id == userId }
        user?.let { foundUser ->
            displayUser(foundUser)
            if (foundUser.ratingChanges.isNotEmpty()) {
                displayChart(foundUser)
            } else {
                tvRatingChanges.text = ""
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun displayUser(user: User) {
        tvRank.text = user.buildRank()
        tvCurrentRating.text = user.buildRating()
        tvUserHandle.text = getString(R.string.name, user.buildName())
        tvMaxRating.text = user.buildMaxRating()
        (ivUserAvatar as CircleImageView).borderColor = ContextCompat.getColor(this, getColorByUserRank(user.rank))

        Picasso.get().load(avatar(user.avatar)).into(ivUserAvatar)
        title = user.handle
    }

    private fun User.buildRank() = if (rank == null) {
        getString(R.string.rank, getString(R.string.none))
    } else {
        getString(R.string.rank, rank)
    }

    private fun User.buildRating() = if (rating == null) {
        getString(R.string.cur_rating, getString(R.string.none))
    } else {
        getString(R.string.cur_rating, rating.toString())
    }

    private fun User.buildName() = when {
        firstName == null && lastName == null -> getString(R.string.none)
        firstName == null -> lastName
        lastName == null -> firstName
        else -> "$firstName $lastName"
    }

    private fun User.buildMaxRating() = if (maxRating == null) {
        getString(R.string.max_rating, getString(R.string.none))
    } else {
        getString(R.string.max_rating, maxRating.toString())
    }

    private fun displayChart(user: User) {
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

        val entries = user.ratingChanges.map {
            Entry(it.ratingUpdateTimeSeconds.toFloat(), it.newRating.toFloat(), it.toChartItem())
        }

        val lineDataSet = LineDataSet(entries, user.handle)
        lineDataSet.setDrawValues(false)
        chart.data = LineData(lineDataSet)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_user_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                val user = store.state.users.users.find { it.id == userId }
                user?.let { store.dispatch(UsersRequests.DeleteUser(it)) }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private const val ID = "id"

        fun newIntent(context: Context, userId: Long): Intent {
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(ID, userId)
            return intent
        }
    }
}
