package com.example.unitylab_expoconfig.ui.proyectos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.ProfesorBD;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CrearProyectoActivity extends AppCompatActivity {
    private static final String TAG = "CrearProyecto"; // Para logs

    private EditText editNombreProyecto, editDescripcion, editNombreEquipo, editMateria,
            editGrupo, editSemestre, editCarrera, editHerramientas,
            editArquitectura, editFunciones, editUrlCartel;
    private Spinner spinnerProfesor, spinnerEstado;
    private Button btnCrearProyecto, btnCancelar;
    private DbmsSQLiteHelper dbHelper;
    private int idUsuarioActual;
    private String tipoUsuario;
    private List<Integer> profesorIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_proyecto);

        Log.d(TAG, "=== INICIANDO CrearProyectoActivity ===");

        // Obtener datos del usuario actual
        Intent intent = getIntent();
        idUsuarioActual = intent.getIntExtra("idUsuario", -1);
        tipoUsuario = intent.getStringExtra("tipoUsuario");

        Log.d(TAG, "Usuario actual: ID=" + idUsuarioActual + ", Tipo=" + tipoUsuario);

        dbHelper = new DbmsSQLiteHelper(this);
        profesorIds = new ArrayList<>();

        inicializarVistas();
        configurarSpinners();
        configurarBotones();

        // Si es estudiante, prellenar algunos campos con sus datos
        if ("estudiante".equals(tipoUsuario)) {
            prellenarDatosEstudiante();
        }
    }

    private void inicializarVistas() {
        Log.d(TAG, "Inicializando vistas...");
        try {
            editNombreProyecto = findViewById(R.id.editNombreProyecto);
            editDescripcion = findViewById(R.id.editDescripcion);
            editNombreEquipo = findViewById(R.id.editNombreEquipo);
            editMateria = findViewById(R.id.editMateria);
            editGrupo = findViewById(R.id.editGrupo);
            editSemestre = findViewById(R.id.editSemestre);
            editCarrera = findViewById(R.id.editCarrera);
            editHerramientas = findViewById(R.id.editHerramientas);
            editArquitectura = findViewById(R.id.editArquitectura);
            editFunciones = findViewById(R.id.editFunciones);
            editUrlCartel = findViewById(R.id.editUrlCartel);

            spinnerProfesor = findViewById(R.id.spinnerProfesor);
            spinnerEstado = findViewById(R.id.spinnerEstado);

            btnCrearProyecto = findViewById(R.id.btnCrearProyecto);
            btnCancelar = findViewById(R.id.btnCancelar);

            Log.d(TAG, "Vistas inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar vistas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarSpinners() {
        Log.d(TAG, "Configurando spinners...");
        try {
            // Configurar spinner de estado
            String[] estados = {"ACTIVO", "EN_DESARROLLO", "COMPLETADO", "PAUSADO", "CANCELADO"};
            ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, estados);
            estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEstado.setAdapter(estadoAdapter);
            Log.d(TAG, "Spinner de estado configurado");

            // Configurar spinner de profesores
            cargarProfesores();
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar spinners: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarProfesores() {
        Log.d(TAG, "Cargando profesores...");
        try {
            Cursor cursor = dbHelper.obtenerTodosProfesores();
            List<String> profesorNombres = new ArrayList<>();
            profesorIds.clear();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProfesorBD.COL_ID));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ProfesorBD.COL_NOMBRE));
                    String apellidos = cursor.getString(cursor.getColumnIndexOrThrow(ProfesorBD.COL_APELLIDOS));

                    profesorIds.add(id);
                    profesorNombres.add(nombre + " " + apellidos);
                    Log.d(TAG, "Profesor cargado: ID=" + id + ", Nombre=" + nombre + " " + apellidos);
                } while (cursor.moveToNext());
                cursor.close();
                Log.d(TAG, "Total profesores cargados: " + profesorNombres.size());
            } else {
                Log.w(TAG, "No se encontraron profesores en la base de datos");
                Toast.makeText(this, "No hay profesores disponibles. Registra un profesor primero.", Toast.LENGTH_LONG).show();
            }

            ArrayAdapter<String> profesorAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, profesorNombres);
            profesorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProfesor.setAdapter(profesorAdapter);

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar profesores: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar profesores: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void prellenarDatosEstudiante() {
        Log.d(TAG, "Prellenando datos del estudiante...");
        try {
            // Obtener datos del estudiante actual para prellenar algunos campos
            Cursor cursor = dbHelper.obtenerEstudiantePorId(idUsuarioActual);
            if (cursor != null && cursor.moveToFirst()) {
                String grupo = cursor.getString(cursor.getColumnIndexOrThrow("grupo"));
                String semestre = cursor.getString(cursor.getColumnIndexOrThrow("semestre"));
                String carrera = cursor.getString(cursor.getColumnIndexOrThrow("carrera"));

                editGrupo.setText(grupo);
                editSemestre.setText(semestre);
                editCarrera.setText(carrera);

                Log.d(TAG, "Datos prellenados: Grupo=" + grupo + ", Semestre=" + semestre + ", Carrera=" + carrera);
                cursor.close();
            } else {
                Log.w(TAG, "No se pudieron obtener datos del estudiante con ID: " + idUsuarioActual);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al prellenar datos del estudiante: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarBotones() {
        btnCrearProyecto.setOnClickListener(v -> guardarProyecto());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void guardarProyecto() {
        Log.d(TAG, "=== INICIANDO GUARDADO DE PROYECTO ===");

        try {
            if (!validarCampos()) {
                Log.w(TAG, "Validación de campos falló");
                return;
            }

            // Obtener datos del formulario
            String nombreProyecto = editNombreProyecto.getText().toString().trim();
            String descripcion = editDescripcion.getText().toString().trim();
            String nombreEquipo = editNombreEquipo.getText().toString().trim();
            String materia = editMateria.getText().toString().trim();
            String grupo = editGrupo.getText().toString().trim();
            String semestre = editSemestre.getText().toString().trim();
            String carrera = editCarrera.getText().toString().trim();
            String herramientas = editHerramientas.getText().toString().trim();
            String arquitectura = editArquitectura.getText().toString().trim();
            String funciones = editFunciones.getText().toString().trim();
            String urlCartel = editUrlCartel.getText().toString().trim();
            String estado = spinnerEstado.getSelectedItem().toString();

            Log.d(TAG, "Datos del formulario:");
            Log.d(TAG, "- Nombre: " + nombreProyecto);
            Log.d(TAG, "- Equipo: " + nombreEquipo);
            Log.d(TAG, "- Materia: " + materia);
            Log.d(TAG, "- Estado: " + estado);

            // Verificar spinner de profesor
            if (spinnerProfesor.getSelectedItemPosition() == -1 || profesorIds.isEmpty()) {
                Log.e(TAG, "No hay profesor seleccionado o no hay profesores disponibles");
                Toast.makeText(this, "Error: No hay profesor seleccionado", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener el profesor seleccionado
            int idProfesor = profesorIds.get(spinnerProfesor.getSelectedItemPosition());
            Log.d(TAG, "Profesor seleccionado ID: " + idProfesor);

            // Obtener fecha actual
            String fechaCreacion = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            Log.d(TAG, "Fecha creación: " + fechaCreacion);

            // Determinar el ID del estudiante líder
            int idEstudianteLider;
            if ("estudiante".equals(tipoUsuario)) {
                idEstudianteLider = idUsuarioActual;
                Log.d(TAG, "Estudiante líder: " + idEstudianteLider);
            } else {
                // Si es profesor, por ahora no asignamos estudiante líder
                idEstudianteLider = -1;
                Log.d(TAG, "No se asigna estudiante líder (usuario es profesor)");
            }

            // Intentar insertar en la base de datos
            Log.d(TAG, "Insertando proyecto en la base de datos...");
            long resultado = dbHelper.insertarProyecto(nombreProyecto, descripcion, nombreEquipo,
                    materia, grupo, semestre, carrera, herramientas, arquitectura, funciones,
                    idProfesor, idEstudianteLider, fechaCreacion, estado, urlCartel);

            Log.d(TAG, "Resultado de inserción: " + resultado);

            if (resultado != -1) {
                Log.d(TAG, "Proyecto creado exitosamente con ID: " + resultado);
                Toast.makeText(this, "Proyecto creado exitosamente", Toast.LENGTH_SHORT).show();

                // Regresar a la lista de proyectos
                Intent intent = new Intent(this, ListaProyectosActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("tipoUsuario", tipoUsuario);
                startActivity(intent);
                finish();
            } else {
                Log.e(TAG, "Error: la inserción devolvió -1");
                Toast.makeText(this, "Error al crear el proyecto en la base de datos", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Excepción al guardar proyecto: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarCampos() {
        Log.d(TAG, "Validando campos...");

        if (TextUtils.isEmpty(editNombreProyecto.getText())) {
            Log.w(TAG, "Validación falló: Nombre del proyecto vacío");
            editNombreProyecto.setError("El nombre del proyecto es obligatorio");
            editNombreProyecto.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editNombreEquipo.getText())) {
            Log.w(TAG, "Validación falló: Nombre del equipo vacío");
            editNombreEquipo.setError("El nombre del equipo es obligatorio");
            editNombreEquipo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editMateria.getText())) {
            Log.w(TAG, "Validación falló: Materia vacía");
            editMateria.setError("La materia es obligatoria");
            editMateria.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editGrupo.getText())) {
            Log.w(TAG, "Validación falló: Grupo vacío");
            editGrupo.setError("El grupo es obligatorio");
            editGrupo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editSemestre.getText())) {
            Log.w(TAG, "Validación falló: Semestre vacío");
            editSemestre.setError("El semestre es obligatorio");
            editSemestre.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editCarrera.getText())) {
            Log.w(TAG, "Validación falló: Carrera vacía");
            editCarrera.setError("La carrera es obligatoria");
            editCarrera.requestFocus();
            return false;
        }

        if (spinnerProfesor.getSelectedItemPosition() == -1) {
            Log.w(TAG, "Validación falló: No hay profesor seleccionado");
            Toast.makeText(this, "Debe seleccionar un profesor", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d(TAG, "Validación exitosa");
        return true;
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
        Log.d(TAG, "CrearProyectoActivity destruida");
    }
}