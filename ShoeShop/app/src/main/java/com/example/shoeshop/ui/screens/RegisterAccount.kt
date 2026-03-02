package com.example.shoeshop.ui.screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoeshop.R
import com.example.shoeshop.ui.components.BackButton
import com.example.shoeshop.ui.components.DisableButton
import com.example.shoeshop.ui.theme.ShoeShopTheme
//Экран регистрации, создан 02.03.2026, Серафимова Екатерина Сергеевна.
@Composable
fun RegisterAccount(modifier: Modifier = Modifier,
                    onBackClick: () -> Unit = {},
                    onRegisterClick: () -> Unit = {},
                    onLoginClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            isChecked

    // Используем цвета из темы
    val hintColor = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline
    val checkboxBorderColor = MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 23.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
    ) {
        BackButton(
            onClick = onBackClick
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.register),
                style = MaterialTheme.typography.headlineMedium, // Используем тему
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(id = R.string.details),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        }

        // Поле "Имя"
        Text(
            text = stringResource(id = R.string.name),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = {
                Text(
                    "xxxxxxxx",
                    style = MaterialTheme.typography.bodyMedium,
                    color = hintColor
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = borderColor,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = hintColor,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true
        )

        // Поле "Email"
        Text(
            text = stringResource(id = R.string.email),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(
                    "......@mail.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = hintColor
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = borderColor,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = hintColor,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true
        )

        // Поле "Пароль"
        Text(
            text = stringResource(id = R.string.pass),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    "......",
                    style = MaterialTheme.typography.bodyMedium,
                    color = hintColor
                )
            },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = borderColor,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = hintColor,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) {
                                R.drawable.eye_close
                            } else {
                                R.drawable.eye_open
                            }
                        ),
                        contentDescription = if (passwordVisible) {
                            "Скрыть пароль"
                        } else {
                            "Показать пароль"
                        },
                        tint = hintColor
                    )
                }
            }
        )

        // Чекбокс с пользовательским соглашением
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .selectable(
                        selected = isChecked,
                        onClick = { isChecked = !isChecked },
                        role = Role.Checkbox
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Фон чекбокса
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(MaterialTheme.shapes.small)
                        .border(
                            width = 2.dp,
                            color = if (isChecked) MaterialTheme.colorScheme.primary else checkboxBorderColor,
                            shape = MaterialTheme.shapes.small
                        )
                        .background(
                            if (isChecked) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                )

                // Галочка
                if (isChecked) {
                    Icon(
                        painter = painterResource(id = R.drawable.policy_check),
                        contentDescription = "Выбрано",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(id = R.string.agree),
                style = MaterialTheme.typography.bodyMedium,
                color = hintColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))

        // Кнопка регистрации
        DisableButton(
            text = stringResource(id = R.string.register),
            onClick = onRegisterClick,
            enabled = isFormValid
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Ссылка на вход
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = onLoginClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = hintColor,
                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            )
                        ) {
                            append(stringResource(id = R.string.have_acc))
                        }
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append(stringResource(id = R.string.sign_in))
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun RegisterAccountPrev() {
    ShoeShopTheme {
        RegisterAccount()
    }
}