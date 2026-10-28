package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.CategoriesApiServices
import com.glowbook.android.data.remote.model.CategoriesResponses
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class CategoriesDataSourceTest {

    private val apiService: CategoriesApiServices = mockk()
    private lateinit var dataSource: CategoriesDataSource

    @Before
    fun setUp() {
        dataSource = CategoriesDataSourceImpl(apiService)
    }

    @Test
    fun `fetchCategories returns categories from api`() = runTest {
        val categories = listOf(CategoriesResponses(1, "Cabelo"), CategoriesResponses(2, "Unhas"))
        coEvery { apiService.categories() } returns Response.success(categories)

        val response = dataSource.fetchCategories()

        assertTrue(response.isSuccessful)
        assertEquals(categories, response.body())
    }

    @Test
    fun `fetchCategories returns the exact api response`() = runTest {
        val apiResponse = Response.success(listOf(CategoriesResponses(1, "Cabelo")))
        coEvery { apiService.categories() } returns apiResponse

        val response = dataSource.fetchCategories()

        assertSame(apiResponse, response)
    }

    @Test
    fun `fetchCategories returns empty list when there are no categories`() = runTest {
        coEvery { apiService.categories() } returns Response.success(emptyList())

        val response = dataSource.fetchCategories()

        assertTrue(response.isSuccessful)
        assertEquals(emptyList<CategoriesResponses>(), response.body())
    }

    @Test
    fun `fetchCategories returns error response when api fails`() = runTest {
        val errorBody = """{"message":"unauthorized"}"""
            .toResponseBody("application/json".toMediaType())
        coEvery { apiService.categories() } returns Response.error(401, errorBody)

        val response = dataSource.fetchCategories()

        assertFalse(response.isSuccessful)
        assertEquals(401, response.code())
        assertNull(response.body())
    }

    @Test(expected = IOException::class)
    fun `fetchCategories propagates network exceptions`() = runTest {
        coEvery { apiService.categories() } throws IOException("no network")

        dataSource.fetchCategories()
    }

    @Test
    fun `fetchCategories calls api ordered by id once`() = runTest {
        coEvery { apiService.categories(any(), any()) } returns Response.success(emptyList())

        dataSource.fetchCategories()

        coVerify(exactly = 1) { apiService.categories(select = "*", order = "id.asc") }
        confirmVerified(apiService)
    }
}
