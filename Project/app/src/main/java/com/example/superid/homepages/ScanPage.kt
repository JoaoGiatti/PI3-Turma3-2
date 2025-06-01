// Declaração do pacote
package com.example.superid.homepages

// Importações de bibliotecas Android e Compose
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
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth
import androidx.camera.core.Preview as CameraPreview

/**
 * Tela de escaneamento de QR Code
 * @param modifier Modificador para personalização do layout
 */
@Composable
fun ScanPage(modifier: Modifier = Modifier) {
    // Estado para armazenar o código QR lido
    var code by remember { mutableStateOf("") }
    // Contexto atual da aplicação
    val context = LocalContext.current
    // Dono do ciclo de vida atual (Activity/Fragment)
    val lifecycleOwner = LocalLifecycleOwner.current
    // Futuro provedor da câmera
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Estado para verificar permissão da câmera
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Estados para verificação de e-mail
    var isEmailVerified by remember { mutableStateOf(false) }
    var isLoadingVerification by remember { mutableStateOf(true) }

    // Instância do Firebase Authentication
    val auth = FirebaseAuth.getInstance()

    /**
     * Função para reenviar e-mail de verificação
     */
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
                        else -> "Falha ao enviar: Muitas tentativas. Tente mais tarde"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
    }

    // Efeito para verificar se o e-mail está validado
    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isEmailVerified = user.isEmailVerified
                } else {
                    Toast.makeText(context, "Erro ao verificar o e-mail.", Toast.LENGTH_SHORT).show()
                }
                isLoadingVerification = false
            }
        } else {
            isLoadingVerification = false
        }
    }

    // Launcher para solicitar permissão da câmera
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    // Efeito para solicitar permissão da câmera quando a tela é carregada
    LaunchedEffect(true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    // Layout principal da tela
    Box(modifier = modifier.fillMaxSize()) {
        // Exibe câmera apenas se tiver permissão e e-mail estiver verificado
        if (hasCameraPermission && isEmailVerified) {
            // View para visualização da câmera
            val previewView = remember { PreviewView(context) }

            // Efeito para configurar e gerenciar o ciclo de vida da câmera
            DisposableEffect(lifecycleOwner) {
                val cameraProvider = cameraProviderFuture.get()
                val preview = CameraPreview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)

                // Configuração da análise de imagem para QR Code
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(previewView.width, previewView.height))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                // Analisador de QR Code
                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QrCodeAnalizer(context) { result ->
                        if (result != code) {
                            code = result
                            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                try {
                    // Vincula os casos de uso da câmera ao ciclo de vida
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Limpeza quando o efeito é descartado
                onDispose {
                    cameraProvider.unbindAll()
                }
            }

            // Exibe a visualização da câmera usando AndroidView
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Alerta de e-mail não verificado
        if (!isEmailVerified && !isLoadingVerification) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { resendVerificationEmail() },
                contentAlignment = Alignment.Center
            ) {
                // Card de alerta
                Card(
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = MaterialTheme.colors.error,
                    elevation = 12.dp,
                    modifier = Modifier
                        .padding(24.dp)
                        .wrapContentSize()
                ) {
                    // Conteúdo do card
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .widthIn(min = 250.dp, max = 320.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Ícone de alerta
                        Image(
                            painter = painterResource(id = R.drawable.alert),
                            contentDescription = "Alerta",
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Título do alerta
                        Text(
                            text = "Valide seu email",
                            style = MaterialTheme.typography.h6,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Mensagem explicativa
                        Text(
                            text = "É necessário validar seu e-mail para usar a função de login sem senha.",
                            style = MaterialTheme.typography.body2,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Instrução para reenviar e-mail
                        Text(
                            text = "Toque em qualquer lugar para reenviar o e-mail",
                            style = MaterialTheme.typography.caption,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}