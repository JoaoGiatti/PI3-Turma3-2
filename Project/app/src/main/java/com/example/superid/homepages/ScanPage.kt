package com.example.superid.homepages

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.camera.core.Preview as CameraPreview
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ScanPage(modifier: Modifier = Modifier) {
    var code by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Estado para verificar se o e-mail está verificado
    var isEmailVerified by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    // Função para reenviar o email de verificação
    fun resendVerificationEmail() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(context, "Erro: Usuário não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Email de verificação enviado para ${user.email}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val error = task.exception?.message ?: "Erro desconhecido"
                    val message = when {
                        error.contains("network", true) -> "Falha de rede. Verifique sua conexão"
                        error.contains("too many", true) -> "Muitas tentativas. Tente mais tarde"
                        else -> "Falha ao enviar: $error"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
    }

    // Verificar o status de verificação do e-mail
    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isEmailVerified = user.isEmailVerified
                } else {
                    Toast.makeText(context, "Erro ao verificar o e-mail.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    // Usando DisposableEffect para controlar a inicialização e liberação da câmera
    val previewView = remember { PreviewView(context) } // Definir PreviewView fora da DisposableEffect
    DisposableEffect(lifecycleOwner) {
        // Código de inicialização da câmera
        val cameraProvider = cameraProviderFuture.get()
        val preview = CameraPreview.Builder().build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(previewView.width, previewView.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(context),
            QrCodeAnalizer(context) { result ->
                // Bloquear processamento se e-mail não estiver validado
                if (!isEmailVerified) {
                    return@QrCodeAnalizer
                }

                if (result != code) {
                    code = result
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Bindando a câmera e a análise de imagem ao ciclo de vida
        try {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Garantindo que ao sair da tela a câmera seja liberada
        onDispose {
            // Libera a câmera e qualquer recurso quando sair da tela
            cameraProvider.unbindAll()
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { context ->
                        previewView
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Banner de email não verificado (sobreposto na câmera)
        if (!isEmailVerified) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { resendVerificationEmail() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colors.error,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.alert),
                        contentDescription = "Alerta",
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Valide seu email",
                        color = Color.White,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Para usar a função de login sem senha!",
                        color = Color.White,
                        style = MaterialTheme.typography.subtitle1,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))



                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Clique em qualquer lugar para fechar",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}