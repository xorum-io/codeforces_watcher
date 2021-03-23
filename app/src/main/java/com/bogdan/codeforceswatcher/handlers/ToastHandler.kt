package com.bogdan.codeforceswatcher.handlers

import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.bogdan.codeforceswatcher.CwApp
import com.bogdan.codeforceswatcher.R
import io.xorum.codeforceswatcher.redux.Message
import io.xorum.codeforceswatcher.redux.middlewares.ToastHandler

class AndroidMessageHandler : ToastHandler {

    override fun handle(message: Message) {
        val string = when (message) {
            is Message.NoConnection -> getString(R.string.check_connection_or_try_again_later)
            is Message.UserAlreadyAdded -> getString(R.string.user_already_added)
            is Message.FailedToFetchUser -> getString(R.string.failed_to_fetch_users)
            is Message.None -> null
            is Message.Custom -> message.message
        }
        string?.let { showToast(it) }
    }

    private fun getString(stringId: Int) = CwApp.app.getString(stringId)

    private fun showToast(message: String) {
        val toast = Toast.makeText(CwApp.app, message, Toast.LENGTH_SHORT)
        val textInToast = toast.view.findViewById<TextView>(android.R.id.message)
        textInToast?.gravity = Gravity.CENTER
        toast.show()
    }
}
