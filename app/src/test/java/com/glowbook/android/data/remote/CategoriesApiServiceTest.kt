package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.CategoriesResponses
import com.glowbook.android.testutils.FakeApiInterceptor
import com.glowbook.android.testutils.createApiService
import com.glowbook.android.testutils.relativePath
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoriesApiServiceTest {

    private val interceptor = FakeApiInterceptor()
    private val apiService = createApiService(CategoriesApiServices::class.java, interceptor)

    @Test
    fun `categories sends GET to categorias ordered by id`() = runTest {
        apiService.categories()

        val request = interceptor.lastRequest
        assertEquals("GET", request.method)
        assertEquals("categorias", request.relativePath)
        assertEquals("*", request.url.queryParameter("select"))
        assertEquals("id.asc", request.url.queryParameter("order"))
    }

    @Test
    fun `categories parses response body`() = runTest {
        interceptor.responseBody = """[{"id":1,"nome":"Cabelo"},{"id":2,"nome":"Unhas"}]"""

        val response = apiService.categories()

        assertEquals(listOf(CategoriesResponses(1, "Cabelo"), CategoriesResponses(2, "Unhas")), response.body())
    }
}
