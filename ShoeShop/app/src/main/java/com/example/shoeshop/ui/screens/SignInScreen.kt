package com.example.shoeshop.ui.screens

import SignInViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoeshop.R
import com.example.shoeshop.ui.components.BackButton
import com.example.shoeshop.ui.components.DisableButton
import com.example.shoeshop.ui.theme.AppTypography
import com.example.shoeshop.ui.theme.ShoeShopTheme

import kotlin.text.isNotEmpty

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onForgotPasswordClick : () -> Unit = {} ,
    onSignInClick : () -> Unit = {} ,
    onSignUpClick : () -> Unit = {},
    viewModel: SignInViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val checkedState = remember { mutableStateOf(false) }
    val signInState by viewModel.signInState.collectAsStateWithLifecycle()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Используем цвета из темы
    val hintColor = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline

    LaunchedEffect(signInState) {
        when (signInState) {
            is SignInState.Success -> {

                onSignInClick()
                viewModel.resetState()
            }
            is SignInState.Error -> {
                errorMessage = (signInState as SignInState.Error).message
                showErrorDialog = true
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // AlertDialog для ошибок авторизации
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Authentication Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0560FA))
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(23.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
    ) {
        BackButton(
            onClick = {}
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.hello),
                style = AppTypography.headingRegular32,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Используем bodyRegular14 вместо bodyMedium
            Text(
                text = stringResource(id = R.string.details),
                style = AppTypography.subtitleRegular16,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 54.dp)
            )
        }

        // Поле "Email" - используем bodyMedium14 для метки
        Text(
            text = stringResource(id = R.string.email),
            style = AppTypography.bodyMedium16.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(
                    "......@mail.com",
                    style = AppTypography.bodyRegular14,
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
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = hintColor,
                unfocusedPlaceholderColor = hintColor
            ),
            textStyle = AppTypography.bodyRegular16, // Body Regular 16 для ввода текста
            singleLine = true
        )

        // Поле "Пароль" - используем bodyMedium14 для метки
        Text(
            text = stringResource(id = R.string.pass),
            style = AppTypography.bodyMedium16.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    "......",
                    style = AppTypography.bodyRegular14,
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
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = borderColor,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = hintColor,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = hintColor,
                unfocusedPlaceholderColor = hintColor
            ),
            textStyle = AppTypography.bodyRegular16, // Body Regular 16 для ввода текста
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
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.End)
        {
            Text(
            text = stringResource(id = R.string.recovery),
            style = AppTypography.bodyRegular12,
            color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable(
                        onClick = onForgotPasswordClick
                    ),
        )}

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка регистрации
        DisableButton(
            text = stringResource(id = R.string.sign_in),
            onClick = { if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.signIn(email, password)
            } else {
                errorMessage = "Please fill in all fields"
                showErrorDialog = true
            } },
            textStyle = AppTypography.bodyMedium16 // Body Medium 16 для текста кнопки
        )

        Spacer(modifier = Modifier.weight(1f))

        // Ссылка на вход
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = onSignUpClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontFamily = AppTypography.bodyRegular16.fontFamily,
                                fontSize = AppTypography.bodyRegular16.fontSize
                            )
                        ) {
                            append(stringResource(id = R.string.new_user))
                        }
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = AppTypography.bodyRegular16.fontFamily,
                                fontSize = AppTypography.bodyRegular16.fontSize,
                            )
                        ) {
                            append(stringResource(id = R.string.create))
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenScreenPreview() {
    ShoeShopTheme {
        SignInScreen()
    }
}