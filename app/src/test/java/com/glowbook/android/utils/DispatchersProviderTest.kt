package com.glowbook.android.utils

import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertSame
import org.junit.Test

class DispatchersProviderTest {

    private val provider: DispatchersProvider = DefaultDispatcherProvider()

    @Test
    fun `default returns Dispatchers Default`() {
        assertSame(Dispatchers.Default, provider.default())
    }

    @Test
    fun `io returns Dispatchers IO`() {
        assertSame(Dispatchers.IO, provider.io())
    }

    @Test
    fun `main returns Dispatchers Main`() {
        assertSame(Dispatchers.Main, provider.main())
    }

    @Test
    fun `unconfined returns Dispatchers Unconfined`() {
        assertSame(Dispatchers.Unconfined, provider.unconfined())
    }
}
