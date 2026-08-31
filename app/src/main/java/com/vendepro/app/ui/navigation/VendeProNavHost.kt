package com.vendepro.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vendepro.app.ui.addproduct.AddProductScreen
import com.vendepro.app.ui.catalog.CatalogScreen
import com.vendepro.app.ui.productdetail.ProductDetailScreen
import com.vendepro.app.ui.settings.BusinessConfigScreen

private object Routes {
    const val CATALOG = "catalog"
    const val ADD_PRODUCT = "add_product"
    const val SETTINGS = "settings"
    const val PRODUCT_DETAIL = "product/{id}"
    fun productDetail(id: Int) = "product/$id"
}

@Composable
fun VendeProNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.CATALOG,
        enterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(220)) { it / 6 } },
        exitTransition = { fadeOut(tween(160)) },
        popEnterTransition = { fadeIn(tween(220)) },
        popExitTransition = { fadeOut(tween(160)) + slideOutHorizontally(tween(160)) { it / 6 } },
    ) {
        composable(Routes.CATALOG) {
            CatalogScreen(
                onAddProduct = { navController.navigate(Routes.ADD_PRODUCT) },
                onProductClick = { id -> navController.navigate(Routes.productDetail(id)) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.ADD_PRODUCT) {
            AddProductScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            BusinessConfigScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.PRODUCT_DETAIL) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            if (id == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                ProductDetailScreen(productId = id, onBack = { navController.popBackStack() })
            }
        }
    }
}
