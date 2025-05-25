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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.camera.core.Preview as CameraPreview

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

    Column(
        modifier = modifier.fillMaxSize()
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
}