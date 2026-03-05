package com.example.shoeshop.ui.screens

import Category
import Product
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoeshop.R
import com.example.shoeshop.data.CartManager
import com.example.shoeshop.ui.components.ProductCard
import com.example.shoeshop.ui.theme.AppTypography
import com.example.shoeshop.ui.viewmodel.FavoritesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onCatalogClick: (String) -> Unit,
    onFavoriteClick: () -> Unit,
    userId: String,
    token: String
) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current

    // Инициализируем FavoritesManager и CartManager
    LaunchedEffect(userId, token) {
        if (userId.isNotEmpty() && token.isNotEmpty()) {
            FavoritesManager.init(userId, token)
            CartManager.init(userId, token)
        }
    }

    // Получаем состояние избранного
    val favoriteIds by FavoritesManager.favoriteProductIds.collectAsState()

    // Состояние для выбранной категории
    var selectedCategory by remember { mutableStateOf("Все") }

    // Данные категорий
    val categories = listOf(
        Category(id = "all", title = "Все", isSelected = true),
        Category(id = "outdoor", title = "Outdoor", isSelected = false),
        Category(id = "tennis", title = "Tennis", isSelected = false)
    )

    // Данные популярных товаров
    val popularProducts = listOf(
        Product(
            id = "6478da8e-87e6-4a7a-821c-ac38dd861cec",
            title = "PUMA CA Pro Classic",
            category_id = "outdoor",
            cost = 13999.00,
            description = "Ретро-кроссовки в стиле баскетбола",
            is_best_seller = true,
            imageResId = R.drawable.nike_zoom_winflo_3_831561_001_mens_running_shoes_11550187236tiyyje6l87_prev_ui_3
        ),
        Product(
            id = "3fb5c391-a841-45fb-9c7a-2f74d11a4bfa",
            title = "Adidas Ozmillen",
            category_id = "outdoor",
            cost = 20599.00,
            description = "Легкие и дышащие беговые кроссовки",
            is_best_seller = true,
            imageResId = R.drawable.nike_zoom_winflo_3_831561_001_mens_running_shoes_11550187236tiyyje6l87_prev_ui_3
        ),
        Product(
            id = "63d56e11-2769-4980-96df-dc11ea70148d",
            title = "PUMA Orkid II",
            category_id = "outdoor",
            cost = 14999.00,
            description = "Женские кроссовки для тренировок",
            is_best_seller = true,
            imageResId = R.drawable.nike_zoom_winflo_3_831561_001_mens_running_shoes_11550187236tiyyje6l87_prev_ui_3
        )
    )

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vector_1789),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        IconButton(onClick = { selected = 0 }) {
                            Icon(
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = "Home",
                                tint = if (selected == 0) MaterialTheme.colorScheme.primary else Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = {
                            selected = 1
                            onFavoriteClick()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.favorite),
                                contentDescription = "Favorites",
                                tint = if (selected == 1) MaterialTheme.colorScheme.primary else Color.Black
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .offset(y = (-20).dp)
                            .size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingActionButton(
                            onClick = {
                                onCartClick()
                                selected = 2
                            },
                            modifier = Modifier.size(56.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.bag_2),
                                contentDescription = "Cart",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { selected = 2 }) {
                            Icon(
                                painter = painterResource(id = R.drawable.notification),
                                contentDescription = "Notification",
                                tint = if (selected == 2) MaterialTheme.colorScheme.primary else Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = { selected = 3 }) {
                            Icon(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Profile",
                                tint = if (selected == 3) MaterialTheme.colorScheme.primary else Color.Black
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            if (selected == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.home),
                        style = AppTypography.headingRegular32,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                        ) {
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                placeholder = {
                                    Text(
                                        text = "Поиск...",
                                        style = AppTypography.bodyRegular14
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Поиск",
                                        tint = Color.Gray
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Gray,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { onSettingsClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.sliders),
                                contentDescription = "Настройки",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (selected) {
                    0 -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            item {
                                CategorySection(
                                    categories = categories,
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = { category ->
                                        selectedCategory = category
                                        if (category == "Outdoor") {
                                            onCatalogClick(category)
                                        }
                                    }
                                )
                            }

                            item {
                                PopularSection(
                                    products = popularProducts,
                                    onProductClick = onProductClick,
                                    favoriteIds = favoriteIds,
                                    onFavoriteClick = { productId ->
                                        FavoritesManager.toggleFavorite(productId) { success ->
                                            if (success) {
                                                // Можно показать уведомление
                                            }
                                        }
                                    },
                                    onAddToCart = { product ->
                                        // Добавление в корзину
                                        CartManager.addToCart(product.id) { success ->
                                            if (success) {
                                                Toast.makeText(
                                                    context,
                                                    "${product.title} добавлен в корзину",
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
                            }

                            item {
                                PromotionsSection()
                            }
                        }
                    }
                    1 -> {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    2 -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Уведомления",
                                style = AppTypography.headingRegular32
                            )
                        }
                    }
                    3 -> {
                        ProfileScreen(userId = userId, token = token)
                    }
                }
            }
        }
    }
}

@Composable
private fun PopularSection(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    favoriteIds: Set<String>,
    onFavoriteClick: (String) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.popular),
                style = AppTypography.bodyMedium16,
            )
            Text(
                text = "Все",
                style = AppTypography.bodyRegular12,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    // Навигация на все популярные товары
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    isFavorite = favoriteIds.contains(product.id),
                    onProductClick = { onProductClick(product) },
                    onFavoriteClick = { onFavoriteClick(product.id) },
                    onAddToCart = { onAddToCart(product) }
                )
            }
        }
    }
}



@Composable
private fun PromotionsSection() {
    Column {
        Text(
            text = stringResource(id = R.string.sales),
            style = AppTypography.bodyMedium16,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Summer Sale",
                        style = AppTypography.headingRegular32.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "15% OFF",
                        style = AppTypography.headingRegular32.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                }

                TextButton(
                    onClick = {
                        // Навигация на акции
                    },
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Смотреть",
                        style = AppTypography.bodyMedium16.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    )
                }
            }
        }
    }
}