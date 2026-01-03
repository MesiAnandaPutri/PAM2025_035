package com.example.projectakhir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.projectakhir.ui.theme.ProjectAkhirTheme
import com.example.projectakhir.uicontroller.NavigasiApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectAkhirTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Membuat NavController untuk mengelola navigasi
                    val navController = rememberNavController()
                    // Memanggil pengelola navigasi utama aplikasi
                    NavigasiApp(navController = navController)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjectAkhirTheme {
        // Anda bisa membuat preview untuk NavigasiApp jika diperlukan
        // atau biarkan kosong jika tidak butuh preview di MainActivity
        val navController = rememberNavController()
        NavigasiApp(navController = navController)
    }
}
