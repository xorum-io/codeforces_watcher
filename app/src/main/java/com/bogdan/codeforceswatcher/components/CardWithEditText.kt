package com.bogdan.codeforceswatcher.components

import android.os.Bundle
import com.bogdan.codeforceswatcher.R
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bogdan.codeforceswatcher.util.showSoftKeyboard
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.features.users.redux.states.UsersState
import io.xorum.codeforceswatcher.redux.store
import kotlinx.android.synthetic.main.card_with_edit_text.*
import kotlinx.android.synthetic.main.input_field.*
import tw.geothings.rekotlin.StoreSubscriber
import java.util.*

class AddUserBottomSheet : BottomSheetDialogFragment(), StoreSubscriber<UsersState> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.card_with_edit_text, container, false)
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
        val addUserStatus = state.addUserStatus

        progressBar.visibility = when (addUserStatus) {
            UsersState.Status.IDLE -> INVISIBLE
            UsersState.Status.PENDING -> VISIBLE
            UsersState.Status.DONE -> INVISIBLE
        }

        actionButton.isEnabled = (addUserStatus != UsersState.Status.PENDING)

        if (addUserStatus == UsersState.Status.DONE) {
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionButton.setOnClickListener {
            addUser(editText.text?.toString() ?: "")
        }

        inputField.configure(
                action = InputField.Action.Go {
                    addUser(editText.text?.toString() ?: "")
                }
        )
    }

    private fun addUser(handle: String) {
        store.dispatch(UsersRequests.AddUser(handle))
    }

    override fun onResume() {
        super.onResume()
        editText.showSoftKeyboard()
    }
}
