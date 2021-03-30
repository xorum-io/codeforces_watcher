package com.bogdan.codeforceswatcher.features.auth

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bogdan.codeforceswatcher.CwApp
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.components.InputField
import com.bogdan.codeforceswatcher.util.disable
import com.bogdan.codeforceswatcher.util.enable
import io.xorum.codeforceswatcher.features.verification.redux.VerificationRequests
import io.xorum.codeforceswatcher.features.verification.redux.VerificationState
import io.xorum.codeforceswatcher.redux.store
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.input_field.view.*
import tw.geothings.rekotlin.StoreSubscriber

class VerificationActivity : AppCompatActivity(), StoreSubscriber<VerificationState> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        title = getString(R.string.verify_codeforces_account)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        ifHandle.configure(
                labelTextResId = R.string.codeforces_handle,
                type = InputField.Type.TEXT
        )

        store.dispatch(VerificationRequests.FetchVerificationCode())

        tvVerificationCode.setOnLongClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("text", tvVerificationCode.text)
            clipboard.setPrimaryClip(clip)
            showToast(getString(R.string.code_copied_to_clipboard))
            true
        }

        btnVerify.setOnClickListener {
            store.dispatch(VerificationRequests.VerifyCodeforces(ifHandle.editText.text.toString()))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showToast(message: String) = Toast.makeText(CwApp.app, message, Toast.LENGTH_SHORT).show()

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.verification == newState.verification
            }.select { it.verification }
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    override fun onNewState(state: VerificationState) {
        if (state.status == VerificationState.Status.DONE) {
            finish()
            return
        }

        if (state.status == VerificationState.Status.PENDING) {
            spinner.visibility = View.VISIBLE
            btnVerify.disable()
        } else {
            spinner.visibility = View.GONE
            tvVerificationCode?.text = state.verificationCode
            btnVerify.enable()
        }
    }
}