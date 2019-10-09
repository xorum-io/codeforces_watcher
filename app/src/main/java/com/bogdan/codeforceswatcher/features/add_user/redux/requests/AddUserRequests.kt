package com.bogdan.codeforceswatcher.features.add_user.redux.requests

import com.bogdan.codeforceswatcher.CwApp
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.users.redux.requests.RatingChangeResponse
import com.bogdan.codeforceswatcher.features.users.redux.requests.UsersResponse
import com.bogdan.codeforceswatcher.model.User
import com.bogdan.codeforceswatcher.network.RestClient
import com.bogdan.codeforceswatcher.redux.Request
import com.bogdan.codeforceswatcher.redux.actions.ToastAction
import com.bogdan.codeforceswatcher.room.DatabaseClient
import com.bogdan.codeforceswatcher.store
import org.rekotlin.Action
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddUserRequests {

    class AddUser(
        val handle: String
    ) : Request() {
        override fun execute() {
            val userCall = RestClient.getUsers(handle)
            val ratingCall = RestClient.getRating(handle)

            userCall.enqueue(object : Callback<UsersResponse> {
                override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                    response.body()?.users?.firstOrNull()?.let { user ->
                        fetchRatingChanges(ratingCall, user)
                    } ?: store.dispatch(Failure(CwApp.app.getString(R.string.wrong_handle)))

                }

                override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                    store.dispatch(Failure(CwApp.app.getString(R.string.no_internet_connection)))
                }
            })
        }

        private fun fetchRatingChanges(ratingCall: Call<RatingChangeResponse>, user: User) {
            ratingCall.enqueue(object : Callback<RatingChangeResponse> {
                override fun onResponse(
                    call: Call<RatingChangeResponse>,
                    response: Response<RatingChangeResponse>
                ) {
                    response.body()?.ratingChanges?.let { ratingChanges ->
                        user.ratingChanges = ratingChanges
                        val foundUser = DatabaseClient.userDao.getAll()
                            .find { it.handle == user.handle }

                        if (foundUser == null) {
                            user.id = DatabaseClient.userDao.insert(user)
                            store.dispatch(Success(user))
                        } else
                            store.dispatch(Failure(CwApp.app.getString(R.string.user_already_added)))
                    } ?: store.dispatch(Failure(CwApp.app.getString(R.string.wrong_handle)))

                }

                override fun onFailure(call: Call<RatingChangeResponse>, t: Throwable) {
                    store.dispatch(Failure(CwApp.app.getString(R.string.no_internet_connection)))
                }
            })
        }

        data class Success(val user: User) : Action

        data class Failure(override val message: String) : ToastAction
    }

}