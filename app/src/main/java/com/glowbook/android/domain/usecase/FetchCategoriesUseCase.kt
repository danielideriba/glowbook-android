package com.glowbook.android.domain.usecase

import com.glowbook.android.domain.models.CategoryEntity
import com.glowbook.android.domain.repository.CategoriesRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchCategoriesUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {
    fun invoke(): Flow<UiState<List<CategoryEntity>>> {
        return categoriesRepository.fetchCategories().map { result ->
            when (result) {
                is ApiState.Success -> UiState.Success(
                    result.data.map { item ->
                        CategoryEntity(
                            id = item.id,
                            name = item.nome
                        )
                    }
                )
                is ApiState.Error -> {
                    val errorMessage = when {
                        result.message.contains(
                            "not found",
                            ignoreCase = true
                        ) -> "Resources not found. Please check the spelling and try again."

                        else -> result.message
                    }
                    UiState.Error(errorMessage)
                }
            }
        }
    }
}
