package com.example.shoeshop.ui.components

import Product
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.shoeshop.ui.theme.AppTypography

@Composable
fun ProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onProductClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column {
            // Верхняя часть с изображением и кнопкой избранного
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                // Изображение товара
                if (product.imageResId != null && product.imageResId != 0) {
                    // Загружаем из ресурсов
                    Image(
                        painter = painterResource(id = product.imageResId),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (!product.imageUrl.isNullOrEmpty()) {
                    // Загружаем из сети (если есть URL)
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(id = com.example.shoeshop.R.drawable.nike_zoom_winflo_3_831561_001_mens_running_shoes_11550187236tiyyje6l87_prev_ui_3)
                    )
                } else {
                    // Запасной вариант, если нет изображения
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "👟",
                                fontSize = 32.sp
                            )
                            Text(
                                text = "Нет фото",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Кнопка избранного поверх изображения
                IconButton(
                    onClick = {
                        isFavorite = !isFavorite
                        onFavoriteClick()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Избранное",
                        tint = if (isFavorite) Color.Red else Color.Black
                    )
                }
            }

            // Нижняя часть с информацией о товаре
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color.White)
            ) {
                // Показываем бейдж BEST SELLER если товар в бестселлерах
                if (product.is_best_seller == true) {
                    Text(
                        text = "BEST SELLER",
                        style = AppTypography.bodyRegular12,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    // Пустое место для выравнивания
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = product.title,
                    style = AppTypography.bodyRegular16,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.getFormattedPrice(),
                    style = AppTypography.bodyRegular14,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
fun ProductCardPreview() {
    ProductCard(
        product = Product(
            id = "1",
            title = "Nike Air Max",
            category_id = "cat1",
            cost = 752.00,
            description = "Test description",
            is_best_seller = true,
            imageResId =com.example.shoeshop.R.drawable.nike_zoom_winflo_3_831561_001_mens_running_shoes_11550187236tiyyje6l87_prev_ui_3 // Для превью используем иконку
        ),
        onProductClick = {},
        onFavoriteClick = {}
    )
}