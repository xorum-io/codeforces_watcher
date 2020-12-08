package io.xorum.codeforceswatcher.util

fun Throwable.stringify(): String {
    var trace = this.toString() + "\n"

    for (e1 in this.getStackTrace()) {
        trace += "\t at ${e1.toString()} \n"
    }

    return trace
}