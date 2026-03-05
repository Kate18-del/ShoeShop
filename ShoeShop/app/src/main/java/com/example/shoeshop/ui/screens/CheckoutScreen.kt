package com.example.shoeshop.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.shoeshop.R
import com.example.shoeshop.data.CartItem
import com.example.shoeshop.data.CartManager
import com.example.shoeshop.data.CheckoutManager
import com.example.shoeshop.data.repository.CheckoutRepository
import com.example.shoeshop.data.repository.ProfileRepository
import com.example.shoeshop.ui.theme.AppTypography
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    userId: String,
    token: String,
    userEmail: String? = null,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val checkoutRepository = remember { CheckoutRepository() }
    val profileRepository = remember { ProfileRepository() }

    val cartItems by CartManager.cartItems.collectAsState()
    val email by CheckoutManager.email.collectAsState()
    val phone by CheckoutManager.phone.collectAsState()
    val address by CheckoutManager.address.collectAsState()
    val isEditingEmail by CheckoutManager.isEditingEmail.collectAsState()
    val isEditingPhone by CheckoutManager.isEditingPhone.collectAsState()
    val useGPSLocation by CheckoutManager.useGPSLocation.collectAsState()
    val currentLocation by CheckoutManager.currentLocation.collectAsState()

    var tempEmail by remember { mutableStateOf(email) }
    var tempPhone by remember { mutableStateOf(phone) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Вызываем функцию получения локации
            getCurrentLocation(context, fusedLocationClient)
        } else {
            Toast.makeText(context, "Разрешение на геолокацию отклонено", Toast.LENGTH_SHORT).show()
            CheckoutManager.setUseGPSLocation(false)
        }
    }

    // Загружаем данные профиля при запуске
    LaunchedEffect(Unit) {
        val profile = profileRepository.getProfile(userId, token)
        if (profile != null) {
            if (email.isEmpty() && !userEmail.isNullOrEmpty()) {
                CheckoutManager.setEmail(userEmail)
                tempEmail = userEmail
            }

            if (phone.isEmpty()) {
                CheckoutManager.setPhone(profile.phone ?: "")
                tempPhone = profile.phone ?: ""
            }
            if (profile.address?.isNotEmpty() == true) {
                CheckoutManager.setAddress(profile.address ?: "1082 Аэропорт, Нигерии")
            }
        } else {
            if (email.isEmpty() && !userEmail.isNullOrEmpty()) {
                CheckoutManager.setEmail(userEmail)
                tempEmail = userEmail
            }
        }
    }

    fun placeOrder() {
        if (email.isBlank() || phone.isBlank()) {
            Toast.makeText(context, "Заполните контактные данные", Toast.LENGTH_SHORT).show()
            return
        }

        if (cartItems.isEmpty()) {
            Toast.makeText(context, "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }

        scope.launch {
            isPlacingOrder = true

            try {
                val order = checkoutRepository.createOrder(
                    userId = userId,
                    email = email,
                    phone = phone,
                    address = CheckoutManager.getDisplayAddress(),
                    totalAmount = CartManager.total,
                    deliveryCost = CartManager.deliveryCost,
                    token = token
                )

                if (order != null) {
                    val itemsCreated = checkoutRepository.createOrderItems(
                        orderId = order.id,
                        cartItems = cartItems,
                        token = token
                    )

                    if (itemsCreated) {
                        showSuccessDialog = true
                        cartItems.forEach { cartItem ->
                            CartManager.removeFromCart(cartItem.cart.id)
                        }
                    } else {
                        Toast.makeText(context, "Ошибка при создании позиций заказа", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Ошибка при создании заказа", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isPlacingOrder = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Корзина",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ContactInfoSection(
                    email = email,
                    phone = phone,
                    isEditingEmail = isEditingEmail,
                    isEditingPhone = isEditingPhone,
                    onEditEmailClick = { CheckoutManager.toggleEditEmail() },
                    onEditPhoneClick = { CheckoutManager.toggleEditPhone() },
                    onEmailChange = { tempEmail = it },
                    onPhoneChange = { tempPhone = it },
                    onSaveEmail = { CheckoutManager.saveEmail(tempEmail) },
                    onSavePhone = { CheckoutManager.savePhone(tempPhone) }
                )
            }

            item {
                AddressSection(
                    address = CheckoutManager.getDisplayAddress(),
                    useGPS = useGPSLocation,
                    onUseGPSClick = {
                        if (!useGPSLocation) {
                            // Вызываем функцию получения локации
                            getCurrentLocation(context, fusedLocationClient)
                        } else {
                            CheckoutManager.setUseGPSLocation(false)
                        }
                    }
                )
            }

            item {
                PaymentSection()
            }

            if (cartItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Товары в корзине",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(cartItems) { cartItem ->
                    CartItemSummary(cartItem = cartItem)
                }
            }

            item {
                TotalsSection()
            }

            item {
                Button(
                    onClick = { placeOrder() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isPlacingOrder,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isPlacingOrder) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Подтвердить",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        SuccessOrderDialog(
            onDismiss = { showSuccessDialog = false },
            onConfirm = {
                showSuccessDialog = false
                onOrderSuccess()
            }
        )
    }
}

// ВЫНОСИМ ФУНКЦИЮ ЗА ПРЕДЕЛЫ COMPOSABLE
private fun getCurrentLocation(
    context: android.content.Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                CheckoutManager.setLocation(location)
                CheckoutManager.setUseGPSLocation(true)
                Toast.makeText(context, "Местоположение получено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Не удалось получить местоположение. Попробуйте включить GPS", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Ошибка получения местоположения: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ContactInfoSection(
    email: String,
    phone: String,
    isEditingEmail: Boolean,
    isEditingPhone: Boolean,
    onEditEmailClick: () -> Unit,
    onEditPhoneClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSaveEmail: () -> Unit,
    onSavePhone: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Контактная информация",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Email
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.email),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (isEditingEmail) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Email") },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = onSaveEmail,
                            modifier = Modifier.padding(top = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Сохранить", color = Color.White)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (email.isNotEmpty()) email else "Не указан",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Email",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (!isEditingEmail) {
                    IconButton(onClick = onEditEmailClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Редактировать",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Телефон
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.call),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (isEditingPhone) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = onPhoneChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Телефон") },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = onSavePhone,
                            modifier = Modifier.padding(top = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Сохранить", color = Color.White)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (phone.isNotEmpty()) phone else "Не указан",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Телефон",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (!isEditingPhone) {
                    IconButton(onClick = onEditPhoneClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Редактировать",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddressSection(
    address: String,
    useGPS: Boolean,
    onUseGPSClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Адрес",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = address,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = useGPS,
                    onCheckedChange = { onUseGPSClick() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = "Использовать GPS местоположение",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable { onUseGPSClick() }
                )
            }

            if (useGPS) {
                Text(
                    text = "Посмотреть на карте",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { /* Открыть карту */ }
                )
            }
        }
    }
}

@Composable
fun PaymentSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Способ оплаты",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.card),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Dbl Card",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "**** **** 0696 4629",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                TextButton(onClick = { /* Добавить новый способ */ }) {
                    Text(
                        text = "Добавить",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemSummary(
    cartItem: CartItem
) {
    val product = cartItem.product ?: return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Изображение товара
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(4.dp))
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
                Box(contentAlignment = Alignment.Center) {
                    Text("👟", fontSize = 20.sp)
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Информация
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = product.title,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${cartItem.cart.count} x ${formatPrice(product.cost)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Цена
        Text(
            text = formatPrice(cartItem.totalPrice),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TotalsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Сумма",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatPrice(CartManager.subtotal),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Доставка",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatPrice(CartManager.deliveryCost),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Итого",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatPrice(CartManager.total),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SuccessOrderDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        fontSize = 40.sp,
                        color = Color(0xFF4CAF50)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Вы успешно",
                    fontSize = 18.sp,
                    color = Color.Gray
                )

                Text(
                    text = "оформили заказ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Вернуться к покупкам",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}