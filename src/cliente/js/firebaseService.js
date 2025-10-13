/**
 * Módulo de servicios para consultas a Firebase Realtime Database.
 * Proporciona funciones para obtener la última medición y el historial de mediciones.
 * 
 * @author Víctor Morant Faus
 * @date 2025
 */

import {
  getDatabase,
  ref,
  query,
  orderByChild,
  limitToLast,
  onValue,
} from "https://www.gstatic.com/firebasejs/12.3.0/firebase-database.js";

/**
 * Obtiene la última medición registrada en la base de datos.
 * 
 * @param {Database} db - Instancia de la base de datos Firebase.
 * @param {function(Object|null):void} callback - Función que recibe la última medición o null si no hay datos.
 */
export function getUltimaMedicion(db, callback) {
  const medicionesQuery = query(
    ref(db, "mediciones"),
    orderByChild("timestamp"),
    limitToLast(1)
  );
  onValue(medicionesQuery, (snapshot) => {
    const data = snapshot.val();
    callback(data ? Object.values(data)[0] : null);
  });
}

/**
 * Obtiene el historial de las últimas mediciones registradas, ordenadas por fecha descendente.
 * 
 * @param {Database} db - Instancia de la base de datos Firebase.
 * @param {number} cantidad - Número de mediciones a recuperar.
 * @param {function(Array<Object>):void} callback - Función que recibe un array de mediciones (vacío si no hay datos).
 */
export function getHistorialMediciones(db, cantidad, callback) {
  const ultimasMedicionesQuery = query(
    ref(db, "mediciones"),
    orderByChild("timestamp"),
    limitToLast(cantidad)
  );
  onValue(ultimasMedicionesQuery, (snapshot) => {
    const data = snapshot.val();
    if (data) {
      const mediciones = Object.values(data).sort((a, b) => b.timestamp - a.timestamp);
      callback(mediciones);
    } else {
      callback([]);
    }
  });
}