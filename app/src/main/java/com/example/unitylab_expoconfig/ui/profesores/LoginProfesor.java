package com.example.unitylab_expoconfig.ui.profesores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.ProfesorBD;
import com.example.unitylab_expoconfig.ui.inicio.BienvenidaActivity;

public class LoginProfesor extends AppCompatActivity {

    private EditText editTextEmpleado, editTextPassword;
    private DbmsSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_profesores);

        dbHelper = new DbmsSQLiteHelper(this);
        editTextEmpleado = findViewById(R.id.editTextBoleta); // Cambiar ID en XML si es necesario
        editTextPassword = findViewById(R.id.editTextPassword);

        Button loginProfesor = findViewById(R.id.buttonLogin);
        TextView textNoProfesor = findViewById(R.id.textViewNoProfesor);
        TextView textRegistrarse = findViewById(R.id.textViewRegistrarse);

        loginProfesor.setOnClickListener(v -> validarProfesor());

        textNoProfesor.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(LoginProfesor.this,
                        Class.forName("com.example.unitylab_expoconfig.MainActivity"));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la pantalla", Toast.LENGTH_SHORT).show();
            }
        });

        textRegistrarse.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroProfesorActivity.class));
        });
    }

    private void validarProfesor() {
        String numEmpleado = editTextEmpleado.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (numEmpleado.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.buscarProfesorPorNumEmpleado(numEmpleado);

        if (cursor != null && cursor.moveToFirst()) {
            String passwordGuardada = cursor.getString(cursor.getColumnIndexOrThrow(ProfesorBD.COL_PASSWORD));

            if (password.equals(passwordGuardada)) {
                int idProfesor = cursor.getInt(cursor.getColumnIndexOrThrow(ProfesorBD.COL_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ProfesorBD.COL_NOMBRE));

                Intent intent = new Intent(this, BienvenidaActivity.class);
                intent.putExtra("tipoUsuario", "profesor");
                intent.putExtra("idUsuario", idProfesor);
                intent.putExtra("nombreUsuario", nombre);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se encontró el profesor", Toast.LENGTH_SHORT).show();
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