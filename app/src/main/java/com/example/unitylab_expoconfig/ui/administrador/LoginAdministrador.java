package com.example.unitylab_expoconfig.ui.administrador;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.AdministradorBD;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.ui.inicio.BienvenidaActivity;

public class LoginAdministrador extends AppCompatActivity {

    private EditText editTextEmpleado, editTextPassword;
    private DbmsSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_administrador);

        dbHelper = new DbmsSQLiteHelper(this);
        editTextEmpleado = findViewById(R.id.editTextNumEmpleado);
        editTextPassword = findViewById(R.id.editTextPassword);

        Button loginButton = findViewById(R.id.buttonLogin);
        TextView textNoAdmin = findViewById(R.id.textViewNoAdmin);

        loginButton.setOnClickListener(v -> validarAdministrador());

        // Opcional: Redirigir a otro tipo de login si no es administrador
        textNoAdmin.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(LoginAdministrador.this,
                        Class.forName("com.example.unitylab_expoconfig.MainActivity"));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la pantalla", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validarAdministrador() {
        String numEmpleado = editTextEmpleado.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (numEmpleado.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.verificarCredencialesAdministrador(numEmpleado, password);

        if (cursor != null && cursor.moveToFirst()) {
            // Autenticación exitosa
            int idAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(AdministradorBD.COL_ID));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(AdministradorBD.COL_NOMBRE));

            Intent intent = new Intent(this, BienvenidaActivity.class);
            intent.putExtra("tipoUsuario", "administrador");
            intent.putExtra("idUsuario", idAdmin);
            intent.putExtra("nombreUsuario", nombre);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
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