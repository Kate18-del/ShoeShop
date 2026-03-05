package com.example.shoeshop.ui.screens

import Product
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.shoeshop.R
import com.example.shoeshop.ui.theme.AppTypography
import com.example.shoeshop.ui.viewmodel.FavoriteViewModel
import com.example.shoeshop.ui.viewmodel.FavoritesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    onProductClick: (Product) -> Unit
) {
    val favorites by FavoritesManager.favorites.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Избранное",
                        style = AppTypography.headingSemiBold16,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "В избранном пока пусто",
                        style = AppTypography.bodyRegular16,
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {

                // Заголовок
                item {
                    Text(
                        text = "Избранное",
                        style = AppTypography.headingRegular32,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                // Список избранных товаров
                items(favorites) { (favorite, product) ->
                    if (product != null) {
                        FavoriteItem(
                            product = product,
                            onProductClick = { onProductClick(product) },
                            onRemoveClick = {
                                FavoritesManager.removeFavorite(favorite.id)
                                Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(
    product: Product,
    onProductClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onProductClick() },
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
                    .size(80.dp)
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

            Spacer(modifier = Modifier.width(16.dp))

            // Информация о товаре
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (product.is_best_seller == true) {
                    Text(
                        text = "BEST SELLER",
                        style = AppTypography.bodyRegular12,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

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

            // Кнопка удаления из избранного
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Удалить из избранного",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FavoriteScreenPreview() {
    FavoriteScreen(
        onProductClick = {}
    )
}