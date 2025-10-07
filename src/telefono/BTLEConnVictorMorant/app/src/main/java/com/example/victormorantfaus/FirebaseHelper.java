package com.example.victormorantfaus;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private static final String DATOS_PATH = "mediciones";

    private final DatabaseReference databaseReference;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference(DATOS_PATH);
    }

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

    public static class Medicion {
        public String uuid;
        public String tipo;
        public int contador;
        public int valor;
        public int rssi;
        public long timestamp;

        public Medicion() {}

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