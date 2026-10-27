package com.glowbook.android.ui.nav

import android.net.Uri

sealed class AppScreen(val route: String) {
    protected fun buildRoute(vararg args: Any): String =
        args.fold(route) { acc, arg ->
            acc.replaceFirst(Regex("\\{[^}]+\\}"), Uri.encode(arg.toString()))
        }

    data object Home : AppScreen("home")
}