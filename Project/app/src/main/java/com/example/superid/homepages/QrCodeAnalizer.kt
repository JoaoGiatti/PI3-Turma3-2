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
                                val loginDoc = docs.documents[0]
                                val loginDocId = loginDoc.id
                                val siteUrl = loginDoc.getString("siteUrl") ?: ""

                                // busca a senha do app com url que bate
                                db.collection("user_passwords").document(uid)
                                    .collection("passwords")
                                    .whereEqualTo("url", siteUrl)
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener { passwordDocs ->
                                        if (!passwordDocs.isEmpty) {
                                            val passwordDoc = passwordDocs.documents[0]
                                            val login = passwordDoc.getString("login") ?: ""
                                            val password = passwordDoc.getString("password") ?: ""

                                            // verifica cadastro do usuario no site
                                            db.collection("users_site")
                                                .whereEqualTo("email", login)
                                                .whereEqualTo("password", password)
                                                .limit(1)
                                                .get()
                                                .addOnSuccessListener { siteUsers ->
                                                    if (!siteUsers.isEmpty) {
                                                        // atualiza p login token UID + dados
                                                        db.collection("login").document(loginDocId)
                                                            .update(
                                                                mapOf(
                                                                    "user" to uid,
                                                                    "login" to login,
                                                                    "password" to password,
                                                                    "timestamp" to FieldValue.serverTimestamp()
                                                                )
                                                            )

                                                        // gera noco accessToken e atualiza
                                                        val newAccessToken = generateAccessToken()
                                                        db.collection("user_passwords").document(uid)
                                                            .collection("passwords")
                                                            .document(passwordDoc.id)
                                                            .update("accessToken", newAccessToken)

                                                        Toast.makeText(context, "Login autorizado com sucesso!", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "Credenciais salvas não conferem com o site", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                        } else {
                                            Toast.makeText(context, "Nenhuma senha salva para este site", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(context, "Token de login inválido", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Usuário não logado", Toast.LENGTH_SHORT).show()
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

    private fun generateAccessToken(length: Int = 256): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        return (1..length).map { chars.random() }.joinToString("")
    }
}
