package com.example.shoeshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoeshop.ui.theme.AppTypography
import com.example.shoeshop.R
import com.example.shoeshop.ui.components.DisableButton

@Composable
fun ProfileScreen() {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Еmmanuel") }
    var lastName by remember { mutableStateOf("Oyiboke") }
    var address by remember { mutableStateOf("Nigeria") }
    var phone by remember { mutableStateOf("") }

    // Проверка, изменились ли данные
    val hasChanges by remember(name, lastName, address, phone) {
        derivedStateOf {
            name != "Еmmanuel" || lastName != "Oyiboke" || address != "Nigeria" || phone != ""
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
                // Пустое место для балансировки
                Spacer(modifier = Modifier.size(40.dp))
                // Заголовок по центру
                Text(
                    text = stringResource(id = R.string.profile),
                    style = AppTypography.headingSemiBold16,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                // Кнопка редактирования/отмены
                IconButton(
                    onClick = {
                        if (isEditing) {
                            // Отмена редактирования - возвращаем оригинальные значения
                            name = "Еmmanuel"
                            lastName = "Oyiboke"
                            address = "Nigeria"
                            phone = ""
                        }
                        isEditing = !isEditing
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id =
                            if (isEditing) R.drawable.edit else R.drawable.edit
                        ),
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
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Имя пользователя
                Text(
                    text = "Еmmanuel Oyiboke",
                    style = AppTypography.bodyRegular20
                )
            }

            // Поля профиля
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isEditing) {
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
                    InputField(label = stringResource(id = R.string.your_name), value = name)
                    Spacer(modifier = Modifier.height(16.dp))
                    InputField(label = stringResource(id = R.string.last_name), value = lastName)
                    Spacer(modifier = Modifier.height(16.dp))
                    InputField(label = stringResource(id = R.string.address), value = address)
                    Spacer(modifier = Modifier.height(16.dp))
                    InputField(label = stringResource(id = R.string.phone_number), value = phone)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Кнопка сохранения (только в режиме редактирования)
            if (isEditing) {
                DisableButton(
                    text = "Сохранить",
                    onClick = {
                        // Здесь логика сохранения данных
                        // Например, вызов ViewModel для сохранения
                        isEditing = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = hasChanges // Кнопка активна только если есть изменения
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
        // Подпись
        Text(
            text = label,
            style = AppTypography.bodyMedium16.copy(
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Поле (non-editable)
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
        // Подпись
        Text(
            text = label,
            style = AppTypography.bodyMedium16.copy(
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Поле для редактирования
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = AppTypography.bodyRegular16,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}