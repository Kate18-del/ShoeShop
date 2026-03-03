import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun NavigationApp(navController: NavHostController) {

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NavigationAppPreview() {
    val navController = rememberNavController()
    NavigationApp(navController = navController)
}