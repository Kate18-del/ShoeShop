package com.example.shoeshop.data

import Product
import android.util.Log
import com.example.shoeshop.data.model.Cart
import com.example.shoeshop.data.repository.CartRepository
import com.example.shoeshop.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CartItem(
    val cart: Cart,
    val product: Product?
) {
    val totalPrice: Double get() = (product?.cost ?: 0.0) * cart.count
}

object CartManager {

    private val cartRepository = CartRepository()
    private val productRepository = ProductRepository()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private var currentUserId: String = ""
    private var currentToken: String = ""

    val subtotal: Double
        get() = _cartItems.value.sumOf { it.totalPrice }

    val deliveryCost: Double
        get() = 60.20 // Фиксированная стоимость доставки

    val total: Double
        get() = subtotal + deliveryCost

    fun init(userId: String, token: String) {
        currentUserId = userId
        currentToken = token
        loadCart()
    }

    fun loadCart() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cart = cartRepository.getCart(currentUserId, currentToken) ?: emptyList()

                val cartItemsWithProducts = mutableListOf<CartItem>()

                for (cartItem in cart) {
                    val product = productRepository.getProductById(cartItem.product_id, currentToken)
                    cartItemsWithProducts.add(CartItem(cartItem, product))
                }

                withContext(Dispatchers.Main) {
                    _cartItems.value = cartItemsWithProducts
                }

                Log.d("CartManager", "Loaded ${cart.size} cart items")

            } catch (e: Exception) {
                Log.e("CartManager", "Error loading cart", e)
            }
        }
    }

    fun increaseQuantity(cartId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentItem = _cartItems.value.find { it.cart.id == cartId }
            if (currentItem != null) {
                val newCount = currentItem.cart.count + 1
                val success = cartRepository.updateCartItemCount(cartId, newCount, currentToken)

                if (success) {
                    withContext(Dispatchers.Main) {
                        _cartItems.value = _cartItems.value.map { item ->
                            if (item.cart.id == cartId) {
                                CartItem(
                                    cart = item.cart.copy(count = newCount),
                                    product = item.product
                                )
                            } else {
                                item
                            }
                        }
                    }
                }
            }
        }
    }

    fun decreaseQuantity(cartId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentItem = _cartItems.value.find { it.cart.id == cartId }
            if (currentItem != null) {
                val newCount = maxOf(1, currentItem.cart.count - 1)
                val success = cartRepository.updateCartItemCount(cartId, newCount, currentToken)

                if (success) {
                    withContext(Dispatchers.Main) {
                        _cartItems.value = _cartItems.value.map { item ->
                            if (item.cart.id == cartId) {
                                CartItem(
                                    cart = item.cart.copy(count = newCount),
                                    product = item.product
                                )
                            } else {
                                item
                            }
                        }
                    }
                }
            }
        }
    }

    fun removeFromCart(cartId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val success = cartRepository.removeFromCart(cartId, currentToken)

            if (success) {
                withContext(Dispatchers.Main) {
                    _cartItems.value = _cartItems.value.filter { it.cart.id != cartId }
                }
            }
        }
    }

    fun clear() {
        _cartItems.value = emptyList()
    }

    fun addToCart(productId: String, onComplete: (Boolean) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = cartRepository.addToCart(currentUserId, productId, currentToken)

                withContext(Dispatchers.Main) {
                    if (result != null) {
                        // Перезагружаем корзину
                        loadCart()
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("CartManager", "Error in addToCart", e)
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }
}