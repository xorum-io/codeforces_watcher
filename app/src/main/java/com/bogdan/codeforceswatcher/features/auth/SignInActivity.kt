package com.bogdan.codeforceswatcher.features.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.components.InputField
import io.xorum.codeforceswatcher.features.auth.AuthRequests
import io.xorum.codeforceswatcher.redux.store
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.input_field.view.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

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

        tvForgotPassword.setOnClickListener { forgotPassword() }
    }

    private fun forgotPassword() {
        val email = ifEmail.editText.text.trim().toString()
        // TODO add analytics
    }

    private fun signInWithEmailAndPassword() {
        val email = ifEmail.editText.text.trim().toString()
        val password = ifPassword.editText.text.toString()
        store.dispatch(AuthRequests.SignIn(email, password))
    }
}