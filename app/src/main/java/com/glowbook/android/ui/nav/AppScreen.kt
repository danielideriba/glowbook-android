package com.glowbook.android.ui.nav

import android.net.Uri

sealed class AppScreen(val route: String) {
    protected fun buildRoute(vararg args: Any): String =
        args.fold(route) { acc, arg ->
            acc.replaceFirst(Regex("\\{[^}]+\\}"), Uri.encode(arg.toString()))
        }

    data object Home : AppScreen("home")
    data object Appointments : AppScreen("appointments")
    data object ProfessionalServices : AppScreen("professional/{professionalId}/services") {
        const val ARG_PROFESSIONAL_ID = "professionalId"
        fun createRoute(professionalId: Long) = buildRoute(professionalId)
    }
    data object Calendar : AppScreen("professional/{professionalId}/calendar?serviceIds={serviceIds}") {
        const val ARG_PROFESSIONAL_ID = "professionalId"
        const val ARG_SERVICE_IDS = "serviceIds"
        fun createRoute(professionalId: Long, serviceIds: Collection<Long>) =
            buildRoute(professionalId, serviceIds.joinToString(","))
    }
}
