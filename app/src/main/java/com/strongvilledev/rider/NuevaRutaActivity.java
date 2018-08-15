package com.strongvilledev.rider;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class NuevaRutaActivity extends AppCompatActivity {

    public static int PLACEPICKER_ORIGEN = 1;
    public static int PLACEPICKER_DESTINO = 2;

    private EditText textoOrigen, textoDestino;
    private Button pickerOrigen, pickerDestino, buscarRutas;
    private SeekBar velocSlider;
    private TextView velocNumero;

    private LatLng mOrigen, mDestino;
    private int mVelocidad;
    private String mPolyRecorrido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_ruta);

        textoOrigen = findViewById(R.id.textoOrigen);
        textoDestino = findViewById(R.id.textoDestino);

        pickerOrigen = findViewById(R.id.pickerOrigen);
        pickerDestino = findViewById(R.id.pickerDestino);
        buscarRutas = findViewById(R.id.buscarRutas);

        velocSlider = findViewById(R.id.velocSlider);
        velocNumero = findViewById(R.id.velocNumero);

        // Solicitar al PlacePicker una ubicación
        pickerOrigen.setOnClickListener(view -> {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(NuevaRutaActivity.this), PLACEPICKER_ORIGEN);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                Log.e("NuevaRutaActivity[PICK]", "Play Services no disponible.");
                Toast.makeText(NuevaRutaActivity.this, "Play Services no disponible.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        pickerDestino.setOnClickListener(view -> {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(NuevaRutaActivity.this), PLACEPICKER_DESTINO);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                Log.e("NuevaRutaActivity[PICK]", "Play Services no disponible.");
                Toast.makeText(NuevaRutaActivity.this, "Play Services no disponible.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        buscarRutas.setOnClickListener(view -> iniciarBusquedaRutas());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACEPICKER_ORIGEN || requestCode == PLACEPICKER_DESTINO) {
            if (resultCode == RESULT_OK) {
                Place seleccion = PlacePicker.getPlace(NuevaRutaActivity.this, data);
                seleccion.getAddress();
                if (requestCode == PLACEPICKER_ORIGEN) {
                    mOrigen = seleccion.getLatLng();
                    textoOrigen.setText(nombreODireccion(seleccion));
                } else {
                    mDestino = seleccion.getLatLng();
                    textoDestino.setText(nombreODireccion(seleccion));
                }

                if (mOrigen != null && mDestino != null) {
                    buscarRutas.setEnabled(true);
                }
            }
        }
    }

    private String nombreODireccion(Place p) {
        String nombre = p.getName().toString();
        if (nombre.contains("º") && nombre.contains("'") && nombre.contains("\"") && p.getAddress() != null) {
            return p.getAddress().toString();
        }
        return nombre;
    }

    private void iniciarBusquedaRutas() {
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=" + mOrigen.latitude + "," + mOrigen.longitude +
                    "&destination=" + mDestino.latitude + "," + mDestino.longitude +
                    "&alternatives=true&mode=driving&key=" + getResources().getString(R.string.google_maps_key));
            new RutasParser().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    class RutasParser extends AsyncTask<URL, Integer, Void> {
        @Override
        protected Void doInBackground(URL... urls) {
            try {
                URLConnection conn = urls[0].openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String linea = br.readLine();
                while (linea != null) {
                    sb.append(linea).append("\n");
                    linea = br.readLine();
                }

                String jsonString = sb.toString();


            } catch (Exception e) {
                Log.e("diplo", "Error " + e.toString());
            }
            //return imagenDescargada;
            return null;
        }

        @Override
        protected void onPostExecute(Void bitmap) {
            super.onPostExecute(bitmap);
        }
    }
}
