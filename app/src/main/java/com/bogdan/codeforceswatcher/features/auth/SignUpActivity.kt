package com.bogdan.codeforceswatcher.features.auth

import android.os.Bundle
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.components.InputField
import com.bogdan.codeforceswatcher.components.WebViewActivity
import com.bogdan.codeforceswatcher.util.linked
import io.xorum.codeforceswatcher.features.auth.AuthRequests
import io.xorum.codeforceswatcher.features.auth.AuthState
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.redux.toMessage
import io.xorum.codeforceswatcher.util.Constants.PRIVACY_POLICY_LINK
import io.xorum.codeforceswatcher.util.Constants.TERMS_AND_CONDITIONS_LINK
import kotlinx.android.synthetic.main.activity_sign_up.*
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

        tvSignIn.text = getString(R.string.already_have_an_account).linked(
                listOf(listOf(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary))))
        )
        tvPrivacy.text = getString(R.string.agree_with_the_conditions_and_privacy_policy).linked(listOf(
                listOf<CharacterStyle>(
                        buildClickableSpan {
                            startActivity(WebViewActivity.newIntent(this, TERMS_AND_CONDITIONS_LINK, getString(R.string.terms_and_conditions)))
                        }
                ), listOf<CharacterStyle>(
                buildClickableSpan {
                    startActivity(WebViewActivity.newIntent(this, PRIVACY_POLICY_LINK, getString(R.string.privacy_policy)))
                })
        ))
        tvPrivacy.movementMethod = LinkMovementMethod.getInstance()

        tvSignIn.setOnClickListener { finish() }

        adjustButtonEnablement(false)

        checkbox.setOnCheckedChangeListener { _, isChecked ->
            adjustButtonEnablement(isChecked)
        }
    }

    private fun buildClickableSpan(onClickListener: () -> Unit) = object : ClickableSpan() {
        override fun onClick(widget: View) {
            onClickListener.invoke()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = ContextCompat.getColor(this@SignUpActivity, R.color.black)
        }
    }

    private fun adjustButtonEnablement(isChecked: Boolean) {
        if (isChecked) {
            btnSignUp.isEnabled = true
            btnSignUp.alpha = 1f
        } else {
            btnSignUp.isEnabled = false
            btnSignUp.alpha = 0.5f
        }
    }

    private fun signUp() {
        val email = ifEmail.editText.text.trim().toString()
        val password = ifPassword.editText.text.toString()
        val confirmedPassword = ifConfirmPassword.editText.text.toString()
        when {
            password != confirmedPassword -> store.dispatch(AuthRequests.SignUp.Failure(getString(R.string.passwords_do_not_match).toMessage()))
            !checkbox.isChecked -> store.dispatch(AuthRequests.SignUp.Failure(getString(R.string.agree_to_the_privacy_policy).toMessage()))
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
