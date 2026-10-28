package com.glowbook.android.ui.nav

import android.net.Uri
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.URLEncoder

class AppScreenTest {

    @Before
    fun setUp() {
        // android.net.Uri is only a stub on the JVM; encode like the real implementation does.
        mockkStatic(Uri::class)
        every { Uri.encode(any()) } answers { URLEncoder.encode(firstArg<String>(), "UTF-8") }
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `static routes`() {
        assertEquals("home", AppScreen.Home.route)
        assertEquals("appointments", AppScreen.Appointments.route)
    }

    @Test
    fun `professional services route template and argument`() {
        assertEquals("professional/{professionalId}/services", AppScreen.ProfessionalServices.route)
        assertEquals("professionalId", AppScreen.ProfessionalServices.ARG_PROFESSIONAL_ID)
    }

    @Test
    fun `professional services createRoute fills professional id`() {
        assertEquals("professional/7/services", AppScreen.ProfessionalServices.createRoute(7))
    }

    @Test
    fun `calendar route template and arguments`() {
        assertEquals("professional/{professionalId}/calendar?serviceIds={serviceIds}", AppScreen.Calendar.route)
        assertEquals("professionalId", AppScreen.Calendar.ARG_PROFESSIONAL_ID)
        assertEquals("serviceIds", AppScreen.Calendar.ARG_SERVICE_IDS)
    }

    @Test
    fun `calendar createRoute joins and encodes service ids`() {
        assertEquals(
            "professional/1/calendar?serviceIds=1%2C2%2C3",
            AppScreen.Calendar.createRoute(1, listOf(1, 2, 3))
        )
    }

    @Test
    fun `calendar createRoute with single and no services`() {
        assertEquals("professional/1/calendar?serviceIds=5", AppScreen.Calendar.createRoute(1, setOf(5)))
        assertEquals("professional/1/calendar?serviceIds=", AppScreen.Calendar.createRoute(1, emptyList()))
    }
}
