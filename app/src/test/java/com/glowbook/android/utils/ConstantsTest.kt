package com.glowbook.android.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class ConstantsTest {

    @Test
    fun `PT_BR is brazilian portuguese`() {
        assertEquals(Locale("pt", "BR"), LocaleConstants.PT_BR)
        assertEquals("pt-BR", LocaleConstants.PT_BR.toLanguageTag())
    }

    @Test
    fun `network timeouts are 45 seconds`() {
        assertEquals(45, NetworkConstants.CONNECTION_TIMEOUT)
        assertEquals(45, NetworkConstants.READ_TIMEOUT)
        assertEquals(45, NetworkConstants.WRITE_TIMEOUT)
    }
}
