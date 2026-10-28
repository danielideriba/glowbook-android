package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.CategoriesDataSource
import com.glowbook.android.data.remote.model.CategoriesResponses
import com.glowbook.android.domain.repository.CategoriesRepository
import com.glowbook.android.testutils.TestDispatchersProvider
import com.glowbook.android.utils.ApiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class CategoriesRepositoryImplTest {

    private val categoriesDataSource: CategoriesDataSource = mockk()
    private val repository: CategoriesRepository =
        CategoriesRepositoryImpl(categoriesDataSource, TestDispatchersProvider())

    @Test
    fun `emits categories on success`() = runTest {
        val categories = listOf(CategoriesResponses(1, "Cabelo"), CategoriesResponses(2, "Unhas"))
        coEvery { categoriesDataSource.fetchCategories() } returns Response.success(categories)

        val emissions = repository.fetchCategories().toList()

        assertEquals(listOf(ApiState.Success(categories)), emissions)
    }

    @Test
    fun `emits empty list when there are no categories`() = runTest {
        coEvery { categoriesDataSource.fetchCategories() } returns Response.success(emptyList())

        val result = repository.fetchCategories().toList().single()

        assertEquals(ApiState.Success(emptyList<CategoriesResponses>()), result)
    }

    @Test
    fun `emits error when api fails`() = runTest {
        coEvery { categoriesDataSource.fetchCategories() } returns errorResponse(500)

        val result = repository.fetchCategories().toList().single()

        assertEquals(500, (result as ApiState.Error).code)
    }

    @Test
    fun `emits error when network fails`() = runTest {
        coEvery { categoriesDataSource.fetchCategories() } throws IOException("no network")

        val result = repository.fetchCategories().toList().single()

        assertEquals(ApiState.Error("no network", -1), result)
    }
}
