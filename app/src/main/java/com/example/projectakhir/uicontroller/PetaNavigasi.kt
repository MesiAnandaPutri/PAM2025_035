package com.example.projectakhir.uicontroller

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.role
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.projectakhir.repositori.AplikasiManageProduk
import com.example.projectakhir.uicontroller.route.*
import com.example.projectakhir.view.*

@Composable
fun NavigasiApp(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val repositori = (LocalContext.current.applicationContext as AplikasiManageProduk).container.repositoriDataProduk

    Scaffold(
        bottomBar = {
            // Tampilkan BottomBar hanya jika bukan di halaman Login atau Entry
            if (currentRoute != DestinasiLogin.route && currentRoute != DestinasiRegister.route && currentRoute != DestinasiEntry.route && currentRoute != DestinasiEdit.routeWithArgs) {
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
                        // Ambil data user yang baru saja login
                        val userLogin = repositori.currentUser

                        if (userLogin?.role == "Admin") {
                            navController.navigate(DestinasiKelolaProduk.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            // Jika Staff, tampilkan pesan
                            Toast.makeText(context, "Hanya Admin yang dapat mengakses Kelola Produk", Toast.LENGTH_SHORT).show()
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
                        // Ambil data user yang sedang login dari repositori
                        val userLogin = repositori.currentUser

                        if (userLogin?.role == "Admin") {
                            navController.navigate(DestinasiLaporan.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            // Jika Staff, tampilkan pesan peringatan
                            Toast.makeText(context, "Akses Ditolak: Hanya Admin yang dapat melihat Laporan", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate(DestinasiProfile.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            // Ambil role terbaru
            val roleSekarang = repositori.currentUser?.role

            if (currentRoute == DestinasiKelolaProduk.route && roleSekarang == "Admin") {
                FloatingActionButton(
                    onClick = { navController.navigate(DestinasiEntry.route) },
                    containerColor = limeColor,
                    contentColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah")
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
                    onForgotPasswordClicked = {},
                    onCreateAccountClicked = {
                        navController.navigate(DestinasiRegister.route)
                    }
                )
            }

            composable(route = DestinasiHome.route) {
                HalamanHome(
                    onKelolaProdukClicked = { navController.navigate(DestinasiKelolaProduk.route) },
                    onTransaksiClicked = { navController.navigate(DestinasiTransaksi.route) },
                    onLaporanClicked = { navController.navigate(DestinasiLaporan.route) },
                    onProfileClicked = {navController.navigate(DestinasiProfile.route)}
                )
            }

            composable(route = DestinasiKelolaProduk.route) {
                HalamanKelolaProduk(
                    onBackClicked = {
                        // Navigasi paksa ke Home
                        navController.navigate(DestinasiHome.route) {
                            // Menghapus tumpukan navigasi sampai ke Home
                            popUpTo(DestinasiHome.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onEditClicked = { id -> navController.navigate("${DestinasiEdit.route}/$id") }
                )
            }

            composable(route = DestinasiTransaksi.route) {
                HalamanTransaksi(
                    onBackClicked = {
                        navController.navigate(DestinasiHome.route) {
                            popUpTo(DestinasiHome.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )            }

            composable(route = DestinasiLaporan.route) {
                HalamanLaporan(
                    onBackClicked = {
                        navController.navigate(DestinasiHome.route) {
                            popUpTo(DestinasiHome.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = DestinasiEdit.routeWithArgs,
                arguments = listOf(navArgument(DestinasiEdit.produkIdArg) { type = NavType.IntType })
            ) {
                HalamanEdit(onNavigateUp = { navController.popBackStack() })
            }

            composable(route = DestinasiEntry.route) {
                HalamanEntry(onNavigateUp = { navController.popBackStack() })
            }
            composable(route = DestinasiProfile.route) {
                HalamanProfile(
                    onLogoutClick = {
                        navController.navigate(DestinasiLogin.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClicked = {
                        // Navigasi paksa ke Home dan bersihkan stack
                        navController.navigate(DestinasiHome.route) {
                            popUpTo(DestinasiHome.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(route = DestinasiRegister.route) {
                HalamanRegister(
                    onNavigateToLogin = {
                        navController.popBackStack() // Kembali ke Login
                    }
                )
            }
        }
    }
}