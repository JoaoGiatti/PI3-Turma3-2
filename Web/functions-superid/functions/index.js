const functions = require("firebase-functions");
const admin = require("firebase-admin");
const QRCode = require("qrcode");
const cors = require("cors")({ origin: true }); // <- Aqui

admin.initializeApp();
const db = admin.firestore();

function generateRandomToken(length = 256) {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  let token = "";
  for (let i = 0; i < length; i++) {
    token += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return token;
}

exports.performAuth = functions.https.onRequest((req, res) => {
  cors(req, res, async () => { // <- Envolve a lógica principal
    if (req.method !== "POST") {
      return res.status(405).send("Método não permitido");
    }

    const { apiKey, siteUrl } = req.body;

    if (!apiKey || !siteUrl) {
      return res.status(400).json({ error: "apiKey e siteUrl são obrigatórios" });
    }

    try {
      const partnerSnapshot = await db
        .collection("partners")
        .where("apiKey", "==", apiKey)
        .where("url", "==", siteUrl)
        .get();

      if (partnerSnapshot.empty) {
        return res.status(401).json({ error: "Parceria inválida" });
      }

      const loginToken = generateRandomToken();

      await db.collection("login").add({
        apiKey,
        siteUrl,
        loginToken,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      });

      const qrCodeBase64 = await QRCode.toDataURL(loginToken);

      return res.status(200).json({
        loginToken,
        qrCodeBase64: qrCodeBase64.replace(/^data:image\/png;base64,/, ""),
      });
    } catch (err) {
      console.error("Erro ao gerar QR Code:", err);
      return res.status(500).json({ error: "Erro interno ao gerar QR Code" });
    }
  });
});

exports.getLoginStatus = functions.https.onRequest(async (req, res) => {
  if (req.method !== "POST") {
    return res.status(405).send("Método não permitido");
  }

  const { loginToken } = req.body;

  if (!loginToken) {
    return res.status(400).json({ error: "loginToken é obrigatório" });
  }

  try {
    const snapshot = await db.collection("login")
      .where("loginToken", "==", loginToken)
      .limit(1)
      .get();

    if (snapshot.empty) {
      return res.status(404).json({ status: "not_found" });
    }

    const doc = snapshot.docs[0];
    const data = doc.data();

    if (data.user) {
      return res.status(200).json({ status: "authorized", uid: data.user });
    } else {
      return res.status(200).json({ status: "pending" });
    }
  } catch (err) {
    console.error("Erro ao verificar status:", err);
    return res.status(500).json({ error: "Erro interno ao consultar status" });
  }
});

