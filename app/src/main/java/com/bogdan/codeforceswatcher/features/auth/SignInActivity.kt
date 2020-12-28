package com.bogdan.codeforceswatcher.features.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.components.InputField
import com.bogdan.codeforceswatcher.util.AnalyticsController
import io.xorum.codeforceswatcher.features.auth.AuthRequests
import io.xorum.codeforceswatcher.features.users.redux.states.UsersState
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.AnalyticsEvents
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.input_field.view.*
import tw.geothings.rekotlin.StoreSubscriber

class SignInActivity : AppCompatActivity(), StoreSubscriber<UsersState> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        title = getString(R.string.sign_in)

        ifEmail.configure(
                labelTextResId = R.string.email,
                type = InputField.Type.EMAIL,
                action = InputField.Action.Next
        )
        ifPassword.configure(
                labelTextResId = R.string.password,
                type = InputField.Type.PASSWORD,
                action = InputField.Action.Go {
                    signInWithEmailAndPassword()
                }
        )

        btnSignIn.setOnClickListener { signInWithEmailAndPassword() }
        tvForgotPassword.setOnClickListener { forgotPassword() }
    }

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.users.signInStatus == newState.users.signInStatus
            }.select { it.users }
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    private fun forgotPassword() {
        val email = ifEmail.editText.text.trim().toString()
        AnalyticsController().logEvent(AnalyticsEvents.FORGOT_PASSWORD_PRESSED, mapOf("email" to email))
    }

    private fun signInWithEmailAndPassword() {
        val email = ifEmail.editText.text.trim().toString()
        val password = ifPassword.editText.text.toString()
        store.dispatch(AuthRequests.SignIn(email, password))
    }

    override fun onNewState(state: UsersState) {
        when (state.signInStatus) {
            UsersState.Status.PENDING -> spinner.visibility = View.VISIBLE.also { println("Here here ") }
            UsersState.Status.DONE -> finish()
            UsersState.Status.IDLE -> spinner.visibility = View.GONE
        }
    }
}