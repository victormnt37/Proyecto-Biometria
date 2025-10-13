/**
 * MainActivity.java
 * Actividad principal de la aplicación Android para el proyecto de Biometría y Medio Ambiente.
 * Gestiona la inicialización de Bluetooth, el escaneo de dispositivos BLE, el filtrado y envío de mediciones a Firebase.
 *
 * @author Víctor Morant Faus
 * @date 2025
 */

package com.example.victormorantfaus;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

// ------------------------------------------------------------------
// ------------------------------------------------------------------

/**
 * Actividad principal que gestiona la lógica de escaneo BLE y el envío de datos a Firebase.
 */
public class MainActivity extends AppCompatActivity {

    // Etiqueta para los logs de depuración.
    private static final String ETIQUETA_LOG = ">>>>";

    // Código para la petición de permisos.
    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // Referencia al campo de texto para mostrar la última medición.
    private EditText ultimaMedicion = null;

    // Escáner BLE y configuración de dispositivo.
    private BluetoothLeScanner elEscanner;
    private static final String uuidDispositivo = "EPSG-GTI-PROY-3A";
    private ScanCallback callbackDelEscaneo = null;

    // Identificadores de tipo de medición.
    private final int idMedicionCO2 = 11;
    private final int idMedicionTemperatura = 12;

    // Variable para evitar el envío duplicado de mediciones.
    private int ultimoContadorEnviado = -1;

