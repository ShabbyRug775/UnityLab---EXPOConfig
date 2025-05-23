package com.example.unitylab_expoconfig.ui.estudiantes;

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

public class RegistroEstudianteActivity extends AppCompatActivity {

    private EditText editTextNombre, editTextApellidos, editTextCorreo, editTextBoleta,
            editTextGrupo, editTextSemestre, editTextCarrera, editTextPassword;
    private DbmsSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_estudiante);

        dbHelper = new DbmsSQLiteHelper(this);

        // Inicializar vistas
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellidos = findViewById(R.id.editTextApellidos);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextBoleta = findViewById(R.id.editTextBoleta);
        editTextGrupo = findViewById(R.id.editTextGrupo);
        editTextSemestre = findViewById(R.id.editTextSemestre);
        editTextCarrera = findViewById(R.id.editTextCarrera);
        editTextPassword = findViewById(R.id.editTextPassword);

        Button buttonRegistrar = findViewById(R.id.buttonRegistrar);
        TextView textViewYaRegistrado = findViewById(R.id.textViewYaRegistrado);

        buttonRegistrar.setOnClickListener(v -> registrarEstudiante());
        textViewYaRegistrado.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginEstudiante.class));
            finish();
        });
    }

    private void registrarEstudiante() {
        // Validar campos
        if (editTextNombre.getText().toString().isEmpty() ||
                editTextApellidos.getText().toString().isEmpty() ||
                editTextCorreo.getText().toString().isEmpty() ||
                editTextBoleta.getText().toString().isEmpty() ||
                editTextPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insertar en la base de datos
        long id = dbHelper.insertarEstudiante(
                editTextNombre.getText().toString(),
                editTextApellidos.getText().toString(),
                editTextCorreo.getText().toString(),
                editTextGrupo.getText().toString(),
                editTextSemestre.getText().toString(),
                editTextCarrera.getText().toString()
        );

        if (id != -1) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginEstudiante.class));
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