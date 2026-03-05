package com.example.shoeshop.data.repository

import android.util.Log
import com.example.shoeshop.data.RetrofitInstance
import com.example.shoeshop.data.model.*

class OrderRepository {

    private val service = RetrofitInstance.userManagementService
    private val tag = "OrderRepository"

    // Получить заказы пользователя
    suspend fun getUserOrders(userId: String, token: String): List<Order>? {
        return try {
            Log.d(tag, "Getting orders for user: $userId")
            val filter = "eq.$userId"
            val response = service.getUserOrders(filter, "Bearer $token")

            if (response.isSuccessful) {
                val orders = response.body()
                Log.d(tag, "Orders loaded: ${orders?.size}")
                orders
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in getUserOrders", e)
            null
        }
    }

    // Получить заказ по ID
    suspend fun getOrderById(orderId: Long, token: String): Order? {
        return try {
            Log.d(tag, "Getting order by id: $orderId")
            val filter = "eq.$orderId"
            val response = service.getOrderById(filter, "Bearer $token")

            if (response.isSuccessful) {
                val orders = response.body()
                orders?.firstOrNull()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in getOrderById", e)
            null
        }
    }

    // Получить позиции заказа
    suspend fun getOrderItems(orderId: Long, token: String): List<OrderItem>? {
        return try {
            Log.d(tag, "Getting items for order: $orderId")
            val filter = "eq.$orderId"
            val response = service.getOrderItems(filter, "Bearer $token")

            if (response.isSuccessful) {
                val items = response.body()
                Log.d(tag, "Items loaded: ${items?.size}")
                items
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in getOrderItems", e)
            null
        }
    }

    // Получить заказы с позициями
    suspend fun getOrdersWithItems(userId: String, token: String): List<OrderWithItems> {
        val orders = getUserOrders(userId, token) ?: return emptyList()

        val result = mutableListOf<OrderWithItems>()

        for (order in orders) {
            val items = getOrderItems(order.id, token) ?: emptyList()
            result.add(OrderWithItems(order, items))
        }

        return result
    }

    // Отменить заказ (просто помечаем как отмененный)
    suspend fun cancelOrder(orderId: Long, token: String): Boolean {
        return try {
            Log.d(tag, "Cancelling order: $orderId")
            val filter = "eq.$orderId"

            // ID статуса "Отменен" из вашей БД
            val cancelledStatusId = "8ac05d2f-8371-42f3-b2a9-7beac2fb2c75"

            val updates = mapOf(
                "status_id" to cancelledStatusId
            )
            val response = service.updateOrderStatus(filter, updates, "Bearer $token")

            if (response.isSuccessful) {
                Log.d(tag, "Order cancelled successfully")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in cancelOrder", e)
            false
        }
    }

    // Повторить заказ
    suspend fun repeatOrder(orderId: Long, userId: String, token: String): Boolean {
        return try {
            Log.d(tag, "Repeating order: $orderId")

            // Получаем старый заказ и его позиции
            val oldOrder = getOrderById(orderId, token) ?: return false
            val oldItems = getOrderItems(orderId, token) ?: emptyList()

            // Создаем новый заказ
            val createOrderRequest = CreateOrderRequest(
                user_id = userId,
                email = oldOrder.email ?: "",
                phone = oldOrder.phone ?: "",
                address = oldOrder.address ?: "",
                delivery_coast = oldOrder.delivery_coast?.toInt() ?: 60,
                status_id = "970aed1e-549c-499b-a649-4bf3f9f93a01" // "Собираем"
            )

            val orderResponse = service.createOrder(createOrderRequest, "Bearer $token")

            if (!orderResponse.isSuccessful) {
                Log.e(tag, "Failed to create new order")
                return false
            }

            val newOrder = orderResponse.body()?.firstOrNull() ?: return false

            // Создаем позиции для нового заказа
            val newItems = oldItems.map { item ->
                CreateOrderItemRequest(
                    order_id = newOrder.id,
                    product_id = item.product_id ?: "",
                    title = item.title ?: "",
                    coast = item.coast ?: 0.0,
                    count = item.count ?: 1
                )
            }

            val itemsResponse = service.createOrderItems(newItems, "Bearer $token")
            itemsResponse.isSuccessful

        } catch (e: Exception) {
            Log.e(tag, "Exception in repeatOrder", e)
            false
        }
    }
}