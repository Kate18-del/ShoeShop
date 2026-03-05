package com.example.shoeshop.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.shoeshop.R
import com.example.shoeshop.data.OrderManager
import com.example.shoeshop.data.model.Order
import com.example.shoeshop.data.model.OrderWithItems
import com.example.shoeshop.data.repository.OrderRepository
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    userId: String,
    token: String,
    onBackClick: () -> Unit,
    onOrderClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val orderRepository = remember { OrderRepository() }

    val groupedOrders by OrderManager.groupedOrders.collectAsState()
    val swipeStates = remember { mutableStateMapOf<Long, Float>() }

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        val orders = orderRepository.getOrdersWithItems(userId, token)
        OrderManager.setOrders(orders)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Заказы",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (groupedOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.bag_2),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "У вас пока нет заказов",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedOrders.forEach { group ->
                    item {
                        Text(
                            text = group.date,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(group.orders) { orderWithItems ->
                        SwipeableOrderCard(
                            orderWithItems = orderWithItems,
                            onRepeat = {
                                scope.launch {
                                    val success = orderRepository.repeatOrder(
                                        orderWithItems.order.id,
                                        userId,
                                        token
                                    )
                                    if (success) {
                                        Toast.makeText(context, "Заказ повторен", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onCancel = {
                                scope.launch {
                                    val success = orderRepository.cancelOrder(
                                        orderWithItems.order.id,
                                        token
                                    )
                                    if (success) {
                                        OrderManager.removeOrder(orderWithItems.order.id)
                                        Toast.makeText(context, "Заказ отменен", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onClick = { onOrderClick(orderWithItems.order.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeableOrderCard(
    orderWithItems: OrderWithItems,
    onRepeat: () -> Unit,
    onCancel: () -> Unit,
    onClick: () -> Unit
) {
    val order = orderWithItems.order
    val firstItem = orderWithItems.items.firstOrNull()
    val product = firstItem?.product
    var offsetX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        // Левая панель с кнопкой повтора (свайп вправо)
        if (offsetX > 20) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 100.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .zIndex(0f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        onRepeat()
                        offsetX = 0f
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Повторить",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Правая панель с кнопкой отмены (свайп влево)
        if (offsetX < -20) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 100.dp)
                    .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .zIndex(0f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        onCancel()
                        offsetX = 0f
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Отменить",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Карточка заказа
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(offsetX) < 80) {
                                offsetX = 0f
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount).coerceIn(-150f, 150f)
                    }
                }
                .clickable { onClick() }
                .zIndex(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Картинка товара
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5))
                ) {
                    if (product?.imageResId != null && product.imageResId != 0) {
                        Image(
                            painter = painterResource(id = product.imageResId),
                            contentDescription = product.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Заглушка
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👟",
                                fontSize = 40.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Информация о заказе
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "N° ${order.id}    ${order.getFormattedTimeAgo()}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (firstItem != null) {
                        Text(
                            text = firstItem.title ?: "",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (orderWithItems.items.size > 1) {
                            Text(
                                text = "и ещё ${orderWithItems.items.size - 1} товар(а)",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Цена
                    val totalPrice = orderWithItems.items.sumOf { (it.coast ?: 0.0) * (it.count ?: 1) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (orderWithItems.items.size == 1 && firstItem?.coast != null) {
                            Text(
                                text = "₽${String.format("%.2f", firstItem.coast)}",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = "₽${String.format("%.2f", totalPrice)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// Вспомогательная функция
fun Order.getFormattedTimeAgo(): String {
    return try {
        val orderTime = java.time.OffsetDateTime.parse(created_at)
        val now = java.time.OffsetDateTime.now()
        val minutes = java.time.Duration.between(orderTime, now).toMinutes()

        when {
            minutes < 1 -> "только что"
            minutes < 60 -> "$minutes мин назад"
            minutes < 120 -> "1 час назад"
            minutes < 1440 -> "${minutes / 60} часов назад"
            else -> "${minutes / 1440} дней назад"
        }
    } catch (e: Exception) {
        ""
    }
}


