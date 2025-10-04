import { initializeApp } from "https://www.gstatic.com/firebasejs/12.3.0/firebase-app.js";
import { getDatabase, ref, query, orderByChild, limitToLast, onValue } from "https://www.gstatic.com/firebasejs/12.3.0/firebase-database.js";

const firebaseConfig = {
  apiKey: "AIzaSyARkCrhm8d9FU41sd5tDWyhQGb8oOa4hXs",
  authDomain: "proyecto-biometria-12925.firebaseapp.com",
  projectId: "proyecto-biometria-12925",
  storageBucket: "proyecto-biometria-12925.firebasestorage.app",
  messagingSenderId: "820888531568",
  appId: "1:820888531568:web:b304c79f70ca5b7b4ffab5",
  databaseURL: "https://proyecto-biometria-12925-default-rtdb.europe-west1.firebasedatabase.app"
};

const app = initializeApp(firebaseConfig);
const db = getDatabase(app);

const medicionesQuery = query(
  ref(db, 'mediciones'),
  orderByChild('timestamp'),
  limitToLast(1)
);

onValue(medicionesQuery, (snapshot) => {
  const data = snapshot.val();
  if (data) {
    const last = Object.values(data)[0];
    document.getElementById('med').textContent = JSON.stringify(last);
  } else {
    document.getElementById('med').textContent = 'Sin datos';
  }
});