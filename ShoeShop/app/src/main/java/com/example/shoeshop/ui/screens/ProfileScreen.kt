package com.example.shoeshop.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.shoeshop.ui.theme.AppTypography
import com.example.shoeshop.R
import com.example.shoeshop.ui.components.DisableButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen() {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Еmmanuel") }
    var lastName by remember { mutableStateOf("Oyiboke") }
    var address by remember { mutableStateOf("Nigeria") }
    var phone by remember { mutableStateOf("") }

    // Состояние для фото профиля
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Создание временного файла для фото (как в вашем примере)
    val photoFile = remember {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        File.createTempFile("JPEG_${timeStamp}_", ".jpg", context.cacheDir)
    }

    val photoUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // Это должно совпадать с authorities в манифесте
        photoFile
    )

    // Лаунчер для камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = photoUri
            Toast.makeText(context, "Фото сохранено", Toast.LENGTH_SHORT).show()
        }
    }

    // Лаунчер для запроса разрешения камеры
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(photoUri)
        } else {
            Toast.makeText(context, "Нет доступа к камере", Toast.LENGTH_SHORT).show()
        }
    }

    // Проверка, изменились ли данные
    val hasChanges by remember(name, lastName, address, phone, imageUri) {
        derivedStateOf {
            name != "Еmmanuel" || lastName != "Oyiboke" || address != "Nigeria" || phone != "" || imageUri != null
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Верхняя часть с заголовком и кнопкой редактирования
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(40.dp))

                Text(
                    text = stringResource(id = R.string.profile),
                    style = AppTypography.headingSemiBold16,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        if (isEditing) {
                            // Отмена редактирования
                            name = "Еmmanuel"
                            lastName = "Oyiboke"
                            address = "Nigeria"
                            phone = ""
                            imageUri = null
                        }
                        isEditing = !isEditing
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = if (isEditing) "Отмена" else "Редактировать",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Аватар по центру
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0))
                        .then(
                            if (isEditing) {
                                Modifier.clickable {
                                    val permission = Manifest.permission.CAMERA
                                    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                        cameraLauncher.launch(photoUri)
                                    } else {
                                        permissionLauncher.launch(permission)
                                    }
                                }
                            } else Modifier
                        )
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Плейсхолдер по умолчанию (используем существующую иконку из R.drawable)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Замените на вашу иконку
                            contentDescription = "Default avatar",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.Center)
                        )
                    }

                    // Оверлей с иконкой камеры в режиме редактирования
                    if (isEditing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Take photo",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$name $lastName",
                    style = AppTypography.bodyRegular20
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Поля профиля
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isEditing) {
                    // Режим редактирования
                    EditableField(
                        label = stringResource(id = R.string.your_name),
                        value = name,
                        onValueChange = { name = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    EditableField(
                        label = stringResource(id = R.string.last_name),
                        value = lastName,
                        onValueChange = { lastName = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    EditableField(
                        label = stringResource(id = R.string.address),
                        value = address,
                        onValueChange = { address = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    EditableField(
                        label = stringResource(id = R.string.phone_number),
                        value = phone,
                        onValueChange = { phone = it }
                    )
                } else {
                    // Режим просмотра
                    InputField(
                        label = stringResource(id = R.string.your_name),
                        value = name
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    InputField(
                        label = stringResource(id = R.string.last_name),
                        value = lastName
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    InputField(
                        label = stringResource(id = R.string.address),
                        value = address
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    InputField(
                        label = stringResource(id = R.string.phone_number),
                        value = phone
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Кнопка сохранения (только в режиме редактирования)
            if (isEditing) {
                DisableButton(
                    text = "Сохранить",
                    onClick = {
                        isEditing = false
                        Toast.makeText(context, "Профиль сохранен", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = hasChanges
                )
            }
        }
    }
}

@Composable
private fun InputField(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium16.copy(
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF5F5F5),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (value.isNotEmpty()) value else "Не указано",
                    style = AppTypography.bodyRegular16.copy(
                        color = if (value.isNotEmpty()) Color.Black else Color.Gray
                    )
                )
            }
        }
    }
}

@Composable
private fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium16.copy(
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = AppTypography.bodyRegular16,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            placeholder = {
                Text(
                    text = "Введите ${label.lowercase()}",
                    color = Color.Gray
                )
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}