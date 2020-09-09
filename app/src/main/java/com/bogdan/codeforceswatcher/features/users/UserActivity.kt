package com.bogdan.codeforceswatcher.features.users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.avatar
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.activity_user.tvUserHandle
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class UserActivity : AppCompatActivity() {

    private var userId: Long = -1
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        userId = intent.getLongExtra(ID, -1)

        user = store.state.users.users.find { it.id == userId } ?: throw IllegalStateException()

        displayUser()

        if (user.ratingChanges.isNotEmpty()) {
            displayChart()
        } else {
            tvRatingChanges.text = ""
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun displayUser() {
        tvRank.text = user.buildRank()
        tvUserRating.text = user.buildRating()
        tvUserHandle.text = user.buildName()
        (ivUserAvatar as CircleImageView).borderColor = ContextCompat.getColor(this, getColorByUserRank(user.rank))

        Picasso.get().load(avatar(user.avatar)).into(ivUserAvatar)
        title = user.buildScreenTitle()
    }

    private fun User.buildRank() = rank?.let { colorTextByUserRank(it.capitalize(), it) }
            ?: getString(R.string.none)

    private fun User.buildRating(): SpannableString {
        val str = SpannableString(
                getString(
                        R.string.rating,
                        rating?.toString() ?: getString(R.string.none),
                        maxRating?.toString() ?: getString(R.string.none)
                )
        )

        rating?.let {
            val startIndex = str.indexOf(it.toString())
            val color = getColorByUserRank(rank)
            str.colorSubstring(startIndex, startIndex + it.toString().length, color)
        }

        maxRating?.let {
            val startIndex = str.lastIndexOf(it.toString())
            val color = getColorByUserRank(maxRank)
            str.colorSubstring(startIndex, startIndex + it.toString().length, color)
        }

        return str
    }

    private fun User.buildName() = colorTextByUserRank(when {
        firstName == null && lastName == null -> getString(R.string.none)
        firstName == null -> lastName!!
        lastName == null -> firstName!!
        else -> "$firstName $lastName"
    }, rank)

    private fun User.buildScreenTitle() = when {
        firstName == null && lastName == null -> handle
        firstName == null -> lastName!!
        lastName == null -> firstName!!
        else -> "$firstName $lastName"
    }

    private fun displayChart() {
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

        private const val ID = "id"

        fun newIntent(context: Context, userId: Long): Intent {
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(ID, userId)
            return intent
        }
    }
}

