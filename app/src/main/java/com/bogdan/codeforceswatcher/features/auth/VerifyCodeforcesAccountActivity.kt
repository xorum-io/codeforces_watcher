package com.bogdan.codeforceswatcher.features.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.components.InputField
import io.xorum.codeforceswatcher.features.auth.AuthState
import io.xorum.codeforceswatcher.redux.store
import kotlinx.android.synthetic.main.activity_verify_codeforces_account.*
import tw.geothings.rekotlin.StoreSubscriber

class VerifyCodeforcesAccountActivity : AppCompatActivity(), StoreSubscriber<AuthState> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_codeforces_account)

        title = getString(R.string.verify_codeforces_account)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        ifHandle.configure(
                labelTextResId = R.string.codeforces_handle,
                type = InputField.Type.TEXT
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        // TODO
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

    override fun onNewState(state: AuthState) {
        // TODO
    }
}