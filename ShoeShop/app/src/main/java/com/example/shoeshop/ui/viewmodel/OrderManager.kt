package com.example.shoeshop.data

import com.example.shoeshop.data.model.OrderWithItems
import com.example.shoeshop.data.model.OrderGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object OrderManager {

    private val _orders = MutableStateFlow<List<OrderWithItems>>(emptyList())
    val orders: StateFlow<List<OrderWithItems>> = _orders.asStateFlow()

    private val _groupedOrders = MutableStateFlow<List<OrderGroup>>(emptyList())
    val groupedOrders: StateFlow<List<OrderGroup>> = _groupedOrders.asStateFlow()

    fun setOrders(orders: List<OrderWithItems>) {
        _orders.value = orders
        groupOrders()
    }

    private fun groupOrders() {
        val grouped = _orders.value
            .groupBy { it.order.getOrderDate() }
            .map { (date, orders) -> OrderGroup(date, orders) }
            .sortedByDescending { group ->
                when (group.date) {
                    "Недавний" -> 3
                    "Вчера" -> 2
                    else -> 1
                }
            }

        _groupedOrders.value = grouped
    }

    fun removeOrder(orderId: Long) {
        _orders.value = _orders.value.filter { it.order.id != orderId }
        groupOrders()
    }

    fun clear() {
        _orders.value = emptyList()
        _groupedOrders.value = emptyList()
    }
}