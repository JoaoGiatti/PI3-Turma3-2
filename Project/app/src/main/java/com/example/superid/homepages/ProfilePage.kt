package com.example.superid.homepages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superid.profile.ProfileViewModel

@Composable
fun ProfilePage(viewModel: ProfileViewModel = viewModel()) {
    val item = viewModel.userItem.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color(0xFF121212)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Meu Perfil",
            style = MaterialTheme.typography.h6,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ProfileField(label = "Nome", value = item.nome)
        ProfileField(label = "Email", value = item.emailMestre)
        ProfileField(label = "Senha", value = item.senhaMestre)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* ação futura */ },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFE2DA06),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sair", fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(text = label, color = Color.Yellow, style = MaterialTheme.typography.caption)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.Red, style = MaterialTheme.typography.body1)
    }
}

