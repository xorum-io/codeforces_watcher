package io.xorum.codeforceswatcher.util

object AnalyticsEvents {

    const val USERS_REFRESH = "users_list_refresh"
    const val USER_ADDED = "user_added"

    const val CONTESTS_REFRESH = "contests_list_refresh"
    const val CONTEST_OPENED = "contest_opened"
    const val CONTEST_SHARED = "contest_shared"
    const val ADD_CONTEST_TO_CALENDAR = "add_contest_to_google_calendar"

    const val ACTIONS_REFRESH = "actions_list_refresh"
    const val SHARE_APP = "actions_share_app"
    const val APP_SHARED = "actions_app_shared"
    const val ACTION_SHARED = "action_share_comment"
    const val ACTION_OPENED = "action_opened"
    const val PINNED_POST_OPENED = "actions_pinned_post_opened"
    const val PINNED_POST_CLOSED = "actions_pinned_post_closed"
    const val NEWS_FETCH = "news_fetch"
    const val NEWS_FETCH_SUCCESS = "news_fetch_success"
    const val NEWS_FETCH_FAILURE = "news_fetch_failure"
    const val VIDEO_OPENED = "video_opened"
    const val VIDEO_SHARED = "video_shared"
    const val FORGOT_PASSWORD_PRESSED = "forgot_password_pressed"

    const val PROBLEMS_REFRESH = "problems_list_refresh"
    const val PROBLEM_OPENED = "problem_opened"
    const val PROBLEM_SHARED = "problem_shared"
}