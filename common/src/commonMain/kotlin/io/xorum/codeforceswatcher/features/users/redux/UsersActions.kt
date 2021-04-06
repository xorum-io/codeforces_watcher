package io.xorum.codeforceswatcher.features.users.redux

import tw.geothings.rekotlin.Action

class UsersActions {

    data class Sort(val sortType: UsersState.SortType) : Action

    class ClearAddUserState : Action
}
