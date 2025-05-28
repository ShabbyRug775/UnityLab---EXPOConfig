package com.example.unitylab_expoconfig.ui.profesores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;

public class RegistroProfesorActivity extends AppCompatActivity {

    private EditText editTextNombre, editTextApellidos, editTextCorreo,
            editTextNumEmpleado, editTextDepartamento, editTextPassword;
    private DbmsSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_profesor);

        dbHelper = new DbmsSQLiteHelper(this);

        // Inicializar vistas
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellidos = findViewById(R.id.editTextApellidos);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextNumEmpleado = findViewById(R.id.editTextNumEmpleado);
        editTextDepartamento = findViewById(R.id.editTextDepartamento);
        editTextPassword = findViewById(R.id.editTextPassword);

        Button buttonRegistrar = findViewById(R.id.buttonRegistrar);
        TextView textViewYaRegistrado = findViewById(R.id.textViewYaRegistrado);

        buttonRegistrar.setOnClickListener(v -> registrarProfesor());
        textViewYaRegistrado.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginProfesor.class));
            finish();
        });
    }

    private void registrarProfesor() {
        // Validar campos
        if (editTextNombre.getText().toString().isEmpty() ||
                editTextApellidos.getText().toString().isEmpty() ||
                editTextCorreo.getText().toString().isEmpty() ||
                editTextNumEmpleado.getText().toString().isEmpty() ||
                editTextDepartamento.getText().toString().isEmpty() ||
                editTextPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir departamento a ID (aquí deberías tener una tabla de departamentos)
        int idDepto = 1; // Por defecto, en una app real esto vendría de un spinner o similar

        // Insertar en la base de datos
        long id = dbHelper.insertarProfesor(
                editTextNombre.getText().toString(),
                editTextApellidos.getText().toString(),
                editTextCorreo.getText().toString(),
                Integer.parseInt(editTextNumEmpleado.getText().toString()),  // Número de empleado
                idDepto,
                editTextPassword.getText().toString()  // Password
        );

        if (id != -1) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginProfesor.class));
            finish();
        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.cerrarConexion();
        super.onDestroy();
    }
}