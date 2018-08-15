package com.strongvilledev.rider;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.strongvilledev.rider.pojo.Ruta;

import java.util.ArrayList;

public class RutasActivity extends AppCompatActivity {

    private Button logout;
    private TextView user;
    private FirebaseAuth.AuthStateListener mListener;
    private FirebaseDatabase mDatabase;

    private RecyclerView listaRutas;
    private RutasAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Ruta> mDatos = new ArrayList<>();
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Log.d("RutasActivity", "Nueva ruta solicitada.");
            Intent i = new Intent(RutasActivity.this, NuevaRutaActivity.class);
            startActivity(i);
        });

        logout = findViewById(R.id.logout);
        user = findViewById(R.id.user);
        listaRutas = findViewById(R.id.listaRutas);

        listaRutas.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(RutasActivity.this);
        listaRutas.setLayoutManager(mLayoutManager);

        mAdapter = new RutasAdapter(mDatos);
        listaRutas.setAdapter(mAdapter);

        mDatabase = FirebaseDatabase.getInstance();

        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Log.i("EncuestaActivity", "Se cerró la sesión.");
            Intent i = new Intent(RutasActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        mListener = firebaseAuth -> {
            FirebaseUser usuario = firebaseAuth.getCurrentUser();
            if (usuario == null) {
                Log.i("EncuestaActivity", "Se cerró la sesión.");
                Toast.makeText(RutasActivity.this, "Por favor vuelve a iniciar sesión.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(RutasActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                mEmail = firebaseAuth.getCurrentUser().getEmail();
                user.setText(getResources().getString(R.string.rutas_bienvenido, mEmail));

                String key = mEmail.replace("@", "|").replace(".", "-");
                mDatabase.getReference("usuarios").child(key).child("rutas").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.i("RutasActivity[VEL]", "Número de hijos: " + dataSnapshot.getChildrenCount());

                        mDatos.clear();
                        for (DataSnapshot jsonRuta : dataSnapshot.getChildren()) {
                            Ruta ruta = jsonRuta.getValue(Ruta.class);
                            String textoRuta = "De " + ruta.getOrigen() + " a " + ruta.getDestino() + ", velocidad ";
                            if (ruta.getVelocidad() == 0) {
                                textoRuta += " lenta.";
                            }
                            else if (ruta.getVelocidad() == 1) {
                                textoRuta += " normal.";
                            }
                            else if (ruta.getVelocidad() == 2) {
                                textoRuta += " rápida.";
                            }
                            mDatos.add(ruta);
                            Log.i("RutasActivity[VEL]", "Dato obtenido: " + textoRuta);
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(mListener);
    }

}
