package com.example.superid.homepages

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superid.R
import com.example.superid.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll

@Composable
fun ProfilePage(viewModel: ProfileViewModel = viewModel()) {
    val item = viewModel.userItem.value
    val context = LocalContext.current
    val colors = MaterialTheme.colors
    val typography = MaterialTheme.typography

    // Estado para verificar se o e-mail está verificado
    var isEmailVerified by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Verificar o status de verificação do e-mail
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isEmailVerified = user.isEmailVerified
            } else {
                Toast.makeText(context, "Erro ao verificar o e-mail.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top

    ) {
        // Topo: menu + logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 32.dp)
        ) {
            val imageResArrow = R.drawable.menu
            Image(
                painter = painterResource(id = imageResArrow),
                contentDescription = "Voltar",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { (context as? ComponentActivity)?.finish() }
            )

            Spacer(modifier = Modifier.width(84.dp))

            val imageResLogo = R.drawable.superid
            Image(
                painter = painterResource(id = imageResLogo),
                contentDescription = "Logo SuperID",
                modifier = Modifier.height(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Alerta de verificação de e-mail
        if (!isEmailVerified) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.Black)
                    .border(1.dp, Color.Red, RoundedCornerShape(4.dp))
                    .height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Alerta: valide o email para usar todas as funções",
                    color = Color.Red,
                    style = typography.subtitle2,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

            // Avatar
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.profileicon),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .border(3.dp, Color.White, CircleShape)
                        .padding(3.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nome e data
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.nome,
                    color = Color.White,
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Entrou em 12 de Abril",
                    color = Color.Gray,
                    style = MaterialTheme.typography.caption
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Divisor horizontal
            Divider(
                color = Color.DarkGray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de informação
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Campo Nome
                Text(
                    text = "Nome:",
                    color = Color.White,
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = item.nome,
                    color = Color.White,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.DarkGray, thickness = 1.dp)

                // Campo Email
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Email:",
                    color = Color.White,
                    style = MaterialTheme.typography.body2
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.emailMestre,
                        color = Color.Gray,
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = "Valide seu Email",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.clickable { /* ação de validação */ }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.DarkGray, thickness = 1.dp)

                // Campo Senha
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Senha:",
                    color = Color.White,
                    style = MaterialTheme.typography.body2
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "••••••••",
                        color = Color.Gray,
                        style = MaterialTheme.typography.body1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Redefinir senha",
                            color = Color.Yellow,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.clickable { /* ação de redefinição */ }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.keyicon),
                            contentDescription = "Ícone de chave",
                            tint = Color.Yellow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.DarkGray, thickness = 1.dp)

                // Espaço extra para o botão não cobrir conteúdo
                Spacer(modifier = Modifier.height(180.dp))

                // Botão Sair posicionado manualmente - AGORA FUNCIONANDO
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        // Navegar para tela de login
                    },
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
}
