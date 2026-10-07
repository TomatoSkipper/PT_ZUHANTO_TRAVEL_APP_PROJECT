const { initializeApp, cert } = require('firebase-admin/app');
const { getAuth } = require('firebase-admin/auth');
const serviceAccount = require('./serviceAccountKey.json');

initializeApp({
  credential: cert(serviceAccount)
});

// Admin's UID from Firebase Console
const adminUid = "ODLmPWGp3QTjpzunfKJHmbZDosd2";

getAuth().setCustomUserClaims(adminUid, { admin: true })
  .then(() => {
    console.log(`Success! Granted admin access to phone user: ${adminUid}`);
    process.exit();
  })
  .catch((error) => console.error("Error assigning claim:", error));
