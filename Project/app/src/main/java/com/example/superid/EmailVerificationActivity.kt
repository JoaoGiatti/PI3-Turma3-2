package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailVerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmailVerificationScreen()
        }
    }
}

@Composable
fun EmailVerificationScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var emailVerified by remember { mutableStateOf(user?.isEmailVerified ?: false) }
    var showAlertDialog by remember { mutableStateOf(false) }

    val yellow = Color(0xFFE2DA06)
    val darkGray = Color(0xFF131313)
    val textWhite = Color(0xFFFFFFFF)

    // Função para verificar o e-mail
    fun verifyEmail() {
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                emailVerified = user?.isEmailVerified == true
                if (emailVerified) {
                    // Se o e-mail estiver verificado, vai para a tela principal
                    context.startActivity(Intent(context, HomeActivity::class.java))
                    (context as? ComponentActivity)?.finish()
                } else {
                    Toast.makeText(context, "E-mail não confirmado. Verifique sua caixa de entrada!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Erro ao verificar o e-mail.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função para prosseguir sem verificar
    fun proceedWithoutVerification() {
        showAlertDialog = true
    }

    // Alert Dialog de confirmação para prosseguir sem verificar o e-mail
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            containerColor = Color.DarkGray, // Fundo do AlertDialog
            title = {
                Text(text = "Atenção!", color = Color.Yellow, fontFamily = FontFamily(Font(R.font.interbold))) // Título branco
            },
            text = {
                Text(
                    "Caso você não valide seu e-mail, não poderá usar a funcionalidade de Login Sem Senha. Deseja continuar?",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.interregular))
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    // Prossegue sem verificação
                    context.startActivity(Intent(context, HomeActivity::class.java))
                    (context as? ComponentActivity)?.finish()
                }) {
                    Text("Sim", color = Color.White, fontFamily = FontFamily(Font(R.font.interbold)))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAlertDialog = false }) {
                    Text("Cancelar", color = Color.Yellow, fontFamily = FontFamily(Font(R.font.interbold)))
                }
            }
        )
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGray),
        color = darkGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.padding(22.dp))

            // Header com seta e logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.arrowback),
                    contentDescription = "Voltar",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            (context as? ComponentActivity)?.finish()
                        }
                )
                Spacer(modifier = Modifier.width(72.dp))
                Image(
                    painter = painterResource(id = R.drawable.superidlogowhiteyellow),
                    contentDescription = "Logo SuperID",
                    modifier = Modifier
                        .height(24.dp) // ajusta o tamanho se quiser
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            // Título principal
            Text(
                text = "Te enviamos um email\nde confirmação!",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.poppinsbold)),
                color = textWhite,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(22.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Confirme a sua identidade antes de prosseguir",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.interregular)),
                color = textWhite,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 38.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Ícone de e-mail
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.emailicon), // seu ícone de e-mail
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Texto explicativo
            Text(
                text = "Após confirmar o email, clique em\n"
                        + "Email já verificado.",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.interregular)),
                color = textWhite,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Botão "Verificar"
            Button(
                onClick = { verifyEmail() },
                colors = ButtonDefaults.buttonColors(containerColor = yellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Email já verificado!", color = Color.Black, fontFamily = FontFamily(Font(R.font.interbold)))
            }

            // Botão "Prosseguir sem verificar"
            Button(
                onClick = { proceedWithoutVerification() },
                colors = ButtonDefaults.buttonColors(containerColor = yellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Prosseguir sem verificar", color = Color.Black, fontFamily = FontFamily(Font(R.font.interbold)))
            }
        }
    }
}
