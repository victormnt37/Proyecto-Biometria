/**
 * Clase de ayuda para el envío de datos a Firebase Realtime Database.
 * Proporciona métodos para registrar mediciones en la base de datos.
 *
 * @author Víctor Morant Faus
 * @date 2025
 */

package com.example.victormorantfaus;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Clase que encapsula la lógica de envío de mediciones a Firebase.
 */
public class FirebaseHelper {

    /** Ruta en la base de datos donde se almacenan las mediciones. */
    private static final String DATOS_PATH = "mediciones";

    /** Referencia a la rama de mediciones en Firebase Realtime Database. */
    private final DatabaseReference databaseReference;

    /**
     * Constructor. Inicializa la referencia a la base de datos.
     */
    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference(DATOS_PATH);
    }

    /**
     * Envía una medición a Firebase Realtime Database.
     *
     * @param uuid     Identificador único del dispositivo.
     * @param tipo     Tipo de medición (por ejemplo, "CO2" o "Temperatura").
     * @param contador Contador secuencial de la medición.
     * @param valor    Valor de la medición.
     * @param rssi     Intensidad de la señal recibida.
     */
    public void enviarMedicion(String uuid, String tipo, int contador, int valor, int rssi) {
        Medicion medicion = new Medicion(uuid, tipo, contador, valor, rssi, System.currentTimeMillis());
        databaseReference.push().setValue(medicion)
            .addOnSuccessListener(aVoid ->
                    Log.d(">>>>", "Dato enviado correctamente: " + medicion)
            )
            .addOnFailureListener(e ->
                    Log.e(">>>>", "Error al enviar dato", e)
            );
    }

    /**
     * Clase interna que representa la estructura de una medición.
     */
    public static class Medicion {
        public String uuid;
        public String tipo;
        public int contador;
        public int valor;
        public int rssi;
        public long timestamp;

        /** Constructor vacío requerido por Firebase. */
        public Medicion() {}

        /**
         * Constructor con parámetros.
         *
         * @param uuid     Identificador único del dispositivo.
         * @param tipo     Tipo de medición.
         * @param contador Contador secuencial.
         * @param valor    Valor de la medición.
         * @param rssi     Intensidad de la señal.
         * @param timestamp Marca temporal de la medición.
         */
        public Medicion(String uuid, String tipo, int contador, int valor, int rssi, long timestamp) {
            this.uuid = uuid;
            this.tipo = tipo;
            this.contador = contador;
            this.valor = valor;
            this.rssi = rssi;
            this.timestamp = timestamp;
        }
    }
}