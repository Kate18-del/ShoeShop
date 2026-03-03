import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoeshop.R
import com.example.shoeshop.ui.theme.AppTypography
import kotlinx.coroutines.launch
import kotlin.ranges.until
import kotlin.text.all
import kotlin.text.isDigit
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.isNullOrEmpty
import kotlin.text.set

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun RecoveryVerificationScreen(
    onSignInClick: () -> Unit,
    onResetPasswordClick: (resetToken: String) -> Unit,
    viewModel: EmailVerificationViewModel = viewModel()
) {
    var otpCode by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    val context = LocalContext.current
    val verificationState by viewModel.verificationState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()


    // Для управления фокусом на каждом OTP поле
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }
    val focusRequester5 = remember { FocusRequester() }
    val focusRequester6 = remember { FocusRequester() }

    val focusRequesters = listOf(
        focusRequester1, focusRequester2, focusRequester3,
        focusRequester4, focusRequester5, focusRequester6
    )

    LaunchedEffect(Unit) {
        userEmail = getUserEmail(context)
        // Запрашиваем фокус на первом поле при запуске
        focusRequester1.requestFocus()
    }

    // Автоматическая проверка при вводе 6 цифр
    LaunchedEffect(otpCode) {
        if (otpCode.length == 6) {
            // Скрываем клавиатуру
            keyboardController?.hide()
            focusManager.clearFocus()

            // Проверяем Recovery OTP
            if (userEmail.isNotEmpty()) {
                viewModel.verifyRecoveryOtp(userEmail, otpCode)
            } else {
                showToast(context, "Email не найден. Пожалуйста, введите email снова")
            }
        }
    }

    // Обработка состояний проверки
    LaunchedEffect(verificationState) {
        Log.d("DEBUG2", "verificationState изменился: $verificationState")

        when (verificationState) {
            is VerificationState.Success -> {
                Log.d("DEBUG2", "Это Success")
                Log.d("DEBUG2", "type: ${(verificationState as VerificationState.Success).type}")

                when ((verificationState as VerificationState.Success).type) {
                    OtpType.RECOVERY -> {
                        Log.d("DEBUG2", "Это RECOVERY Success")

                        val resetToken = viewModel.getResetToken()
                        Log.d("DEBUG2", "getResetToken() вернул: $resetToken")

                        if (resetToken != null) {
                            Log.d("DEBUG2", "resetToken НЕ null, вызываем onResetPasswordClick")
                            onResetPasswordClick(resetToken)
                        } else {
                            Log.d("DEBUG2", "resetToken = null!")
                            showToast(context, "Ошибка: токен сброса не получен")
                        }
                        viewModel.resetState()
                    }
                    else -> {
                        Log.d("DEBUG2", "Другой тип: ${(verificationState as VerificationState.Success).type}")
                    }
                }
            }
            is VerificationState.Error -> {
                Log.d("DEBUG2", "Error: ${(verificationState as VerificationState.Error).message}")
            }
            else -> {
                Log.d("DEBUG2", "Другое состояние: $verificationState")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок
        Text(
            text = stringResource(id = R.string.otp_verification),
            style = AppTypography.headingRegular32,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Информационное сообщение
        Text(
            text = stringResource(id = R.string.check_email_for_code),
            style = AppTypography.subtitleRegular16.copy(color = MaterialTheme.colorScheme.outline),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Отображение email
        if (userEmail.isNotEmpty()) {
            Text(
                text = "Код отправлен на: $userEmail",
                style = AppTypography.bodyMedium14.copy(color = MaterialTheme.colorScheme.primary),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // Контейнер для OTP полей
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0 until 6) {
                OTPDigitBox(
                    index = i,
                    otpCode = otpCode,
                    onValueChange = { newValue ->
                        handleOtpInput(
                            index = i,
                            newValue = newValue,
                            currentOtp = otpCode,
                            onOtpChange = { otpCode = it },
                            focusRequesters = focusRequesters
                        )
                    },
                    focusRequester = focusRequesters[i],
                    modifier = Modifier
                        .size(56.dp)
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
            }
        }

        // Статус загрузки
        if (verificationState is VerificationState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OTPDigitBox(
    index: Int,
    otpCode: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val currentChar = if (index < otpCode.length) otpCode[index].toString() else ""

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = if (isFocused) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
            .clickable { focusRequester.requestFocus() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = currentChar,
            onValueChange = { newValue ->
                if (newValue.length <= 1 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused },
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (currentChar.isEmpty()) {
                        // Показываем подчеркивание когда поле пустое
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .align(Alignment.Center)
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun handleOtpInput(
    index: Int,
    newValue: String,
    currentOtp: String,
    onOtpChange: (String) -> Unit,
    focusRequesters: List<FocusRequester>
) {
    val newOtp = kotlin.text.StringBuilder(currentOtp)

    if (newValue.isNotEmpty()) {
        // Ввод цифры
        if (index < newOtp.length) {
            newOtp[index] = newValue[0]
        } else {
            // Добавляем новую цифру
            while (newOtp.length < index) {
                newOtp.append(' ')
            }
            newOtp.append(newValue[0])
        }

        onOtpChange(newOtp.toString())

        // Переходим к следующему полю
        if (index < 5) {
            focusRequesters[index + 1].requestFocus()
        }
    } else {
        // Удаление цифры
        if (index < newOtp.length) {
            newOtp.deleteCharAt(index)
            onOtpChange(newOtp.toString())
        }

        // Переходим к предыдущему полю
        if (index > 0) {
            focusRequesters[index - 1].requestFocus()
        }
    }
}

// Вспомогательная функция для показа Toast
private fun showToast(context: Context, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show()
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecoveryVerificationScreenPreview() {
    MaterialTheme {
        RecoveryVerificationScreen(
            onSignInClick = {},
            onResetPasswordClick = { resetToken ->
            }
        )
    }
}