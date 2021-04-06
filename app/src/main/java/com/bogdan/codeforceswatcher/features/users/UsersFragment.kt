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
import com.bogdan.codeforceswatcher.epoxy.BaseEpoxyController
import io.xorum.codeforceswatcher.features.auth.redux.AuthState
import io.xorum.codeforceswatcher.features.auth.models.UserAccount
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.features.users.redux.*
import io.xorum.codeforceswatcher.features.users.redux.UsersState.SortType.Companion.getSortType
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.redux.states.AppState
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.AnalyticsEvents
import kotlinx.android.synthetic.main.fragment_users.*
import tw.geothings.rekotlin.StoreSubscriber

class UsersFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, StoreSubscriber<AppState> {

    private lateinit var spSort: AppCompatSpinner

    private val epoxyController by lazy { EpoxyController() }

    override fun onRefresh() {
        store.dispatch(UsersRequests.FetchUserData(FetchUserDataType.REFRESH, Source.USER))
        analyticsController.logEvent(AnalyticsEvents.USERS_REFRESH)
    }

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.users == newState.users && oldState.auth == newState.auth
            }
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    override fun onNewState(state: AppState) {
        swipeRefreshLayout.isRefreshing = (state.users.status == UsersState.Status.PENDING)

        epoxyController.userAccount = state.users.userAccount
        epoxyController.authStage = state.auth.authStage
        epoxyController.data = state.users.users.sort(state.users.sortType).map { UserItem(it) }

        adjustSpinnerSortVisibility(state.users.users.isEmpty())

        if (state.users.addUserStatus == UsersState.Status.DONE) {
            store.dispatch(UsersActions.ClearAddUserState())
        }
    }

    private fun adjustSpinnerSortVisibility(isUsersListEmpty: Boolean) {
        spSort.visibility = if (isUsersListEmpty) View.GONE else View.VISIBLE
        requireActivity().findViewById<TextView>(R.id.tvSortBy).visibility =
                if (isUsersListEmpty) View.GONE else View.VISIBLE
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

        recyclerView.adapter = epoxyController.adapter
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

    class EpoxyController : BaseEpoxyController<UserItem>() {

        var userAccount: UserAccount? = null
            set(value) {
                field = value
                requestModelBuild()
            }

        var authStage: AuthState.Stage = store.state.auth.authStage
            set(value) {
                field = value
                requestModelBuild()
            }

        override fun buildModels() {
            ProfileItemEpoxyModel(userAccount, authStage).addTo(this)
            data?.forEach { userItem ->
                UserItemEpoxyModel(userItem).addTo(this)
            }
        }
    }
}