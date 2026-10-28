package com.glowbook.android.data.remote.model

import com.glowbook.android.di.NetworkModule
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoriesResponsesTest {

    private val gson = NetworkModule.provideGson()

    @Test
    fun `deserializes supabase json into all fields`() {
        val category = gson.fromJson("""{"id":2,"nome":"Unhas"}""", CategoriesResponses::class.java)

        assertEquals(2L, category.id)
        assertEquals("Unhas", category.nome)
    }

    @Test
    fun `deserializes supabase json array into list`() {
        val type = object : TypeToken<List<CategoriesResponses>>() {}.type

        val categories: List<CategoriesResponses> =
            gson.fromJson("""[{"id":1,"nome":"Cabelo"},{"id":2,"nome":"Unhas"}]""", type)

        assertEquals(listOf(CategoriesResponses(1, "Cabelo"), CategoriesResponses(2, "Unhas")), categories)
    }

    @Test
    fun `serializes to json with same field names`() {
        assertEquals("""{"id":2,"nome":"Unhas"}""", gson.toJson(CategoriesResponses(2, "Unhas")))
    }
}
