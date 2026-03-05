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
import com.example.shoeshop.data.AuthManager
import com.example.shoeshop.ui.screens.CartScreen
import com.example.shoeshop.ui.screens.CatalogScreen
import com.example.shoeshop.ui.screens.CheckoutScreen
import com.example.shoeshop.ui.screens.CreateNewPasswordScreen
import com.example.shoeshop.ui.screens.DetailsScreen
import com.example.shoeshop.ui.screens.FavoriteScreen
import com.example.shoeshop.ui.screens.ForgotPasswordScreen
import com.example.shoeshop.ui.screens.HomeScreen
import com.example.shoeshop.ui.screens.OnboardScreen
import com.example.shoeshop.ui.screens.OrderDetailScreen
import com.example.shoeshop.ui.screens.OrdersScreen
import com.example.shoeshop.ui.screens.ProfileScreen
import com.example.shoeshop.ui.screens.RegisterAccount
import com.example.shoeshop.ui.screens.SignInScreen


@Composable
fun NavigationApp(navController: NavHostController) {

    // Получаем состояние авторизации из AuthManager
    val isAuthenticated by AuthManager.isAuthenticated.collectAsState(initial = false)
    val userId by AuthManager.userId.collectAsState(initial = null)
    val accessToken by AuthManager.accessToken.collectAsState(initial = null)
    val userEmail by AuthManager.email.collectAsState(initial = null)

    // Логируем для отладки
    LaunchedEffect(isAuthenticated, userId, accessToken, userEmail) {
        println("🔥 NavigationApp - isAuthenticated: $isAuthenticated")
        println("🔥 NavigationApp - userId: $userId")
        println("🔥 NavigationApp - token exists: ${accessToken != null}")
    }

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
                    navController.navigate("product_detail/${product.id}")
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                onSearchClick = {
                    navController.navigate("search")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                },
                onCatalogClick = { category ->
                    navController.navigate("catalog/$category")
                },
                onFavoriteClick = {
                    navController.navigate("favorite")
                },
                onOrdersClick = { // Добавляем переход к заказам
                    navController.navigate("orders")
                },
                userId = userId ?: "",
                token = accessToken ?: ""
            )
        }

        // ЭКРАН ПРОФИЛЯ
        composable("profile") {
            if (userId != null && accessToken != null) {
                ProfileScreen(
                    userId = userId!!,
                    token = accessToken!!,
                    onOrdersClick = { // Добавляем переход к заказам из профиля
                        navController.navigate("orders")
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("sign_in") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            }
        }

        composable("orders") {
            val currentUserId by AuthManager.userId.collectAsState(initial = null)
            val currentToken by AuthManager.accessToken.collectAsState(initial = null)

            println("📱 Navigation to orders - userId: $currentUserId, token exists: ${currentToken != null}")

            if (currentUserId != null && currentToken != null) {
                OrdersScreen(
                    userId = currentUserId!!,
                    token = currentToken!!,
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = { orderId ->
                        navController.navigate("order_detail/$orderId")
                    }
                )
            }
        }

        // ЭКРАН ДЕТАЛЕЙ ЗАКАЗА
        composable("order_detail/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")?.toLongOrNull() ?: 0L
            val currentUserId by AuthManager.userId.collectAsState(initial = null)
            val currentToken by AuthManager.accessToken.collectAsState(initial = null)

            if (currentUserId != null && currentToken != null) {
                OrderDetailScreen(
                    orderId = orderId,
                    userId = currentUserId!!,
                    token = currentToken!!,
                    onBackClick = { navController.popBackStack() }
                )
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

        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            DetailsScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate("cart") }
            )
        }

        composable("cart") {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = {
                    navController.navigate("checkout")
                }
            )
        }

        composable("checkout") {
            val userId by AuthManager.userId.collectAsState()
            val accessToken by AuthManager.accessToken.collectAsState()
            val userEmail by AuthManager.email.collectAsState()

            if (userId != null && accessToken != null) {
                CheckoutScreen(
                    userId = userId!!,
                    token = accessToken!!,
                    userEmail = userEmail,
                    onBackClick = { navController.popBackStack() },
                    onOrderSuccess = {
                        navController.navigate("home") {
                            popUpTo("checkout") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NavigationAppPreview() {
    val navController = rememberNavController()
    NavigationApp(navController = navController)
}