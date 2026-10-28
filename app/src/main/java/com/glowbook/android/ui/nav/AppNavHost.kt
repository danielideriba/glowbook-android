package com.glowbook.android.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.glowbook.android.ui.features.appointments.AppointmentsScreen
import com.glowbook.android.ui.features.calendar.CalendarScreen
import com.glowbook.android.ui.features.home.HomeScreen
import com.glowbook.android.ui.features.professional.ProfessionalScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.route
    ) {
        composable(AppScreen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(AppScreen.Appointments.route) {
            AppointmentsScreen(navController = navController)
        }
        composable(
            route = AppScreen.ProfessionalServices.route,
            arguments = listOf(
                navArgument(AppScreen.ProfessionalServices.ARG_PROFESSIONAL_ID) { type = NavType.LongType }
            )
        ) {
            ProfessionalScreen(navController = navController)
        }
        composable(
            route = AppScreen.Calendar.route,
            arguments = listOf(
                navArgument(AppScreen.Calendar.ARG_PROFESSIONAL_ID) { type = NavType.LongType },
                navArgument(AppScreen.Calendar.ARG_SERVICE_IDS) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            CalendarScreen(navController = navController)
        }
    }
}