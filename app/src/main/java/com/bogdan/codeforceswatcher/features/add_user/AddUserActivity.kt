package com.bogdan.codeforceswatcher.features.add_user

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.util.Analytics
import io.xorum.codeforceswatcher.features.users.redux.actions.UsersActions
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.features.users.redux.states.UsersState
import io.xorum.codeforceswatcher.redux.store
import kotlinx.android.synthetic.main.activity_add_user.*
import tw.geothings.rekotlin.StoreSubscriber
import java.util.*

class AddUserActivity : AppCompatActivity(), OnClickListener, StoreSubscriber<UsersState> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        showKeyboard()
        btnAdd.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.users == newState.users
            }.select { it.users }
        }
    }

    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        store.dispatch(UsersActions.ClearAddUserState())
        store.unsubscribe(this)
    }

    private fun showKeyboard() {
        etHandle.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(etHandle.windowToken, 0)
    }

    override fun onNewState(state: UsersState) {
        progressBar.visibility = when (state.addUserStatus) {
            UsersState.Status.IDLE -> INVISIBLE
            UsersState.Status.PENDING -> VISIBLE
            UsersState.Status.DONE -> INVISIBLE
        }
        if (state.addUserStatus == UsersState.Status.DONE) finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAdd -> {
                store.dispatch(UsersRequests.AddUser(etHandle.text.toString(), Locale.getDefault().language))
                Analytics.logUserAdded()
            }
            else -> {
            }
        }
    }
}
