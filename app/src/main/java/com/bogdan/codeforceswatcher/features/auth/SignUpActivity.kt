package com.bogdan.codeforceswatcher.features.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.components.InputField
import com.bogdan.codeforceswatcher.util.colorLinkTagPart
import io.xorum.codeforceswatcher.features.auth.AuthRequests
import io.xorum.codeforceswatcher.features.auth.AuthState
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.redux.toMessage
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.ifEmail
import kotlinx.android.synthetic.main.activity_sign_up.ifPassword
import kotlinx.android.synthetic.main.activity_sign_up.spinner
import kotlinx.android.synthetic.main.input_field.view.*
import tw.geothings.rekotlin.StoreSubscriber

class SignUpActivity : AppCompatActivity(), StoreSubscriber<AuthState> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        title = getString(R.string.sign_up)

        initViews()
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initViews() {
        ifEmail.configure(
                labelTextResId = R.string.email,
                type = InputField.Type.EMAIL,
                action = InputField.Action.Next
        )
        ifPassword.configure(
                labelTextResId = R.string.password,
                type = InputField.Type.PASSWORD
        )
        ifConfirmPassword.configure(
                labelTextResId = R.string.confirm_password,
                type = InputField.Type.PASSWORD,
                action = InputField.Action.Go { signUp() }
        )

        btnSignUp.setOnClickListener { signUp() }

        tvSignIn.text = getString(R.string.already_have_an_account).colorLinkTagPart()
        tvSignIn.setOnClickListener { finish() }
    }

    private fun signUp() {
        val email = ifEmail.editText.text.trim().toString()
        val password = ifPassword.editText.text.toString()
        val confirmedPassword = ifConfirmPassword.editText.text.toString()
        when {
            password != confirmedPassword -> store.dispatch(AuthRequests.SignUp.Failure(getString(R.string.passwords_do_not_match).toMessage()))
            checkbox.isChecked.not() -> store.dispatch(AuthRequests.SignUp.Failure(getString(R.string.agree_to_the_privacy_policy).toMessage()))
            else -> store.dispatch(AuthRequests.SignUp(email, password))
        }
    }

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.auth.signUpStatus == newState.auth.signUpStatus
            }.select { it.auth }
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    override fun onNewState(state: AuthState) {
        when (state.signUpStatus) {
            AuthState.Status.PENDING -> spinner.visibility = View.VISIBLE
            AuthState.Status.DONE -> finish()
            AuthState.Status.IDLE -> spinner.visibility = View.GONE
        }
    }
}
