package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.ProfessionalsDataSource
import com.glowbook.android.data.remote.model.ProfessionalsResponse
import com.glowbook.android.domain.repository.ProfessionalsRepository
import com.glowbook.android.testutils.TestDispatchersProvider
import com.glowbook.android.utils.ApiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ProfessionalsRepositoryImplTest {

    private val professionalsDataSource: ProfessionalsDataSource = mockk()
    private val repository: ProfessionalsRepository =
        ProfessionalsRepositoryImpl(professionalsDataSource, TestDispatchersProvider())

    @Test
    fun `emits available professionals on success`() = runTest {
        val professionals = listOf(professional(1), professional(2))
        coEvery { professionalsDataSource.fetchProfessionalsAvailable(30) } returns Response.success(professionals)

        val emissions = repository.fetchProfessionalsAvailable(30).toList()

        assertEquals(listOf(ApiState.Success(professionals)), emissions)
    }

    @Test
    fun `passes limit to data source`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalsAvailable(any()) } returns Response.success(emptyList())

        repository.fetchProfessionalsAvailable(5).toList()

        coVerify(exactly = 1) { professionalsDataSource.fetchProfessionalsAvailable(5) }
    }

    @Test
    fun `emits empty list when nobody is available`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalsAvailable(30) } returns Response.success(emptyList())

        val result = repository.fetchProfessionalsAvailable(30).toList().single()

        assertEquals(ApiState.Success(emptyList<ProfessionalsResponse>()), result)
    }

    @Test
    fun `emits error when api fails`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalsAvailable(30) } returns errorResponse(404)

        val result = repository.fetchProfessionalsAvailable(30).toList().single()

        assertEquals(ApiState.Error("Not found", 404), result)
    }

    @Test
    fun `emits error when network fails`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalsAvailable(30) } throws IOException("no network")

        val result = repository.fetchProfessionalsAvailable(30).toList().single()

        assertEquals(ApiState.Error("no network", -1), result)
    }
}
