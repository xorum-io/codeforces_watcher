package com.bogdan.codeforceswatcher.features.auth

import android.content.Intent
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.components.InputField
import com.bogdan.codeforceswatcher.util.AnalyticsController
import com.bogdan.codeforceswatcher.util.linked
import io.xorum.codeforceswatcher.features.auth.redux.AuthRequests
import io.xorum.codeforceswatcher.features.auth.redux.AuthState
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.AnalyticsEvents
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.input_field.view.*
import tw.geothings.rekotlin.StoreSubscriber

class SignInActivity : AppCompatActivity(), StoreSubscriber<AuthState> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        title = getString(R.string.sign_in)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

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

        tvSignUp.text = getString(R.string.dont_have_an_account_yet).linked(listOf(listOf(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary))
        )))
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.auth.signInStatus == newState.auth.signInStatus
            }.select { it.auth }
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

    override fun onDestroy() {
        super.onDestroy()
        store.dispatch(AuthRequests.DestroyStatus)
    }

    override fun onNewState(state: AuthState) {
        when (state.signInStatus) {
            AuthState.Status.PENDING -> spinner.visibility = View.VISIBLE
            AuthState.Status.DONE -> finish()
            AuthState.Status.IDLE -> spinner.visibility = View.GONE
        }
    }
}