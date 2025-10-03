const firebaseConfig = {
  apiKey: "TU_API_KEY",
  authDomain: "TU_AUTH_DOMAIN",
  databaseURL: "TU_DATABASE_URL",
  projectId: "TU_PROJECT_ID",
  storageBucket: "TU_STORAGE_BUCKET",
  messagingSenderId: "TU_MESSAGING_SENDER_ID",
  appId: "TU_APP_ID"
};

firebase.initializeApp(firebaseConfig);

const db = firebase.database();

db.ref('mediciones').orderByChild('timestamp').limitToLast(1).on('value', snapshot => {
  const data = snapshot.val();
  if (data) {
    const last = Object.values(data)[0];
    document.getElementById('med').textContent = JSON.stringify(last);
  } else {
    document.getElementById('med').textContent = 'Sin datos';
  }
});