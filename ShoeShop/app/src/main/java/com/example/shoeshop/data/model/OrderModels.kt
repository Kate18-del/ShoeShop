package com.example.shoeshop.data.model

import Product
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Serializable
data class Order(
    val id: Long = 0,
    val created_at: String = "",
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val user_id: String? = null,
    val payment_id: String? = null,
    val delivery_coast: Double? = 60.20,
    val status_id: String? = null
) {
    fun getFormattedTime(): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault())
            val date = format.parse(created_at)
            val now = Date()
            val diffInMillis = now.time - date.time
            val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)

            when {
                diffInMinutes < 60 -> "$diffInMinutes мин назад"
                diffInMinutes < 1440 -> {
                    val hours = diffInMinutes / 60
                    "$hours ч назад"
                }

                else -> {
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    timeFormat.format(date)
                }
            }
        } catch (e: Exception) {
            created_at
        }
    }

    fun getOrderDate(): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault())
            val date = format.parse(created_at)
            val now = Date()
            val diffInMillis = now.time - date.time
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

            when {
                diffInDays < 1 -> "Недавний"
                diffInDays < 2 -> "Вчера"
                else -> {
                    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
                    dateFormat.format(date)
                }
            }
        } catch (e: Exception) {
            "Недавний"
        }

    }
}
@Serializable
data class OrderItem(
    val id: String = "",
    val created_at: String = "",
    val title: String? = null,
    val coast: Double? = null,
    val count: Int? = null,
    val order_id: Long? = null,
    val product_id: String? = null,
    var product: Product? = null // Добавляем поле для продукта
)

@Serializable
data class OrderWithItems(
    val order: Order,
    val items: List<OrderItem> = emptyList()
)

data class OrderGroup(
    val date: String,
    val orders: List<OrderWithItems>
)