    /**
     * Inicia el escaneo de todos los dispositivos BLE.
     */
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanResult() ");
                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");
            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");
        try {
            this.elEscanner.startScan(this.callbackDelEscaneo);
        } catch (SecurityException e) {
            Log.e(ETIQUETA_LOG, "No tienes permisos para inicializar el Bluetooth", e);
        }
    }

    /**
     * Muestra la información de un dispositivo BLE detectado y filtra los datos relevantes.
     *
     * @param resultado Resultado del escaneo BLE.
     */
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {
        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");

        try {
            Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        } catch (SecurityException e) {
            Log.e(ETIQUETA_LOG, "No tienes permisos para inicializar el Bluetooth", e);
        }
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());
        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        if (tib.getUUID() != null) {
            Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
            Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
            Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
            Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
            Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
            Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
            Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                    + tib.getiBeaconLength() + " ) ");
            Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
            Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
            Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                    + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
            Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                    + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
            Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
            Log.d(ETIQUETA_LOG, " ****************************************************");

            int major = Utilidades.bytesToInt(tib.getMajor());
            int minor = Utilidades.bytesToInt(tib.getMinor());

            try {
                filtrarInformacionBTLE(tib, major, minor, resultado);
            } catch (SecurityException e) {
                Log.e(ETIQUETA_LOG, "No tienes permisos para inicializar el Bluetooth", e);
            }
        } else {
            Log.d(ETIQUETA_LOG, "UUID invalido");
        }
    }

    /**
     * Filtra los datos recibidos del beacon y, si cumplen los requisitos, llama a enviarMedicionFiltrada.
     *
     * @param tib      Trama iBeacon recibida.
     * @param major    Valor major descompuesto.
     * @param minor    Valor minor descompuesto.
     * @param resultado Resultado del escaneo BLE.
     */
    private void filtrarInformacionBTLE(TramaIBeacon tib, int major, int minor, ScanResult resultado) {
        // Filtra por UUID del dispositivo
        if (Objects.equals(Utilidades.bytesToString(tib.getUUID()), uuidDispositivo)) {
            Log.d(ETIQUETA_LOG, " ************************** ES NUESTRO BEACON **************************" + Utilidades.bytesToString(tib.getUUID()));

            // Descomponer el major
            int id = (major >> 8) & 0xFF; // Bits altos → ID
            int contador = major & 0xFF;  // Bits bajos → contador

            String tipoMedicion;
            switch (id) {
                case idMedicionCO2:
                    tipoMedicion = "CO2";
                    break;
                case idMedicionTemperatura:
                    tipoMedicion = "Temperatura";
                    break;
                default:
                    tipoMedicion = "Desconocido";
                    break;
            }

            if (major != 0 || minor != 0) {
                String texto = "Tipo de medicion: " + tipoMedicion + " | Valor: " + minor;
                runOnUiThread(() -> ultimaMedicion.setText(texto));

                // Solo enviar si el contador es diferente al último enviado
                if (contador != ultimoContadorEnviado) {
                    enviarMedicionFiltrada(
                            Utilidades.bytesToString(tib.getUUID()),
                            tipoMedicion,
                            contador,
                            minor,
                            resultado.getRssi()
                    );
                    ultimoContadorEnviado = contador; // Actualiza el último contador enviado
                }
            }
        }
    }

    /**
     * Envía los datos filtrados a Firebase.
     *
     * @param uuid         Identificador único del dispositivo.
     * @param tipoMedicion Tipo de medición ("CO2", "Temperatura", etc.).
     * @param contador     Contador secuencial de la medición.
     * @param valor        Valor de la medición.
     * @param rssi         Intensidad de la señal recibida.
     */
    private void enviarMedicionFiltrada(String uuid, String tipoMedicion, int contador, int valor, int rssi) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.enviarMedicion(uuid, tipoMedicion, contador, valor, rssi);
    }

    /**
     * Inicia el escaneo de un dispositivo BLE específico por nombre.
     *
     * @param dispositivoBuscado Nombre del dispositivo a buscar.
     */
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");
        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");
                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");
            }
        };

        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);

        try {
            this.elEscanner.startScan(this.callbackDelEscaneo);
        } catch (SecurityException e) {
            Log.e(ETIQUETA_LOG, "No tienes permisos para inicializar el Bluetooth", e);
        }
    }

    /**
     * Detiene el escaneo de dispositivos BLE.
     */
    private void detenerBusquedaDispositivosBTLE() {
        if (this.callbackDelEscaneo == null) {
            return;
        }
        try {
            this.elEscanner.stopScan(this.callbackDelEscaneo);
            this.callbackDelEscaneo = null;
        } catch (SecurityException e) {
            Log.e(ETIQUETA_LOG, "No tienes permisos para inicializar el Bluetooth", e);
        }
    }

    /**
     * Método asociado al botón para buscar todos los dispositivos BLE.
     *
     * @param v Vista del botón pulsado.
     */
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado");
        this.buscarTodosLosDispositivosBTLE();
    }

    /**
     * Método asociado al botón para buscar el dispositivo BLE propio.
     *
     * @param v Vista del botón pulsado.
     */
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton nuestro dispositivo BTLE Pulsado");
        this.buscarEsteDispositivoBTLE("fistro");
    }

    /**
     * Método asociado al botón para detener la búsqueda de dispositivos BLE.
     *
     * @param v Vista del botón pulsado.
     */
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado");
        this.detenerBusquedaDispositivosBTLE();
    }

    /**
     * Inicializa el adaptador Bluetooth y solicita permisos si es necesario.
     */
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        try {
            bta.enable();
        } catch (SecurityException e) {
            Log.e(ETIQUETA_LOG, "No tienes permisos para inicializar el Bluetooth", e);
        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled());
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState());
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");
            return;
        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): voy a perdir permisos (si no los tuviera) !!!!");

        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);
        } else {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");
        }
    }

    /**
     * Método llamado al crear la actividad. Inicializa la interfaz y Bluetooth.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ultimaMedicion = findViewById(R.id.ultimaMedicion);

        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");

        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, " onCreate(): termina ");
    }

    /**
     * Callback para el resultado de la petición de permisos.
     *
     * @param requestCode  Código de la petición.
     * @param permissions  Array de permisos solicitados.
     * @param grantResults Resultados de la concesión de permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): permisos concedidos  !!!!");
                } else {
                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");
                }
                return;
        }
        // Otros casos de permisos pueden añadirse aquí.
    }
} // class

// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------