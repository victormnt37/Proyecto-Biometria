/**
 * Cliente web para el proyecto de Biometría y Medio Ambiente.
 * Inicializa la conexión con Firebase y muestra la última medición y el historial de mediciones en la interfaz.
 * 
 * @author Víctor Morant Faus
 * @date 2025
 */

import { initializeApp } from "https://www.gstatic.com/firebasejs/12.3.0/firebase-app.js";
import { getDatabase } from "https://www.gstatic.com/firebasejs/12.3.0/firebase-database.js";
import { getUltimaMedicion, getHistorialMediciones } from "./firebaseService.js";

/**
 * Configuración de Firebase para la aplicación web.
 */
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

// Inicializa la app de Firebase y la base de datos
const app = initializeApp(firebaseConfig);
const db = getDatabase(app);

/**
 * Formatea un timestamp en milisegundos a una cadena de fecha legible.
 * @param {number} timestamp - Fecha en milisegundos desde Epoch.
 * @returns {string} Fecha y hora en formato local.
 */
function formateaFecha(timestamp) {
  const fecha = new Date(timestamp);
  return fecha.toLocaleString();
}

/**
 * Consulta y muestra la última medición en la interfaz.
 */
getUltimaMedicion(db, (last) => {
  const medElem = document.getElementById("med");
  if (last) {
    medElem.innerHTML = `
      <table>
        <tr><th>Contador</th><td>${last.contador}</td></tr>
        <tr><th>Tipo</th><td>${last.tipo}</td></tr>
        <tr><th>Valor</th><td>${
          last.tipo === "11"
            ? `${last.valor} ºC`
            : last.tipo === "12"
            ? `${last.valor} ppm`
            : last.valor
        }</td></tr>
        <tr><th>RSSI</th><td>${last.rssi} dBm</td></tr>
        <tr><th>Fecha</th><td>${formateaFecha(last.timestamp)}</td></tr>
      </table>
    `;
  } else {
    medElem.textContent = "Sin datos";
  }
});

/**
 * Consulta y muestra el historial de las últimas 10 mediciones en la interfaz.
 */
getHistorialMediciones(db, 10, (mediciones) => {
  const tablaElem = document.getElementById("tabla-mediciones");
  if (mediciones.length > 0) {
    let tablaHtml = `
      <table>
        <thead>
          <tr>
            <th>Contador</th>
            <th>Tipo</th>
            <th>Valor</th>
            <th>RSSI</th>
            <th>Fecha</th>
          </tr>
        </thead>
        <tbody>
    `;
    mediciones.forEach(med => {
      tablaHtml += `
        <tr>
          <td>${med.contador}</td>
          <td>${med.tipo}</td>
          <td>${
            med.tipo === "11"
              ? `${med.valor} ºC`
              : med.tipo === "12"
              ? `${med.valor} ppm`
              : med.valor
          }</td>
          <td>${med.rssi} dBm</td>
          <td>${formateaFecha(med.timestamp)}</td>
        </tr>
      `;
    });
    tablaHtml += `</tbody></table>`;
    tablaElem.innerHTML = tablaHtml;
  } else {
    tablaElem.textContent = "Sin datos";
  }
});