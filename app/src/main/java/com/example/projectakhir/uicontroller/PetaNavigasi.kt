package com.example.projectakhir.uicontroller

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.projectakhir.uicontroller.route.*
import com.example.projectakhir.view.*
import com.example.projectakhir.view.limeColor

@Composable
fun NavigasiApp(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            val bottomBarShouldShow = currentRoute != DestinasiLogin.route &&
                    currentRoute != DestinasiEntry.route &&
                    currentRoute != DestinasiEdit.routeWithArgs

            // Tampilkan BottomBar hanya di halaman yang memerlukannya
            if (currentRoute != DestinasiLogin.route && currentRoute != DestinasiEntry.route) {
                AppBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigateToHome = {
                        navController.navigate(DestinasiHome.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToKelola = {
                        navController.navigate(DestinasiKelolaProduk.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToTransaksi = {
                        navController.navigate(DestinasiTransaksi.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToLaporan = {
                        navController.navigate(DestinasiLaporan.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToProfile = { /* TODO */ }
                )
            }
        },
        floatingActionButton = {
            // Tampilkan FloatingActionButton hanya saat berada di halaman Kelola Produk
            if (currentRoute == DestinasiKelolaProduk.route) {
                FloatingActionButton(
                    onClick = { navController.navigate(DestinasiEntry.route) },
                    containerColor = limeColor,
                    contentColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DestinasiLogin.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(DestinasiLogin.route) {
                HalamanLogin(
                    onLoginSuccess = {
                        navController.navigate(DestinasiHome.route) {
                            popUpTo(DestinasiLogin.route) { inclusive = true }
                        }
                    },
                    onForgotPasswordClicked = { /* TODO */ },
                    onCreateAccountClicked = { /* TODO */ }
                )
            }

            composable(route = DestinasiHome.route) {
                HalamanHome(
                    onKelolaProdukClicked = {
                        navController.navigate(DestinasiKelolaProduk.route)
                    },
                    bottomBar = {} // Dibiarkan kosong karena sudah ditangani Scaffold utama
                )
            }

            composable(route = DestinasiKelolaProduk.route) {
                // Pemanggilan fungsi disederhanakan
                HalamanKelolaProduk(
                    onBackClicked = { navController.popBackStack() },
                    onEditClicked = { produkId ->
                        navController.navigate("${DestinasiEdit.route}/$produkId")
                    }
                )
            }
            composable(
                route = DestinasiEdit.routeWithArgs,
                arguments = listOf(navArgument(DestinasiEdit.produkIdArg) {
                    type = NavType.IntType
                })
            ) {
                HalamanEdit(
                    onNavigateUp = { navController.popBackStack() }
                )
            }
            composable(route = DestinasiEntry.route) {
                HalamanEntry(
                    onNavigateUp = { navController.popBackStack() }
                )
            }
            composable(route = DestinasiTransaksi.route) {
                HalamanTransaksi()
            }
            composable(route = DestinasiLaporan.route) {
                HalamanLaporan()
            }

        }
    }
}
