import { initializeApp } from "https://www.gstatic.com/firebasejs/12.3.0/firebase-app.js";
import {
  getDatabase,
  ref,
  query,
  orderByChild,
  limitToLast,
  onValue,
} from "https://www.gstatic.com/firebasejs/12.3.0/firebase-database.js";

const firebaseConfig = {
  apiKey: "AIzaSyARkCrhm8d9FU41sd5tDWyhQGb8oOa4hXs",
  authDomain: "proyecto-biometria-12925.firebaseapp.com",
  projectId: "proyecto-biometria-12925",
  storageBucket: "proyecto-biometria-12925.firebasestorage.app",
  messagingSenderId: "820888531568",
  appId: "1:820888531568:web:b304c79f70ca5b7b4ffab5",
  databaseURL:
    "https://proyecto-biometria-12925-default-rtdb.europe-west1.firebasedatabase.app",
};

const app = initializeApp(firebaseConfig);
const db = getDatabase(app);

const medicionesQuery = query(
  ref(db, "mediciones"),
  orderByChild("timestamp"),
  limitToLast(1)
);

function tipoMedicionStr(tipo) {
  if (tipo === "11") return "Temperatura";
  if (tipo === "12") return "CO2";
  return "Desconocido";
}

function formateaFecha(timestamp) {
  const fecha = new Date(timestamp);
  return fecha.toLocaleString();
}

onValue(medicionesQuery, (snapshot) => {
  const data = snapshot.val();
  const medElem = document.getElementById("med");
  if (data) {
    const last = Object.values(data)[0];
    medElem.innerHTML = `
    <table>
      <tr>
        <th>Contador</th>
        <td>${last.contador}</td>
      </tr>
      <tr>
        <th>Tipo</th>
        <td>${tipoMedicionStr(last.tipo)}</td>
      </tr>
      <tr>
        <th>Valor</th>
        <td>${
          last.tipo === "11"
            ? `${last.valor} ºC`
            : last.tipo === "12"
            ? `${last.valor} ppm`
            : last.valor
        }</td>
      </tr>
      <tr>
        <th>RSSI</th>
        <td>${last.rssi} dBm</td>
      </tr>
      <tr>
        <th>Fecha</th>
        <td>${formateaFecha(last.timestamp)}</td>
      </tr>
    </table>
    `;
  } else {
    medElem.textContent = "Sin datos";
  }
});

// TODO: crear tabla con datos de las útimas mediciones
// idealmente se pueden filtrar usando los ths