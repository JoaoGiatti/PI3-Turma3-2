/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
const QRCode = require("qrcode");

admin.initializeApp();
const db = admin.firestore();

function generateRandomToken(length = 256) {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"; // base64
  let token = "";
  for (let i = 0; i < length; i++) {
    token += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return token;
}

exports.performAuth = functions.https.onRequest(async (req, res) => {
  if (req.method !== "POST") {
    return res.status(405).send("Método não permitido");
  }

  const { apiKey, siteUrl } = req.body;

  if (!apiKey || !siteUrl) {
    return res.status(400).json({ error: "apiKey e siteUrl são obrigatórios" });
  }

  try {
    // Verifica se o parceiro existe
    const partnerSnapshot = await db.collection("partners")
      .where("apiKey", "==", apiKey)
      .where("url", "==", siteUrl)
      .get();

    if (partnerSnapshot.empty) {
      return res.status(401).json({ error: "Parceria inválida" });
    }

    const loginToken = generateRandomToken();

    // Salva o documento de login
    await db.collection("login").add({
      apiKey,
      siteUrl,
      loginToken,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    // Gera o QRCode em Base64 (com o conteúdo sendo apenas o token)
    const qrCodeBase64 = await QRCode.toDataURL(loginToken);

    return res.status(200).json({
      loginToken,
      qrCodeBase64: qrCodeBase64.replace(/^data:image\/png;base64,/, ""), // apenas a base64 bruta
    });
  } catch (err) {
    console.error("Erro ao gerar QR Code:", err);
    return res.status(500).json({ error: "Erro interno ao gerar QR Code" });
  }
});
// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
