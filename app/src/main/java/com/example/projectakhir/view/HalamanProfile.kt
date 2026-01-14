package com.example.projectakhir.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectakhir.R
import com.example.projectakhir.viewmodel.ProfileViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel
// ... (import tetap sama)

@Composable
fun HalamanProfile(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.profileUIState

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- Header Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(limeColor)
                .padding(top = 40.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Profile", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("Kelola akun dan pengaturan", fontSize = 14.sp, color = Color.DarkGray)
        }

        // --- User Card (PERBAIKAN DI SINI) ---
        Card(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Ikon Profil Kuning (Satu-satunya ikon utama)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(limeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // 2. Data User (Nama & Email)
                Column {
                    Text(
                        text = uiState.username,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                }
            }
        }

        // --- Pengaturan Akun (Sisa kode tetap sama) ---
        Text(
            "Pengaturan Akun",
            modifier = Modifier.padding(horizontal = 24.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        ProfileMenuAction(
            label = "Edit Profil",
            subLabel = "Ubah informasi pribadi",
            icon = Icons.Default.Person,
            onClick = {}
        )

        ProfileMenuAction(
            label = "Keluar",
            subLabel = "Logout dari akun Anda",
            icon = Icons.Default.ExitToApp,
            iconColor = Color.Red,
            labelColor = Color.Red,
            onClick = { viewModel.logout(onLogoutClick) }
        )
    }
}
@Composable
fun ProfileMenuAction(
    label: String,
    subLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color = Color.Black,
    labelColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(Modifier.size(45.dp), RoundedCornerShape(12.dp), if(labelColor == Color.Red) Color(0xFFFFEBEE) else Color(0xFFF8F9FA)) {
                Icon(icon, null, Modifier.padding(12.dp), iconColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(label, fontWeight = FontWeight.Bold, color = labelColor)
                Text(subLabel, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}