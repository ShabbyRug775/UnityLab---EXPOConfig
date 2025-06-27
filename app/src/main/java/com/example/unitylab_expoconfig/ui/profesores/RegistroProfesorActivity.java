package com.example.unitylab_expoconfig.ui.profesores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

public class RegistroProfesorActivity extends AppCompatActivity {

    private EditText editNombre, editApellidos, editCorreo, editNumEmpleado,
            editPassword, editConfirmarPassword;
    private AutoCompleteTextView spinnerDepartamento;
    private CheckBox checkTerminos;
    private DbmsSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_profesor);

        dbHelper = new DbmsSQLiteHelper(this);

        // Inicializar vistas
        editNombre = findViewById(R.id.editNombre);
        editApellidos = findViewById(R.id.editApellidos);
        editCorreo = findViewById(R.id.editCorreo);
        editNumEmpleado = findViewById(R.id.editNumEmpleado);
        editPassword = findViewById(R.id.editPassword);
        editConfirmarPassword = findViewById(R.id.editConfirmarPassword);
        checkTerminos = findViewById(R.id.checkTerminos);

        // Configurar spinner de departamento
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        configurarSpinnerDepartamento();

        Button buttonRegistrar = findViewById(R.id.btnRegistrarse);
        TextView textViewYaRegistrado = findViewById(R.id.tvIniciarSesion);

        buttonRegistrar.setOnClickListener(v -> registrarProfesor());
        textViewYaRegistrado.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginProfesor.class));
            finish();
        });
    }

    private void configurarSpinnerDepartamento() {
        String[] departamentos = new String[]{
                "Ciencias Básicas",
                "Sistemas y Computación",
                "Industrial",
                "Eléctrica y Electrónica",
                "Gestión Empresarial"
        };

        ArrayAdapter<String> departamentoAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_menu_item, departamentos);
        spinnerDepartamento.setAdapter(departamentoAdapter);
    }

    private int obtenerIdDepartamento(String nombreDepartamento) {
        switch(nombreDepartamento) {
            case "Ciencias Básicas": return 1;
            case "Sistemas y Computación": return 2;
            case "Industrial": return 3;
            case "Eléctrica y Electrónica": return 4;
            case "Gestión Empresarial": return 5;
            default: return 0;
        }
    }

    private void registrarProfesor() {
        // Validar campos vacíos
        if (editNombre.getText().toString().isEmpty() ||
                editApellidos.getText().toString().isEmpty() ||
                editCorreo.getText().toString().isEmpty() ||
                editNumEmpleado.getText().toString().isEmpty() ||
                spinnerDepartamento.getText().toString().isEmpty() ||
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

        // Validar número de empleado
        String numEmpleadoStr = editNumEmpleado.getText().toString();
        int numEmpleado;
        try {
            numEmpleado = Integer.parseInt(numEmpleadoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Número de empleado inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el número de empleado ya existe
//        Cursor cursor = dbHelper.buscarProfesorPorNumEmpleado(numEmpleadoStr);
//        if (cursor != null && cursor.getCount() > 0) {
//            Toast.makeText(this, "Ya existe un profesor con este número de empleado", Toast.LENGTH_SHORT).show();
//            cursor.close();
//            return;
//        }

        // Obtener ID de departamento
        int idDepto = obtenerIdDepartamento(spinnerDepartamento.getText().toString());

        // Insertar en la base de datos
//        long id = dbHelper.insertarProfesor(
//                editNombre.getText().toString(),
//                editApellidos.getText().toString(),
//                editCorreo.getText().toString(),
//                numEmpleado,
//                idDepto,
//                editPassword.getText().toString()
//        );

//        if (id != -1) {
//            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, LoginProfesor.class));
//            finish();
//        } else {
//            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
//        }
//
//        if (cursor != null) {
//            cursor.close();
//        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.cerrarConexion();
        super.onDestroy();
    }
}