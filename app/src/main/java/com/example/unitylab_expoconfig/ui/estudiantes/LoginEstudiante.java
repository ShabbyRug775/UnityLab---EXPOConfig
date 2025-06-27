package com.example.unitylab_expoconfig.ui.estudiantes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.AlumnoBD;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
//import com.example.unitylab_expoconfig.SQLite.EstudianteBD;
import com.example.unitylab_expoconfig.ui.inicio.EstudianteActivity;

public class LoginEstudiante extends AppCompatActivity {

    private EditText editTextBoleta, editTextPassword;
    private DbmsSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_estudiantes);

        dbHelper = new DbmsSQLiteHelper(this);
        editTextBoleta = findViewById(R.id.editTextBoleta);
        editTextPassword = findViewById(R.id.editTextPassword);

        Button loginEstudiante = findViewById(R.id.buttonLogin);
        TextView textNoEstudiante = findViewById(R.id.textViewNoEstudiante);
        TextView textRegistrarse = findViewById(R.id.textViewRegistrarse);

        loginEstudiante.setOnClickListener(v -> validarEstudiante());

        textNoEstudiante.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(LoginEstudiante.this,
                        Class.forName("com.example.unitylab_expoconfig.MainActivity"));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la pantalla", Toast.LENGTH_SHORT).show();
            }
        });

        textRegistrarse.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroEstudianteActivity.class));
        });
    }

    private void validarEstudiante() {
        int boleta = Integer.parseInt(editTextBoleta.getText().toString().trim());
        String password = editTextPassword.getText().toString().trim();

        if (boleta <= 0 || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.obtenerAlumnoPorBoleta(boleta);

        if (cursor != null && cursor.moveToFirst()) {
            String passwordGuardada = cursor.getString(cursor.getColumnIndexOrThrow(AlumnoBD.COL_CONTRASEÑA));

            // En una app real, aquí compararías el hash de la contraseña
            if (password.equals(passwordGuardada)) {
                int idEstudiante = cursor.getInt(cursor.getColumnIndexOrThrow(AlumnoBD.COL_BOLETA));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(AlumnoBD.COL_NOMBRE));

                // Redirigir a Bienvenida con datos del estudiante
                Intent intent = new Intent(this, EstudianteActivity.class);
                intent.putExtra("tipoUsuario", "estudiante");
                intent.putExtra("idUsuario", idEstudiante);
                intent.putExtra("nombreUsuario", nombre);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se encontró el estudiante", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.cerrarConexion();
        super.onDestroy();
    }
}