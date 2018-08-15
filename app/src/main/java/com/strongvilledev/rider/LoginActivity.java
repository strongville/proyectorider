package com.strongvilledev.rider;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mListener;
    private AlertDialog.Builder mBuilder;

    private EditText mEmail, mPassword;
    private Button mLogin, mSignup;

    private InputMethodManager input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login);
        mSignup = findViewById(R.id.signup);

        input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mBuilder = new AlertDialog.Builder(LoginActivity.this);

        mLogin.setOnClickListener(view -> {

            mLogin.setEnabled(false);
            mSignup.setEnabled(false);

            input.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_HIDDEN);

            if (!validaCampos()) return;

            String correo = mEmail.getText().toString();
            String contra = mPassword.getText().toString();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, contra).addOnCompleteListener(runnable -> {
                if (runnable.isSuccessful()) {
                    Log.i("LoginActivity", "Usuario creado exitosamente, accediendo a lista de rutas...");
                    Intent i = new Intent(LoginActivity.this, RutasActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                else {
                    Log.i("LoginActivity", "Error de inicio de sesión, comprueba tus credenciales.");
                    mBuilder.setTitle("Error al iniciar sesión")
                            .setMessage("El correo electrónico o la contraseña son incorrectos. ¿Ya estás registrado?")
                            .setPositiveButton("Aceptar", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                mLogin.setEnabled(true);
                                mSignup.setEnabled(true);
                            });
                    mBuilder.create().show();
                }
            });
        });

        mSignup.setOnClickListener(view -> {

            mLogin.setEnabled(false);
            mSignup.setEnabled(false);

            input.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_HIDDEN);

            if (!validaCampos()) return;

            String correo = mEmail.getText().toString();
            String contra = mPassword.getText().toString();

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contra).addOnCompleteListener(runnable -> {
                if (runnable.isSuccessful()) {
                    Log.i("LoginActivity", "Usuario creado exitosamente, accediendo a lista de rutas...");
                    Intent i = new Intent(LoginActivity.this, RutasActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                else {
                    Log.e("LoginActivity", "Error al crear el usuario.");

                    mBuilder.setTitle("Error al registrarse")
                            .setMessage("Favor de intentarlo más tarde.")
                            .setPositiveButton("Aceptar", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                mLogin.setEnabled(true);
                                mSignup.setEnabled(true);
                            });

                    if (runnable.getException() instanceof FirebaseAuthUserCollisionException) {
                        Log.e("LoginActivity", "El correo ya está registrado.");
                        mBuilder.setMessage("La dirección de correo electrónico ya existe. Intenta iniciando sesión.");
                    }

                    mBuilder.create().show();
                }
            });
        });

        mListener = firebaseAuth -> {
            FirebaseUser usuario = firebaseAuth.getCurrentUser();
            if (usuario != null) {
                Log.i("LoginActivity", "Sesión iniciada, accediendo a lista de rutas...");
                Intent i = new Intent(LoginActivity.this, RutasActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            else {
                Toast.makeText(LoginActivity.this, "No hay sesión iniciada.", Toast.LENGTH_SHORT).show();
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

    private boolean validaCampos() {
        String correo = mEmail.getText().toString();
        String contra = mPassword.getText().toString();

        if (correo.isEmpty() || contra.isEmpty()) {
            mBuilder.setTitle("")
                    .setMessage("Introduce tu correo y contraseña para iniciar sesión o registrarse.")
                    .setPositiveButton("Aceptar", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        mLogin.setEnabled(true);
                        mSignup.setEnabled(true);
                    });
            mBuilder.create().show();
            return false;
        }

        if (contra.length() < 6) {
            Toast.makeText(LoginActivity.this, "La contraseña debe ser de mínimo 6 caracteres.", Toast.LENGTH_SHORT).show();
            mLogin.setEnabled(true);
            mSignup.setEnabled(true);
            return false;
        }

        return true;
    }
}
