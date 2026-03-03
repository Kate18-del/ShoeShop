package com.example.shoeshop.ui.screens


import android.content.Context
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import androidx.core.content.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfirstproject.data.model.SignUpRequest
import com.example.myfirstproject.ui.viewModel.SignUpState
import com.example.myfirstproject.ui.viewModel.SignUpViewModel
import com.example.shoeshop.R
import com.example.shoeshop.ui.components.BackButton
import com.example.shoeshop.ui.components.DisableButton
import com.example.shoeshop.ui.theme.AppTypography
import com.example.shoeshop.ui.theme.ShoeShopTheme
import java.util.regex.Pattern

// Серафимова Екатерина Сергеевна
// 03.03.2026
// Назначение: класс для регистрации

@Composable
fun RegisterAccount(modifier: Modifier = Modifier,
                    onBackClick: () -> Unit = {},
                    onRegisterClick: () -> Unit = {},
                    onLoginClick: () -> Unit = {},
                    onSignInClick : () -> Unit = {} ,
                    onSignUpClick : () -> Unit = {},
                    viewModel: SignUpViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var pendingSignUpRequest by remember { mutableStateOf<SignUpRequest?>(null) }
    val signUpState by viewModel.signUpState.collectAsStateWithLifecycle()

    // Состояние для диалога с ошибкой
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current


    val sharedPreferences = remember {
        context.getSharedPreferences("shoe_shop_prefs", Context.MODE_PRIVATE)
    }

    // Обработка состояний регистрации
    LaunchedEffect(signUpState) {
        when (signUpState) {
            is SignUpState.Success -> {
                // Сохраняем данные при успешной регистрации
                saveUserDataToPreferences(sharedPreferences, name, email)
                onSignUpClick()
                viewModel.resetState()
            }
            is SignUpState.Error -> {
                val error = (signUpState as SignUpState.Error)
                errorMessage = error.message

                // Показываем диалог только для определенных ошибок
                val showDialog = when {
                    error.message.contains("Too many requests", ignoreCase = true) -> true
                    error.message.contains("rate limit", ignoreCase = true) -> true
                    error.message.contains("network", ignoreCase = true) -> true
                    error.message.contains("invalid", ignoreCase = true) -> true
                    else -> true
                }

                if (showDialog) {
                    showErrorDialog = true
                } else {
                    // Для других ошибок можно показать Snackbar или Toast
                    // например, для ошибок валидации
                }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Функция проверки email
    fun isValidEmail(email: String): Boolean {
        if (email.isEmpty()) return false // Пустой email считаем невалидным для проверки

        // Регулярное выражение для проверки email:
        // name@domenname.ru
        // где name и domenname - только маленькие буквы и цифры
        // старший домен (ru) - только буквы, количество больше двух
        val emailPattern = Pattern.compile(
            "^[a-z0-9]+@[a-z0-9]+\\.[a-z]{3,}$"
        )
        return emailPattern.matcher(email).matches()
    }

    // Функция получения сообщения об ошибке для email
    fun getEmailErrorMessage(email: String): String {
        return when {
            email.isEmpty() -> "Email не может быть пустым"
            !email.contains("@") -> "Email должен содержать символ @"
            else -> {
                val parts = email.split("@")
                if (parts.size != 2) {
                    "Неверный формат email"
                } else {
                    val localPart = parts[0]
                    val domainPart = parts[1]

                    when {
                        localPart.isEmpty() -> "Отсутствует имя пользователя"
                        !localPart.matches(Regex("^[a-z0-9]+$")) ->
                            "Имя домена должно содержать только маленькие буквы и цифры"
                        !domainPart.contains(".") -> "Домен должен содержать точку"
                        else -> {
                            val domainParts = domainPart.split(".")
                            if (domainParts.size != 2) {
                                "Неверный формат домена"
                            } else {
                                val domainName = domainParts[0]
                                val topLevelDomain = domainParts[1]

                                when {
                                    domainName.isEmpty() -> "Отсутствует имя домена"
                                    !domainName.matches(Regex("^[a-z0-9]+$")) ->
                                        "Имя домена должно содержать только маленькие буквы и цифры"
                                    topLevelDomain.length < 3 ->
                                        "Старший домен должен содержать больше 2 символов"
                                    !topLevelDomain.matches(Regex("^[a-z]+$")) ->
                                        "Старший домен должен содержать только буквы"
                                    else -> "Некорректный email"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Валидация формы (только для активации кнопки - все поля заполнены)
    val isFormValid = name.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            isChecked

    // Обработчик нажатия на кнопку регистрации
    val onRegisterClickWithValidation = {
        // Проверяем email на корректность
        if (!isValidEmail(email)) {
            errorMessage = getEmailErrorMessage(email)
            showErrorDialog = true
        } else {
            onRegisterClick()
        }
    }

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
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(id = R.string.details),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        }

        // Поле "Имя"
        Text(
            text = stringResource(id = R.string.name),
            style = MaterialTheme.typography.bodyLarge,
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

        // Поле "Email" с индикацией ошибки
        Text(
            text = stringResource(id = R.string.email),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                // Сбрасываем ошибку при изменении текста
                if (showErrorDialog) showErrorDialog = false
            },
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
            singleLine = true,
            isError = email.isNotBlank() && !isValidEmail(email) // Показываем ошибку в поле
        )

        // Поле "Пароль"
        Text(
            text = stringResource(id = R.string.pass),
            style = MaterialTheme.typography.bodyLarge,
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
                style = MaterialTheme.typography.bodyLarge,
                color = hintColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))

        // Кнопка регистрации
        DisableButton(
            text = stringResource(id = R.string.sign_up),
            onClick = {
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && isChecked) {
                    val signUpRequest = SignUpRequest(email, password)
                    pendingSignUpRequest = signUpRequest
                    viewModel.signUp(signUpRequest)
                } else {
                    // Валидация полей
                    errorMessage = when {
                        name.isEmpty() -> "Please enter your name"
                        email.isEmpty() -> "Please enter your email address"
                        password.isEmpty() -> "Please enter your password"
                        !isChecked -> "Please accept the terms and conditions"
                        else -> "Please fill in all required fields"
                    }
                    showErrorDialog = true
                }
            },
            enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && isChecked,
            textStyle = AppTypography.bodyMedium16
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

    // Диалог с ошибкой
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    text = "Ошибка валидации",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("OK")
                }
            },
        )
    }
}


private fun saveUserDataToPreferences(
    sharedPreferences: android.content.SharedPreferences,
    name: String,
    email: String
) {
    sharedPreferences.edit {
        putString("user_name", name)
        putString("user_email", email)
    }
}

@Preview
@Composable
private fun RegisterAccountPrev() {
    ShoeShopTheme {
        RegisterAccount()
    }
}