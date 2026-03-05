package com.example.shoeshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoeshop.R
import com.example.shoeshop.data.CartItem
import com.example.shoeshop.data.CartManager
import com.example.shoeshop.ui.theme.AppTypography
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit
) {
    val cartItems by CartManager.cartItems.collectAsState()
    val context = LocalContext.current

    // Состояние для отслеживания свайпов
    val swipeStates = remember {
        mutableStateMapOf<String, SwipeState>()
    }

    // Обновляем состояния при изменении списка
    LaunchedEffect(cartItems) {
        // Удаляем состояния для удаленных товаров
        val currentIds = cartItems.map { it.cart.id }.toSet()
        swipeStates.keys.retainAll(currentIds)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Cart",
                        style = AppTypography.headingSemiBold16,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Заголовок и количество товаров
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Корзина",
                    style = AppTypography.headingRegular32.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "${cartItems.size} товара",
                    style = AppTypography.bodyRegular16,
                    color = Color.Gray
                )
            }

            if (cartItems.isEmpty()) {
                // Пустая корзина
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
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
                            text = "Корзина пуста",
                            style = AppTypography.bodyRegular16,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Список товаров
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { cartItem ->
                        val swipeState = swipeStates.getOrPut(cartItem.cart.id) { SwipeState() }

                        SwipeableCartItem(
                            cartItem = cartItem,
                            swipeState = swipeState,
                            onIncrease = { CartManager.increaseQuantity(cartItem.cart.id) },
                            onDecrease = { CartManager.decreaseQuantity(cartItem.cart.id) },
                            onDelete = { CartManager.removeFromCart(cartItem.cart.id) }
                        )
                    }
                }

                // Итого
                CartSummary()
            }
        }
    }
}

data class SwipeState(
    var offsetX: Float = 0f,
    var isSwiping: Boolean = false
)

@Composable
fun SwipeableCartItem(
    cartItem: CartItem,
    swipeState: SwipeState,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    val product = cartItem.product ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        // Кнопка удаления (появляется при свайпе влево)
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(end = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Red.copy(alpha = 0.1f)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Удалить",
                    color = Color.Red,
                    style = AppTypography.bodyMedium14
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color.Red
                )
            }
        }

        // Кнопки увеличения/уменьшения (появляются при свайпе вправо)
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(start = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onDecrease,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    painter  = painterResource(id= R.drawable.minus),
                    contentDescription = "Уменьшить",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "${cartItem.cart.count}",
                style = AppTypography.bodyMedium16,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = onIncrease,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    painter = painterResource(id= R.drawable.add),
                    contentDescription = "Увеличить",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Основная карточка товара (с возможностью свайпа)
        CartItemCard(
            cartItem = cartItem,
            swipeState = swipeState,
            onSwipeLeft = {
                // Показываем кнопку удаления
            },
            onSwipeRight = {
                // Показываем кнопки количества
            }
        )
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    swipeState: SwipeState,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val product = cartItem.product ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { swipeState.isSwiping = true },
                    onDragEnd = {
                        swipeState.isSwiping = false
                        if (swipeState.offsetX > 100) {
                            onSwipeRight()
                        } else if (swipeState.offsetX < -100) {
                            onSwipeLeft()
                        }
                        swipeState.offsetX = 0f
                    },
                    onDragCancel = {
                        swipeState.isSwiping = false
                        swipeState.offsetX = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    swipeState.offsetX = (swipeState.offsetX + dragAmount).coerceIn(-150f, 150f)
                }
            }
            .offset(x = swipeState.offsetX.dp),
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
            // Изображение товара
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                if (product.imageResId != null && product.imageResId != 0) {
                    Image(
                        painter = painterResource(id = product.imageResId),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👟",
                            fontSize = 32.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Информация о товаре
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.title,
                    style = AppTypography.bodyMedium16,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.getFormattedPrice(),
                    style = AppTypography.bodyMedium16.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Количество и цена за всё
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "x${cartItem.cart.count}",
                    style = AppTypography.bodyRegular14,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatPrice(cartItem.totalPrice),
                    style = AppTypography.bodyMedium16.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
fun CartSummary() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Сумма
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Сумма",
                    style = AppTypography.bodyRegular16,
                    color = Color.Gray
                )
                Text(
                    text = formatPrice(CartManager.subtotal),
                    style = AppTypography.bodyMedium16
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Доставка
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Доставка",
                    style = AppTypography.bodyRegular16,
                    color = Color.Gray
                )
                Text(
                    text = formatPrice(CartManager.deliveryCost),
                    style = AppTypography.bodyMedium16
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            // Итого
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Итого",
                    style = AppTypography.headingSemiBold16
                )
                Text(
                    text = formatPrice(CartManager.total),
                    style = AppTypography.headingSemiBold16.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка отправки заказа
            Button(
                onClick = { /* Отправить заказ */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Отправить Заказ",
                    style = AppTypography.bodyMedium16,
                    color = Color.White
                )
            }
        }
    }
}

fun formatPrice(price: Double): String {
    return try {
        // Простой способ форматирования
        val formatted = String.format("%,.2f", price)
            .replace(",", " ")
            .replace(".", ",")

        "₽$formatted"
    } catch (e: Exception) {
        "₽${String.format("%.2f", price)}"
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenPreview() {
    CartScreen(
        onBackClick = {}
    )
}