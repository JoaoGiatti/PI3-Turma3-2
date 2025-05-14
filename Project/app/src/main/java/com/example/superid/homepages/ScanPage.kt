package com.example.superid.homepages

import android.hardware.camera2.CameraMetadata.LENS_FACING_BACK
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview as CameraPreview

@Preview
@Composable
fun ScanPage(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        AndroidView(factory = { context ->
            val previewView = PreviewView(context)
            val preview = CameraPreview.Builder().build()
            val selector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            preview.setSurfaceProvider((previewView.surfaceProvider))
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(
                    previewView.width,
                    previewView.height
                ))
                // Caso o framerate da câmera seja maior que a capacidade de processamento do dispositivo
                .setBackpressureStrategy(

                )
        })
    }
}