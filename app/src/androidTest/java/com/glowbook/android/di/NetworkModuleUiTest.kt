package com.glowbook.android.di

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.glowbook.android.data.remote.ApiClientConstants
import com.glowbook.android.data.remote.AppointmentsApiService
import com.glowbook.android.data.remote.AppointmentsHistoryApiService
import com.glowbook.android.data.remote.AppointmentsUpNextApiService
import com.glowbook.android.data.remote.CategoriesApiServices
import com.glowbook.android.data.remote.CreateAppointmentApiService
import com.glowbook.android.data.remote.ProfessionalsApiService
import com.glowbook.android.data.remote.ServicesApiService
import com.glowbook.android.testutils.FakeRetrofit
import com.glowbook.android.testutils.FakeSupabase
import com.glowbook.android.ui.MainActivity
import com.glowbook.android.ui.features.calendar.model.toDayLabel
import com.google.gson.JsonParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Runs the app UI on top of the real Hilt graph, replacing only NetworkModule:
 * clients keep the real apikey/bearer interceptors and end in [FakeSupabase]
 * instead of the network, so nothing is written to Supabase.
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(NetworkModule::class)
@RunWith(AndroidJUnit4::class)
class NetworkModuleUiTest {

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

    // Must run before the compose rule launches MainActivity (which loads the Home right away).
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
    }

    private fun waitForText(text: String) {
        composeRule.waitUntilAtLeastOneExists(hasText(text), timeoutMillis = 10_000)
    }

    private fun openCalendarWithCorteAndEscova() {
        waitForText("Camila Rocha")
        composeRule.onNodeWithText("Camila Rocha").performClick()

        waitForText("Escova")
        composeRule.onNodeWithText("Corte").performClick()
        composeRule.onNodeWithText("Escova").performClick()
        composeRule.onNodeWithText("2 serviços • 2h 15min").assertIsDisplayed()
        composeRule.onNodeWithText("R$ 260,00").assertIsDisplayed()
        composeRule.onNodeWithText("Escolher horário →").performClick()

        waitForText("Escolha a data e horário")
    }

    @Test
    fun home_showsDataLoadedThroughInjectedApiServices() {
        waitForText("Camila Rocha")

        composeRule.onNodeWithText("Bea Lima").assertIsDisplayed()
        composeRule.onNodeWithText("Cabelo").assertIsDisplayed()
        composeRule.onNodeWithText("Unhas").assertIsDisplayed()
        waitForText("Corte")

        val paths = FakeSupabase.requests.map { it.path }.toSet()
        assertEquals(true, paths.containsAll(listOf("profissionais", "categorias", "proximos_agendamentos", "servicos")))
    }

    @Test
    fun readRequests_carryApiKeyButNoBearer() {
        waitForText("Camila Rocha")

        val reads = FakeSupabase.requests.filter { it.method == "GET" }
        assertEquals(true, reads.isNotEmpty())
        reads.forEach { request ->
            assertEquals(ApiClientConstants.API_KEY, request.headers[ApiClientConstants.HEADER_PARAM_API_KEY])
            assertNull(request.headers[ApiClientConstants.HEADER_PARAM_AUTHORIZATION])
        }
    }

    @Test
    fun professionalServices_loadsProfessionalByIdAndAllServices() {
        waitForText("Camila Rocha")
        composeRule.onNodeWithText("Camila Rocha").performClick()

        waitForText("Manicure")
        composeRule.onNodeWithText("ESCOLHA OS SERVIÇOS").assertIsDisplayed()
        composeRule.onNodeWithText("Selecionar todos").performClick()
        composeRule.onNodeWithText("3 serviços • 3h 15min").assertIsDisplayed()
        composeRule.onNodeWithText("R$ 320,00").assertIsDisplayed()
    }

    @Test
    fun calendar_marksBookedSlotAsTaken() {
        openCalendarWithCorteAndEscova()
        val secondDay = FakeSupabase.calendarDays[1]

        composeRule.onNodeWithText(secondDay.dayOfMonth.toString()).performClick()

        waitForText("11:00")
        composeRule.onNodeWithText("11:00").assertIsNotEnabled()
    }

    @Test
    fun confirmBooking_postsRpcThroughBearerClientAndOpensAppointments() {
        openCalendarWithCorteAndEscova()
        val secondDay = FakeSupabase.calendarDays[1]

        composeRule.onNodeWithText(secondDay.dayOfMonth.toString()).performClick()
        waitForText("14:00")
        composeRule.onNodeWithText("14:00").performClick()
        composeRule.onNodeWithText("${secondDay.toDayLabel()} • 14:00").assertIsDisplayed()
        composeRule.onNodeWithText("Confirmar agendamento").performClick()

        composeRule.waitUntil(10_000) { FakeSupabase.rpcRequests().isNotEmpty() }
        val rpc = FakeSupabase.rpcRequests().single()
        assertEquals("POST", rpc.method)
        assertEquals(ApiClientConstants.API_KEY, rpc.headers[ApiClientConstants.HEADER_PARAM_API_KEY])
        assertEquals(ApiClientConstants.BEARER_ANON, rpc.headers[ApiClientConstants.HEADER_PARAM_AUTHORIZATION])
        assertEquals(ApiClientConstants.CONTENT_TYPE_JSON, rpc.headers[ApiClientConstants.HEADER_PARAM_CONTENT_TYPE])

        val body = JsonParser.parseString(rpc.body).asJsonObject
        assertEquals(secondDay.toString(), body["p_data"].asString)
        assertEquals("14:00:00", body["p_horario"].asString)
        assertEquals(1L, body["p_profissional_id"].asLong)
        assertEquals(listOf(1L, 2L), body["p_servicos"].asJsonArray.map { it.asLong }.sorted())

        // Success navigates to "Meus agendamentos".
        waitForText("Cancelados")
        composeRule.onNodeWithText("Próximos").assertIsDisplayed()
    }

    @Test
    fun confirmBooking_rpcErrorKeepsUserOnCalendar() {
        FakeSupabase.createAppointmentCode = 400
        openCalendarWithCorteAndEscova()
        val secondDay = FakeSupabase.calendarDays[1]

        composeRule.onNodeWithText(secondDay.dayOfMonth.toString()).performClick()
        waitForText("14:00")
        composeRule.onNodeWithText("14:00").performClick()
        composeRule.onNodeWithText("Confirmar agendamento").performClick()

        composeRule.waitUntil(10_000) { FakeSupabase.rpcRequests().isNotEmpty() }
        waitForText("Confirmar agendamento")
        composeRule.onNodeWithText("Escolha a data e horário").assertIsDisplayed()
        assertEquals(1, FakeSupabase.rpcRequests().size)
    }
}
