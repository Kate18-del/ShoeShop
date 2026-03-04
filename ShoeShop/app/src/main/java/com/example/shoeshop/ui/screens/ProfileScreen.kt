package com.example.shoeshop.ui.screens

import Profile
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.filled.Edit
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.shoeshop.ui.theme.AppTypography
import com.example.shoeshop.R
import com.example.shoeshop.data.repository.ProfileRepository
import com.example.shoeshop.ui.components.DisableButton
import com.example.shoeshop.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    userId: String,
    token: String,
    viewModel: ProfileViewModel = viewModel()
) {
    // Инициализируем ViewModel с данными пользователя
    LaunchedEffect(Unit) {
        viewModel.initData(userId, token)
    }

    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }

    // Для фото
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Временный файл для камеры
    val photoFile = remember {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        File.createTempFile("JPEG_${timeStamp}_", ".jpg", context.cacheDir)
    }

    val photoUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        photoFile
    )

    // Лаунчер камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = photoUri
            viewModel.setImageUri(photoUri.toString())
        }
    }

    // Лаунчер разрешений
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(photoUri)
        else Toast.makeText(context, "Нет доступа к камере", Toast.LENGTH_SHORT).show()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Верхняя часть
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing) {
                        TextButton(
                            onClick = {
                                isEditing = false
                                viewModel.resetToOriginal()
                                imageUri = null
                            }
                        ) {
                            Text("Отмена", color = Color.Gray)
                        }
                    } else {
                        Spacer(modifier = Modifier.size(60.dp))
                    }

                    Text(
                        text = stringResource(id = R.string.profile),
                        style = AppTypography.headingSemiBold16,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isEditing) {
                        IconButton(
                            onClick = { isEditing = true },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Редактировать",
                                tint = Color.Gray
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Аватар
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
                            Icon(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Default avatar",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(50.dp)
                                    .align(Alignment.Center)
                            )
                        }

                        if (isEditing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Take photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${state.firstName} ${state.lastName}",
                        style = AppTypography.bodyRegular20
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Поля профиля
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isEditing) {
                        EditableField(
                            label = stringResource(id = R.string.your_name),
                            value = state.firstName,
                            onValueChange = { viewModel.updateField("firstName", it) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        EditableField(
                            label = stringResource(id = R.string.last_name),
                            value = state.lastName,
                            onValueChange = { viewModel.updateField("lastName", it) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        EditableField(
                            label = stringResource(id = R.string.address),
                            value = state.address,
                            onValueChange = { viewModel.updateField("address", it) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        EditableField(
                            label = stringResource(id = R.string.phone_number),
                            value = state.phone,
                            onValueChange = { viewModel.updateField("phone", it) }
                        )
                    } else {
                        InputField(
                            label = stringResource(id = R.string.your_name),
                            value = state.firstName
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        InputField(
                            label = stringResource(id = R.string.last_name),
                            value = state.lastName
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        InputField(
                            label = stringResource(id = R.string.address),
                            value = state.address
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        InputField(
                            label = stringResource(id = R.string.phone_number),
                            value = state.phone
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Кнопка сохранения
                if (isEditing) {
                    Button(
                        onClick = {
                            viewModel.saveProfile { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    isEditing = false
                                    imageUri = null
                                }
                            }
                        },
                        enabled = state.hasChanges && !state.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE)
                        )
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Сохранить")
                        }
                    }
                }
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
            singleLine = true,
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



