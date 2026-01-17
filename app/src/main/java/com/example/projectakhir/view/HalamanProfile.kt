package com.example.projectakhir.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectakhir.R
import com.example.projectakhir.modeldata.UserData
import com.example.projectakhir.viewmodel.ProfileViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel
// ... (import tetap sama)

@Composable
fun HalamanProfile(
    onLogoutClick: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.profileUIState

    var userToDelete by remember { mutableStateOf<UserData?>(null) }
    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Hapus User?") },
            text = { Text("Apakah Anda yakin ingin menghapus akun ${userToDelete?.username}?") },
            confirmButton = {
                Button(
                    onClick = {
                        userToDelete?.let { viewModel.deleteUser(it.user_id) }
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    LazyColumn( // Gunakan LazyColumn agar bisa scroll saat list user panjang
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(limeColor)
                    .padding(top = 40.dp, bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali ke Home",
                            tint = Color.Black
                        )
                    }
                }
                Text("Profile", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            }
            // Card User Info
            Card(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(limeColor), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(35.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(uiState.username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(uiState.role, color = Color.Gray)
                    }
                }
            }
        }

        // --- Pengaturan Akun ---
        item {
            Text("Pengaturan Akun", modifier = Modifier.padding(horizontal = 24.dp), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Tampilkan Manage User HANYA jika Admin
            if (uiState.role == "Admin") {
                ProfileMenuAction(
                    label = "Manage User",
                    subLabel = "Lihat dan hapus akun staff",
                    icon = Icons.Default.Person,
                    onClick = { viewModel.toggleUserList() }
                )
            }
        }

        // --- List User (Muncul jika Manage User di-klik) ---
        if (uiState.showUserList && uiState.role == "Admin") {
            if (uiState.isLoading) {
                item {
                    Box(Modifier
                        .fillMaxWidth()
                        .padding(20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = limeColor)
                    }
                }
            } else if (uiState.listUsers.isEmpty()) {
                item {
                    Text("Tidak ada user ditemukan", modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp), textAlign = TextAlign.Center)
                }
            } else {
                items(uiState.listUsers) { user ->
                    UserItem(
                        user = user,
                        onDelete = { userToDelete = user }, // Set user yang akan dihapus ke state dialog
                        isCurrentUser = user.username == uiState.username
                    )
                }
            }
        }

        item {
            ProfileMenuAction(
                label = "Keluar",
                subLabel = "Logout dari akun Anda",
                icon = Icons.Default.ExitToApp,
                iconColor = Color.Red,
                labelColor = Color.Red,
                onClick = { viewModel.logout(onLogoutClick) }
            )
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun UserItem(user: UserData, onDelete: () -> Unit, isCurrentUser: Boolean) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.username, fontWeight = FontWeight.Bold)
                Text(user.role, fontSize = 12.sp, color = Color.Gray)
            }
            // Admin tidak bisa hapus dirinya sendiri
            if (!isCurrentUser) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }
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