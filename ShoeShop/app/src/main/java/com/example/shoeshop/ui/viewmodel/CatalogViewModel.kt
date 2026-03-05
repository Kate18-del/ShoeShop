package com.example.shoeshop.ui.viewmodel

import Category
import Product
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoeshop.data.AuthManager
import com.example.shoeshop.data.repository.CatalogRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.map

data class CatalogState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val allProducts: List<Product> = emptyList(),
    val bestSellers: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategoryId: String? = null,
    val selectedCategoryName: String = "Все",
    val error: String? = null
)

class CatalogViewModel : ViewModel() {

    private val repository = CatalogRepository()
    private val _state = MutableStateFlow(CatalogState())
    val state: StateFlow<CatalogState> = _state.asStateFlow()

    private var currentToken: String = ""

    init {
        viewModelScope.launch {
            AuthManager.accessToken.collect { token ->
                if (token != null) {
                    currentToken = token
                    loadCatalog()
                }
            }
        }
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Загружаем категории с сервера
                val categories = repository.getCategories(currentToken) ?: emptyList()

                // Фильтруем категории - оставляем только Outdoor и Tennis
                val filteredCategories = categories.filter { category ->
                    category.title == "Outdoor" || category.title == "Tennis"
                }

                // Создаем категорию "Все" для UI
                val allCategory = Category(id = "all", title = "Все", isSelected = true)

                // Формируем список категорий для отображения
                val displayCategories = listOf(allCategory) +
                        filteredCategories.map { it.copy(isSelected = false) }

                // Загружаем товары
                val products = repository.getProducts(currentToken) ?: emptyList()

                _state.update {
                    it.copy(
                        isLoading = false,
                        categories = displayCategories,
                        allProducts = products,
                        bestSellers = products.filter { product -> product.is_best_seller == true },
                        filteredProducts = products,
                        selectedCategoryId = "all",
                        selectedCategoryName = "Все"
                    )
                }

            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error loading catalog", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectCategory(categoryName: String) {
        val currentState = _state.value

        // Находим выбранную категорию
        val selectedCategory = currentState.categories.find { it.title == categoryName }

        if (selectedCategory != null) {
            // Обновляем isSelected для всех категорий
            val updatedCategories = currentState.categories.map {
                it.copy(isSelected = it.title == categoryName)
            }

            _state.update {
                it.copy(
                    categories = updatedCategories,
                    selectedCategoryId = selectedCategory.id,
                    selectedCategoryName = selectedCategory.title
                )
            }

            // Фильтруем товары
            if (selectedCategory.id == "all") {
                _state.update { it.copy(filteredProducts = currentState.allProducts) }
            } else {
                viewModelScope.launch {
                    val filtered = repository.getProductsByCategoryId(currentToken, selectedCategory.id)
                    _state.update { it.copy(filteredProducts = filtered ?: emptyList()) }
                }
            }
        }
    }

    fun refresh() {
        loadCatalog()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}