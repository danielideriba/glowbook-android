package com.glowbook.android.di

import android.app.Application
import android.content.Context
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Test

class AppModuleTest {

    @Test
    fun `provideContext returns application context`() {
        val appContext: Context = mockk()
        val application: Application = mockk { every { applicationContext } returns appContext }

        assertSame(appContext, AppModule.provideContext(application))
    }
}
