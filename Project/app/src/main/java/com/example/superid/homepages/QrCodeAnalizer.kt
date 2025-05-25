package com.example.superid.homepages

import android.content.Context
import android.graphics.ImageFormat
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QrCodeAnalizer(
    private val context: Context,
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )

    override fun analyze(image: ImageProxy) {
        if (image.format in supportedImageFormats) {
            val bytes = image.planes.first().buffer.toByteArray()

            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            val binaryBmp = BinaryBitmap(HybridBinarizer(source))

            try {
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)
                        )
                    )
                }.decode(binaryBmp)

                val loginToken = result.text
                onQrCodeScanned(loginToken)

                val currentUser = FirebaseAuth.getInstance().currentUser
                val uid = currentUser?.uid
                val db = FirebaseFirestore.getInstance()

                if (uid != null) {
                    db.collection("login")
                        .whereEqualTo("loginToken", loginToken)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { docs ->
                            if (!docs.isEmpty) {
                                val doc = docs.documents[0]
                                val docId = doc.id
                                val alreadySet = doc.contains("user")

                                if (!alreadySet) {
                                    db.collection("login").document(docId).update(
                                        mapOf(
                                            "user" to uid,
                                            "timestamp" to FieldValue.serverTimestamp()
                                        )
                                    ).addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Login autorizado com sucesso!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Erro ao autorizar login.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "QR Code já utilizado.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Token inválido!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Erro na consulta ao banco de dados.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Usuário não logado.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                image.close()
            }
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also { get(it) }
    }
}
