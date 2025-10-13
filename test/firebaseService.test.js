/**
 * Pruebas unitarias para el módulo de servicios de consultas a Firebase Realtime Database.
 * Verifica el correcto funcionamiento de las funciones getUltimaMedicion y getHistorialMediciones.
 *
 * @author Víctor Morant Faus
 * @date 2025
 */

import { getUltimaMedicion, getHistorialMediciones } from "../src/firebaseService.js";
import FirebaseMock from "firebase-mock";

// Crea una instancia mock del Realtime Database para pruebas
const mockDatabase = new FirebaseMock.MockFirebase();
const db = mockDatabase.database();

/**
 * Carga datos de ejemplo en la base de datos mock antes de cada test.
 */
beforeEach(() => {
  mockDatabase.autoFlush(); // ejecuta callbacks automáticamente
  mockDatabase.ref("mediciones").set({
    "m1": { contador: 1, rssi: -60, timestamp: 1000, tipo: "Temperatura", uuid: "EPSG-GTI-PROY-3A", valor: "22.5" },
    "m2": { contador: 2, rssi: -65, timestamp: 2000, tipo: "Temperatura", uuid: "EPSG-GTI-PROY-3A", valor: "23.0" },
    "m3": { contador: 3, rssi: -70, timestamp: 3000, tipo: "Temperatura", uuid: "EPSG-GTI-PROY-3A", valor: "23.4" }
  });
});

/**
 * TEST 1: Verifica que getUltimaMedicion devuelve la última medición por timestamp.
 */
test("getUltimaMedicion devuelve la última medición por timestamp", (done) => {
  getUltimaMedicion(db, (medicion) => {
    try {
      expect(medicion.valor).toBe("23.4");
      expect(medicion.timestamp).toBe(3000);
      done();
    } catch (error) {
      done(error);
    }
  });
});

/**
 * TEST 2: Verifica que getHistorialMediciones devuelve las mediciones ordenadas descendentemente por fecha.
 */
test("getHistorialMediciones devuelve las mediciones ordenadas descendentemente", (done) => {
  getHistorialMediciones(db, 2, (mediciones) => {
    try {
      expect(mediciones.length).toBe(2);
      expect(mediciones[0].timestamp).toBeGreaterThan(mediciones[1].timestamp);
      done();
    } catch (error) {
      done(error);
    }
  });
});