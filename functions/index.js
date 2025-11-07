const functions = require("firebase-functions");
const { getStorage } = require("firebase-admin/storage");
const admin = require("firebase-admin");

admin.initializeApp();

exports.getSecureShareableUrl = functions.https.onCall(async (data, context) => {
  // 1. Kiểm tra xem người dùng đã đăng nhập chưa
  if (!context.auth) {
    throw new functions.https.HttpsError(
      "unauthenticated",
      "Bạn phải đăng nhập để chia sẻ."
    );
  }

  // 2. Lấy đường dẫn file từ app Android
  const filePath = data.filePath; // Ví dụ: "user_uploads/user_id_123/cert.pdf"
  if (!filePath) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "Thiếu đường dẫn file."
    );
  }

  // 3. Tính toán thời gian hết hạn (ví dụ: 24 giờ)
  const expires = Date.now() + 24 * 60 * 60 * 1000; // 24 giờ

  // 4. Tạo Signed URL
  const bucket = admin.storage().bucket(); // Lấy bucket mặc định
  const file = bucket.file(filePath);

  const [signedUrl] = await file.getSignedUrl({
    action: "read",
    expires: expires,
  });

  // 5. Trả URL về cho app Android
  return { shareableLink: signedUrl };
});