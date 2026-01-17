package com.example.projectakhir.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectakhir.R
import com.example.projectakhir.ui.theme.ProjectAkhirTheme
import com.example.projectakhir.viewmodel.LoginUIState
import com.example.projectakhir.viewmodel.LoginViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanLogin(
    // Fungsi ini akan dipanggil untuk navigasi setelah login berhasil
    onLoginSuccess: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onCreateAccountClicked: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel(factory = PenyediaViewModel.Factory)) {
    // Mendapatkan state dari ViewModel
    val loginUIState = loginViewModel.loginUIState
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ikon Toko
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD8FF00)),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                // Menggunakan ikon placeholder dari Android
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Ikon Toko",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Judul Login
        Text(
            text = stringResource(id = R.string.login),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = loginUIState.username,
            onValueChange = { loginViewModel.updateLoginUIState(loginUIState.copy(username = it)) },
            label = { Text(stringResource(id = R.string.username)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Password
        OutlinedTextField(
            value = loginUIState.pass,
            onValueChange = { loginViewModel.updateLoginUIState(loginUIState.copy(pass = it)) },
            label = { Text(stringResource(id = R.string.password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Log In
        Button(
            onClick = {
                loginViewModel.tryLogin(
                    onSuccess = onLoginSuccess,
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD8FF00)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(stringResource(id = R.string.log_in), color = Color.Black, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Lupa Password
        TextButton(onClick = onForgotPasswordClicked) {
            Text(stringResource(id = R.string.lupa_password), color = LocalContentColor.current)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = stringResource(id = R.string.atau), color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        // Tombol Buat Akun
        Button(
            onClick = onCreateAccountClicked,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(stringResource(id = R.string.buat_akun), color = Color.White, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHalamanLogin() {
    ProjectAkhirTheme {
        HalamanLogin(
            onLoginSuccess = {},
            onForgotPasswordClicked = {},
            onCreateAccountClicked = {}
        )
    }
}
