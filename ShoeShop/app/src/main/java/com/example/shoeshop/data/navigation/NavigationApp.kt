import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shoeshop.ui.screens.CatalogScreen
import com.example.shoeshop.ui.screens.CreateNewPasswordScreen
import com.example.shoeshop.ui.screens.FavoriteScreen
import com.example.shoeshop.ui.screens.ForgotPasswordScreen
import com.example.shoeshop.ui.screens.HomeScreen
import com.example.shoeshop.ui.screens.OnboardScreen
import com.example.shoeshop.ui.screens.ProfileScreen
import com.example.shoeshop.ui.screens.RegisterAccount
import com.example.shoeshop.ui.screens.SignInScreen


@Composable
fun NavigationApp(navController: NavHostController) {

    // Получаем состояние авторизации из AuthManager
    val isAuthenticated by AuthManager.isAuthenticated.collectAsState()
    val userId by AuthManager.userId.collectAsState()
    val accessToken by AuthManager.accessToken.collectAsState()

    // Логируем для отладки
    LaunchedEffect(isAuthenticated, userId, accessToken) {
        println("NavigationApp - isAuthenticated: $isAuthenticated")
        println("NavigationApp - userId: $userId")
        println("NavigationApp - token exists: ${accessToken != null}")
    }

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "home" else "start_menu"
    ) {
        composable("sign_up") {
            RegisterAccount(
                onSignInClick = { navController.navigate("sign_in") },
                onSignUpClick = { navController.navigate("email_verification") }
            )
        }

        composable("sign_in") {
            SignInScreen(
                onForgotPasswordClick = { navController.navigate("forgot_password") },
                onSignInClick = {
                    // После успешного входа переходим на home
                    navController.navigate("home") {
                        popUpTo("sign_in") { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate("sign_up") }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateToOtpVerification = { navController.navigate("reset_password") },
                onBackClick = { navController.navigate("sign_in") }
            )
        }

        composable("email_verification") {
            EmailVerificationScreen(
                onSignInClick = { navController.navigate("sign_in") },
                onVerificationSuccess = {
                    navController.navigate("home") {
                        popUpTo("email_verification") { inclusive = true }
                    }
                }
            )
        }

        composable("reset_password") {
            RecoveryVerificationScreen(
                onSignInClick = { navController.navigate("sign_in") },
                onResetPasswordClick = { resetToken ->
                    navController.navigate("create_password/$resetToken")
                }
            )
        }

        composable("create_password/{resetToken}") { backStackEntry ->
            val resetToken = backStackEntry.arguments?.getString("resetToken") ?: ""
            CreateNewPasswordScreen(
                userToken = resetToken,
                onPasswordChanged = { navController.navigate("sign_in") }
            )
        }

        composable("start_menu") {
            OnboardScreen(
                onGetStartedClick = { navController.navigate("sign_up") }
            )
        }

        composable("home") {
            HomeScreen(
                onProductClick = { product ->
                    // Навигация на детали товара
                    navController.navigate("product_detail/${product.id}")
                },
                onCartClick = {
                    // Навигация в корзину
                    navController.navigate("cart")
                },
                onSearchClick = {
                    // Навигация на поиск
                    navController.navigate("search")
                },
                onSettingsClick = {
                    // Навигация в настройки
                    navController.navigate("settings")
                },
                onCatalogClick = {
                    // Переходим в каталог с выбранной категорией
                    navController.navigate("catalog/Outdoor")
                },
                onFavoriteClick = {
                    navController.navigate("favorite")
                },
                userId = userId ?: "",  // Передаем userId из AuthManager
                token = accessToken ?: "" // Передаем token из AuthManager
            )
        }

        // ЭКРАН ПРОФИЛЯ - берем данные из AuthManager
        composable("profile") {
            // Проверяем, есть ли данные авторизации
            if (userId != null && accessToken != null) {
                println("✅ Передаем в ProfileScreen - userId: $userId, token: ${accessToken?.take(20)}...")
                ProfileScreen(
                    userId = userId!!,
                    token = accessToken!!
                )
            } else {
                println("❌ Нет данных авторизации, редирект на вход")
                LaunchedEffect(Unit) {
                    navController.navigate("sign_in") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            }
        }

        // Добавьте другие экраны по необходимости
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            // Экран деталей товара
            Text("Детали товара $productId")
        }

        composable("cart") {
            // Экран корзины
            Text("Корзина")
        }

        composable("search") {
            // Экран поиска
            Text("Поиск")
        }

        composable("settings") {
            // Экран настроек
            Text("Настройки")
        }

        composable("catalog/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "Все"
            CatalogScreen(
                initialCategory = category,
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                }
            )
        }

        composable("favorite") {
            FavoriteScreen(
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NavigationAppPreview() {
    val navController = rememberNavController()
    NavigationApp(navController = navController)
}