import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shoeshop.ui.screens.CreateNewPasswordScreen
import com.example.shoeshop.ui.screens.ForgotPasswordScreen
import com.example.shoeshop.ui.screens.HomeScreen
import com.example.shoeshop.ui.screens.OnboardScreen
import com.example.shoeshop.ui.screens.RegisterAccount
import com.example.shoeshop.ui.screens.SignInScreen


@Composable
fun NavigationApp(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "start_menu"
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
                onSignInClick = { navController.navigate("home") },
                onSignUpClick = { navController.navigate("sign_up") }

            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateToOtpVerification = { navController.navigate("reset_password") },
                onBackClick = {navController.navigate("sign_in")}
            )
        }

        composable("email_verification") {
            EmailVerificationScreen(
                onSignInClick = { navController.navigate("sign_in") },
                onVerificationSuccess = { navController.navigate("home") }

            )
        }
        composable("reset_password") {
            RecoveryVerificationScreen(
                onSignInClick = {navController.navigate("sign_in")},
                onResetPasswordClick = { resetToken ->  // Токен приходит сюда
                    // Передаем токен в маршрут
                    navController.navigate("create_password/$resetToken")
                }

            )
        }

        composable("create_password/{resetToken}") { backStackEntry ->
            val resetToken = backStackEntry.arguments?.getString("resetToken") ?: ""

            CreateNewPasswordScreen(
                userToken = resetToken,  // Передаем токен в экран
                onPasswordChanged = { navController.navigate("sign_in") }
            )
        }

        composable("start_menu") {
            OnboardScreen (
                onGetStartedClick = { navController.navigate("sign_up") },
            )
        }

        composable("home") {
            HomeScreen({},{},{})
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NavigationAppPreview() {
    val navController = rememberNavController()
    NavigationApp(navController = navController)
}