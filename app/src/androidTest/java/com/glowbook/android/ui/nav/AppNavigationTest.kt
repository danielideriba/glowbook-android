package com.glowbook.android.ui.nav

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.glowbook.android.data.remote.AppointmentsApiService
import com.glowbook.android.data.remote.AppointmentsHistoryApiService
import com.glowbook.android.data.remote.AppointmentsUpNextApiService
import com.glowbook.android.data.remote.CategoriesApiServices
import com.glowbook.android.data.remote.CreateAppointmentApiService
import com.glowbook.android.data.remote.ProfessionalsApiService
import com.glowbook.android.data.remote.ServicesApiService
import com.glowbook.android.di.CreateAppointmentClient
import com.glowbook.android.di.NetworkModule
import com.glowbook.android.testutils.FakeRetrofit
import com.glowbook.android.testutils.FakeSupabase
import com.glowbook.android.ui.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith
import retrofit2.Retrofit
import javax.inject.Singleton

/** Drives AppNavHost through MainActivity with real screens/ViewModels and a fake backend. */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(NetworkModule::class)
@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @Module
    @InstallIn(SingletonComponent::class)
    object FakeNetworkModule {

        @Provides
        @Singleton
        fun provideRetrofit(): Retrofit = FakeRetrofit.read()

        @Provides
        @Singleton
        @CreateAppointmentClient
        fun provideCreateAppointmentRetrofit(): Retrofit = FakeRetrofit.createAppointment()

        @Provides
        fun provideProfessionalsApiService(retrofit: Retrofit): ProfessionalsApiService =
            NetworkModule.provideProfessionalsApiService(retrofit)

        @Provides
        fun provideCategoriesApiServices(retrofit: Retrofit): CategoriesApiServices =
            NetworkModule.provideCategoriesApiServices(retrofit)

        @Provides
        fun provideAppointmentsUpNextApiService(retrofit: Retrofit): AppointmentsUpNextApiService =
            NetworkModule.provideAppointmentsUpNextApiService(retrofit)

        @Provides
        fun provideAppointmentsHistoryApiService(retrofit: Retrofit): AppointmentsHistoryApiService =
            NetworkModule.provideAppointmentsHistoryApiService(retrofit)

        @Provides
        fun provideServicesApiService(retrofit: Retrofit): ServicesApiService =
            NetworkModule.provideServicesApiService(retrofit)

        @Provides
        fun provideAppointmentsApiService(retrofit: Retrofit): AppointmentsApiService =
            NetworkModule.provideAppointmentsApiService(retrofit)

        @Provides
        fun provideCreateAppointmentApiService(@CreateAppointmentClient retrofit: Retrofit): CreateAppointmentApiService =
            NetworkModule.provideCreateAppointmentApiService(retrofit)
    }

    @get:Rule(order = 0)
    val resetFakeSupabase = object : ExternalResource() {
        override fun before() = FakeSupabase.reset()
    }

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        waitForText("Camila Rocha")
    }

    private fun waitForText(text: String) {
        composeRule.waitUntilAtLeastOneExists(hasText(text), timeoutMillis = 10_000)
    }

    private fun assertOnHome() {
        waitForText("PROFISSIONAIS DISPONÍVEIS")
        composeRule.onNodeWithText("Olá, Usuário 👋").assertIsDisplayed()
    }

    private fun assertOnAppointments() {
        waitForText("Meus agendamentos")
        composeRule.onNodeWithText("Próximos").assertIsDisplayed()
    }

    private fun openServicesOfCamila() {
        composeRule.onNodeWithText("Camila Rocha").performClick()
        waitForText("ESCOLHA OS SERVIÇOS")
    }

    @Test
    fun startsOnHome() {
        assertOnHome()
    }

    @Test
    fun bottomNavScheduleOpensAppointments_andHomeGoesBack() {
        composeRule.onNodeWithText("Agenda").performClick()
        assertOnAppointments()

        composeRule.onNodeWithText("Início").performClick()
        assertOnHome()
        composeRule.onAllNodesWithText("Meus agendamentos").assertCountEquals(0)
    }

    @Test
    fun nextAppointmentCardOpensAppointments_andBackArrowReturns() {
        waitForText("PRÓXIMO AGENDAMENTO")
        composeRule.onNodeWithText("PRÓXIMO AGENDAMENTO").performClick()
        assertOnAppointments()

        composeRule.onNodeWithText("←").performClick()
        assertOnHome()
    }

    @Test
    fun professionalCardOpensServicesOfThatProfessional() {
        openServicesOfCamila()

        composeRule.onNodeWithText("★ 4.9 • Cabeleireira").assertIsDisplayed()
        val byId = FakeSupabase.requests.filter { it.path == "profissionais" }
        assertEquals(true, byId.isNotEmpty())
    }

    @Test
    fun servicesBackArrowReturnsHome() {
        openServicesOfCamila()

        composeRule.onNodeWithText("←").performClick()

        assertOnHome()
    }

    @Test
    fun servicesSystemBackReturnsHome() {
        openServicesOfCamila()

        Espresso.pressBack()

        assertOnHome()
    }

    @Test
    fun continueOpensCalendarWithSelectedServices_andBackReturnsToServices() {
        openServicesOfCamila()
        composeRule.onNodeWithText("Corte").performClick()
        composeRule.onNodeWithText("Escolher horário →").performClick()

        waitForText("Escolha a data e horário")
        composeRule.onNodeWithText("Corte").assertIsDisplayed() // summary shows the selected service

        composeRule.onNodeWithText("←").performClick()
        waitForText("ESCOLHA OS SERVIÇOS")
    }

    @Test
    fun calendarRequestsOnlySelectedServices() {
        openServicesOfCamila()
        composeRule.onNodeWithText("Escova").performClick()
        composeRule.onNodeWithText("Escolher horário →").performClick()
        waitForText("Escolha a data e horário")

        composeRule.waitUntil(10_000) { FakeSupabase.requests.any { it.path == "agendamentos" } }
        composeRule.onNodeWithText("Escova").assertIsDisplayed()
        composeRule.onAllNodesWithText("Corte").assertCountEquals(0)
    }

    @Test
    fun bookingSuccessReplacesFlowWithAppointments_andBackGoesHome() {
        openServicesOfCamila()
        composeRule.onNodeWithText("Corte").performClick()
        composeRule.onNodeWithText("Escolher horário →").performClick()
        waitForText("Escolha a data e horário")

        composeRule.onNodeWithText(FakeSupabase.calendarDays[1].dayOfMonth.toString()).performClick()
        waitForText("14:00")
        composeRule.onNodeWithText("14:00").performClick()
        composeRule.onNodeWithText("Confirmar agendamento").performClick()
        assertOnAppointments()

        // popUpTo(Home): services and calendar are no longer in the back stack.
        Espresso.pressBack()
        assertOnHome()
    }
}
