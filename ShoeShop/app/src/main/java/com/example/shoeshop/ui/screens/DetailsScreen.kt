package com.example.shoeshop.ui.screens

import Product
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoeshop.R
import com.example.shoeshop.data.CartManager
import com.example.shoeshop.ui.theme.AppTypography
import com.example.shoeshop.ui.viewmodel.DetailsState
import com.example.shoeshop.ui.viewmodel.DetailsViewModel
import com.example.shoeshop.ui.viewmodel.FavoritesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    productId: String,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit, // Добавляем навигацию в корзину
    viewModel: DetailsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val favoriteIds by FavoritesManager.favoriteProductIds.collectAsState()

    // Состояние для уведомления о добавлении в корзину
    var showAddedToCart by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        viewModel.loadProducts(productId)
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // Сбрасываем уведомление через 2 секунды
    LaunchedEffect(showAddedToCart) {
        if (showAddedToCart) {
            kotlinx.coroutines.delay(2000)
            showAddedToCart = false
        }
    }

    // Обработка свайпов
    val swipeModifier = Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragStart = { /* Начало свайпа */ },
            onDragEnd = { /* Конец свайпа */ },
            onDragCancel = { /* Отмена свайпа */ }
        ) { change, dragAmount ->
            change.consume()
            if (dragAmount > 50) {
                viewModel.previousProduct()
            } else if (dragAmount < -50) {
                viewModel.nextProduct()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sneaker Shop",
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
                actions = {
                    // Кнопка перехода в корзину
                    IconButton(onClick = onCartClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.bag_2),
                            contentDescription = "Корзина"
                        )
                    }
                    IconButton(onClick = { /* Поделиться */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Поделиться"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            // Показываем загрузку
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Загрузка...",
                        style = AppTypography.bodyRegular14
                    )
                }
            }
        } else if (state.products.isEmpty()) {
            // Нет товаров
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Нет товаров для отображения",
                        style = AppTypography.bodyRegular16
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onBackClick) {
                        Text("Вернуться назад")
                    }
                }
            }
        } else {
            // Безопасно получаем текущий товар
            val currentProduct = state.currentProduct

            if (currentProduct == null) {
                // Товар не найден
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Товар не найден",
                            style = AppTypography.bodyRegular16
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onBackClick) {
                            Text("Вернуться назад")
                        }
                    }
                }
            } else {
                // Отображаем товар
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ProductDetailsContent(
                        product = currentProduct,
                        state = state,
                        favoriteIds = favoriteIds,
                        swipeModifier = swipeModifier,
                        viewModel = viewModel,
                        onAddToCart = {
                            // Добавляем в корзину
                            CartManager.addToCart(currentProduct.id) { success ->
                                if (success) {
                                    showAddedToCart = true
                                    Toast.makeText(
                                        context,
                                        "${currentProduct.title} добавлен в корзину",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Ошибка добавления в корзину",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    )

                    // Уведомление о добавлении в корзину
                    if (showAddedToCart) {
                        AddedToCartNotification()
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailsContent(
    product: Product,
    state: DetailsState,
    favoriteIds: Set<String>,
    swipeModifier: Modifier,
    viewModel: DetailsViewModel,
    onAddToCart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(swipeModifier)
    ) {

        // Индикатор текущего товара
        if (state.products.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(state.products.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == state.currentIndex)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.LightGray
                            )
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // Изображение товара
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
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
                        fontSize = 100.sp
                    )
                }
            }
        }

        // Информация о товаре
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = product.title,
                style = AppTypography.headingRegular32.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "Men's Shoes",
                style = AppTypography.bodyRegular16,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.getFormattedPrice(),
                style = AppTypography.headingRegular32.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            DescriptionSection(
                description = product.description,
                isExpanded = state.isDescriptionExpanded,
                onToggle = { viewModel.toggleDescription() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Кнопка добавления в корзину
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "В корзину",
                            style = AppTypography.bodyMedium16,
                            color = Color.White
                        )
                    }
                }

                // Кнопка избранного
                IconButton(
                    onClick = {
                        FavoritesManager.toggleFavorite(product.id)
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (favoriteIds.contains(product.id))
                                Color.Red.copy(alpha = 0.1f)
                            else
                                Color(0xFFF5F5F5)
                        )
                ) {
                    Icon(
                        imageVector = if (favoriteIds.contains(product.id))
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = "Избранное",
                        tint = if (favoriteIds.contains(product.id))
                            Color.Red
                        else
                            Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        }
    }
}

@Composable
fun DescriptionSection(
    description: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Column {
        Text(
            text = description,
            style = AppTypography.bodyRegular14,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            color = Color.DarkGray
        )

        if (description.length > 100) {
            TextButton(
                onClick = onToggle,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = if (isExpanded) "Свернуть" else "Подробнее",
                    style = AppTypography.bodyMedium14,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AddedToCartNotification() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Товар добавлен в корзину",
                    style = AppTypography.bodyMedium16,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailsScreenPreview() {
    DetailsScreen(
        productId = "1",
        onBackClick = {},
        onCartClick = {}
    )
}