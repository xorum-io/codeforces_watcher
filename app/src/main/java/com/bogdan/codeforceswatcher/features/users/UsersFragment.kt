package com.bogdan.codeforceswatcher.features.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bogdan.codeforceswatcher.R
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.features.users.redux.actions.UsersActions
import io.xorum.codeforceswatcher.features.users.redux.requests.Source
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.features.users.redux.states.UsersState
import io.xorum.codeforceswatcher.features.users.redux.states.UsersState.SortType.Companion.getSortType
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.RefreshScreen
import kotlinx.android.synthetic.main.fragment_users.*
import tw.geothings.rekotlin.StoreSubscriber
import java.util.*

class UsersFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, StoreSubscriber<UsersState> {

    private lateinit var spSort: AppCompatSpinner
    private lateinit var usersAdapter: UsersAdapter

    override fun onRefresh() {
        store.dispatch(UsersRequests.FetchUsers(Source.USER, Locale.getDefault().language))
        analyticsController.logRefreshingData(RefreshScreen.USERS)
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
        swipeRefreshLayout.isRefreshing = (state.status == UsersState.Status.PENDING)
        usersAdapter.setItems(state.users.sort(state.sortType).map { UserItem.User(it) })
        adjustSpinnerSortVisibility(state.users.isEmpty())

        if (state.addUserStatus == UsersState.Status.DONE) {
            store.dispatch(UsersActions.ClearAddUserState())
        }
    }

    private fun adjustSpinnerSortVisibility(isUsersListEmpty: Boolean) {
        spSort.visibility = if (isUsersListEmpty) View.GONE else View.VISIBLE
        requireActivity().findViewById<TextView>(R.id.tvSortBy).visibility =
                if (isUsersListEmpty) View.GONE else View.VISIBLE
        swipeRefreshLayout.isEnabled = !isUsersListEmpty
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_users, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        swipeRefreshLayout.setOnRefreshListener(this)

        usersAdapter = UsersAdapter(requireContext()) { userIndex ->
            val user = store.state.users.users.sort(store.state.users.sortType)[userIndex]
            startActivity(UserActivity.newIntent(requireContext(), user.id))
        }

        recyclerView.adapter = usersAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        spSort = requireActivity().findViewById(R.id.spSort)

        val spinnerAdapter = ArrayAdapter(
                requireContext(), R.layout.spinner_item,
                resources.getStringArray(R.array.array_sort)
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spSort.adapter = spinnerAdapter
        spSort.setSelection(store.state.users.sortType.position)

        spSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                store.dispatch(UsersActions.Sort(getSortType(position)))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun List<User>.sort(sortType: UsersState.SortType) = when (sortType) {
        UsersState.SortType.DEFAULT -> reversed()
        UsersState.SortType.RATING_DOWN -> sortedByDescending(User::rating)
        UsersState.SortType.RATING_UP -> sortedBy(User::rating)
        UsersState.SortType.UPDATE_DOWN -> sortedByDescending { user ->
            user.ratingChanges.lastOrNull()?.ratingUpdateTimeSeconds
        }
        UsersState.SortType.UPDATE_UP -> sortedBy { user ->
            user.ratingChanges.lastOrNull()?.ratingUpdateTimeSeconds
        }
    }
}
