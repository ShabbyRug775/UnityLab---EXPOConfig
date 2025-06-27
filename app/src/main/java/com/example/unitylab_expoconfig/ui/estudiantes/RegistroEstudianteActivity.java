package com.example.unitylab_expoconfig.ui.estudiantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;

public class RegistroEstudianteActivity extends AppCompatActivity {

    private EditText editNombre, editApellido1,editApellido2, editCorreo, editBoleta,
            editGrupo, editPassword, editConfirmarPassword;
    private AutoCompleteTextView spinnerSemestre, spinnerCarrera, spinnerTurno;
    private CheckBox checkTerminos;
    private DbmsSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_estudiante);

        dbHelper = new DbmsSQLiteHelper(this);

        // Inicializar vistas
        editNombre = findViewById(R.id.editNombre);
        editApellido1 = findViewById(R.id.editApellido1);
        editApellido2 = findViewById(R.id.editApellido2);
        editCorreo = findViewById(R.id.editCorreo);
        editBoleta = findViewById(R.id.editBoleta);
        editGrupo = findViewById(R.id.editGrupo);
        editPassword = findViewById(R.id.editPassword);
        editConfirmarPassword = findViewById(R.id.editConfirmarPassword);
        checkTerminos = findViewById(R.id.checkTerminos);

        // Configurar spinners
        spinnerSemestre = findViewById(R.id.spinnerSemestre);
        spinnerCarrera = findViewById(R.id.spinnerCarrera);
        spinnerTurno = findViewById(R.id.spinnerTurno);

        configurarSpinners();

        Button buttonRegistrar = findViewById(R.id.btnRegistrarse);
        TextView textViewYaRegistrado = findViewById(R.id.tvIniciarSesion);

        buttonRegistrar.setOnClickListener(v -> registrarEstudiante());
        textViewYaRegistrado.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginEstudiante.class));
            finish();
        });
    }

    private void configurarSpinners() {
        // Carreras disponibles
        String[] carreras = new String[]{"ISC", "IIA", "LCD"};
        ArrayAdapter<String> carreraAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_menu_item, carreras);
        spinnerCarrera.setAdapter(carreraAdapter);

        // Semestres disponibles
        String[] semestres = new String[]{"1", "2", "3", "4", "5", "6", "7", "8","9","10","11","12"};
        ArrayAdapter<String> semestreAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_menu_item, semestres);
        spinnerSemestre.setAdapter(semestreAdapter);

        // Turnos disponibles
        String[] turnos = new String[]{"Matutino", "Vespertino", "Mixto"};
        ArrayAdapter<String> turnoAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_menu_item, turnos);
        spinnerTurno.setAdapter(turnoAdapter);
    }

    private void registrarEstudiante() {
        // Validar campos vacíos
        if (editNombre.getText().toString().isEmpty() ||
                editApellido1.getText().toString().isEmpty() ||
                editApellido2.getText().toString().isEmpty() ||
                editCorreo.getText().toString().isEmpty() ||
                editBoleta.getText().toString().isEmpty() ||
                editGrupo.getText().toString().isEmpty() ||
                spinnerSemestre.getText().toString().isEmpty() ||
                spinnerCarrera.getText().toString().isEmpty() ||
                spinnerTurno.getText().toString().isEmpty() ||
                editPassword.getText().toString().isEmpty() ||
                editConfirmarPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar contraseñas coincidan
        if (!editPassword.getText().toString().equals(editConfirmarPassword.getText().toString())) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar términos y condiciones
        if (!checkTerminos.isChecked()) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar formato de correo
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editCorreo.getText().toString()).matches()) {
            Toast.makeText(this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar longitud de contraseña
        if (editPassword.getText().toString().length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insertar en la base de datos
        long id = dbHelper.insertarAlumno(Integer.parseInt(editBoleta.getText().toString()),editNombre.getText().toString(),editApellido1.getText().toString(),editApellido2.getText().toString(),editCorreo.getText().toString(),spinnerCarrera.getText().toString(),Integer.parseInt(spinnerSemestre.getText().toString()),editGrupo.getText().toString(),spinnerTurno.getText().toString(),editPassword.getText().toString(),null,null);

        if (id != -1) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginEstudiante.class));
            finish();
        } else {
            Toast.makeText(this, "Error al registrar. ¿Ya existe un estudiante con esta boleta?", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.cerrarConexion();
        super.onDestroy();
    }
}