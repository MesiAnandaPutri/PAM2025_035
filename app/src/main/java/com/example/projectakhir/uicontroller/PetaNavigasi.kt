package com.example.projectakhir.uicontroller

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projectakhir.uicontroller.route.DestinasiEntry
import com.example.projectakhir.uicontroller.route.DestinasiHome
import com.example.projectakhir.uicontroller.route.DestinasiKelolaProduk
import com.example.projectakhir.uicontroller.route.DestinasiLogin
import com.example.projectakhir.view.HalamanEntry
import com.example.projectakhir.view.HalamanHome
import com.example.projectakhir.view.HalamanKelolaProduk
import com.example.projectakhir.view.HalamanLogin
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel

@Composable
fun NavigasiApp(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiLogin.route,
        modifier = modifier
    ) {
        composable(DestinasiLogin.route) {
            HalamanLogin(
                onLoginSuccess = {
                    navController.navigate(DestinasiHome.route) {
                        popUpTo(DestinasiLogin.route) { inclusive = true }
                    }
                },
                onForgotPasswordClicked = { /* TODO */ },
                onCreateAccountClicked = { /* TODO */ },
                loginViewModel = viewModel(factory = PenyediaViewModel.Factory)
            )
        }
        composable(route = DestinasiHome.route) {
            HalamanHome(
                onKelolaProdukClicked = {
                    navController.navigate(DestinasiKelolaProduk.route)
                },
                // --------------------------
                homeViewModel = viewModel(factory = PenyediaViewModel.Factory)
            )
        }
        composable(route = DestinasiKelolaProduk.route) {
            HalamanKelolaProduk(
                onBackClicked = { navController.popBackStack() },onAddClicked = {
                    navController.navigate(DestinasiEntry.route)
                },
                kelolaProdukViewModel = viewModel(factory = PenyediaViewModel.Factory)
            )
        }
        composable(route = DestinasiEntry.route) {
            HalamanEntry(
                onNavigateUp = { navController.popBackStack() },
                entryViewModel = viewModel(factory = PenyediaViewModel.Factory)
            )
        }
    }
}
