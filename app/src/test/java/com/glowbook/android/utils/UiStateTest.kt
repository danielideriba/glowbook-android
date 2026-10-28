package com.glowbook.android.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Test

class UiStateTest {

    @Test
    fun `success holds data`() {
        assertEquals(listOf(1, 2), UiState.Success(listOf(1, 2)).data)
    }

    @Test
    fun `error holds message`() {
        assertEquals("boom", UiState.Error("boom").message)
    }

    @Test
    fun `loading and ready are singletons and different`() {
        val loading: UiState<Nothing> = UiState.Loading
        val ready: UiState<Nothing> = UiState.Ready

        assertSame(UiState.Loading, loading)
        assertSame(UiState.Ready, ready)
        assertNotEquals(loading, ready)
    }
}
