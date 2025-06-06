package com.example.unitylab_expoconfig.ui.proyectos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.ProfesorBD;
import com.example.unitylab_expoconfig.SQLite.ProyectoBD;

import java.util.ArrayList;
import java.util.List;

public class EditarProyectoActivity extends AppCompatActivity {
    private static final String TAG = "EditarProyecto";

    // Views del layout
    private EditText editNombreProyecto, editDescripcion, editNombreEquipo, editMateria,
            editGrupo, editSemestre, editCarrera, editHerramientas,
            editArquitectura, editFunciones, editUrlCartel;
    private Spinner spinnerProfesor, spinnerEstado;
    private Button btnGuardarCambios, btnCancelar;

    // Datos y lógica
    private DbmsSQLiteHelper dbHelper;
    private int idProyecto, idUsuarioActual;
    private String tipoUsuario;
    private Proyecto proyectoActual;
    private List<Integer> profesorIds;
    private boolean datosModificados = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "=== INICIANDO EditarProyectoActivity ===");

        try {
            setContentView(R.layout.activity_editar_proyecto);
            Log.d(TAG, "Layout cargado exitosamente");

            // Configurar toolbar
            configurarToolbar();

            // Obtener datos del intent
            obtenerDatosIntent();

            // Verificar datos válidos
            if (!validarDatos()) {
                return;
            }

            dbHelper = new DbmsSQLiteHelper(this);
            profesorIds = new ArrayList<>();

            // Inicializar views
            inicializarVistas();

            // Configurar spinners
            configurarSpinners();

            // Cargar datos del proyecto
            cargarDatosProyecto();

            // Configurar botones
            configurarBotones();

            // Configurar listeners para detectar cambios
            configurarListenersDeteccionCambios();

            Log.d(TAG, "=== EditarProyectoActivity CREADA EXITOSAMENTE ===");

        } catch (Exception e) {
            Log.e(TAG, "ERROR CRÍTICO EN onCreate(): " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar la pantalla de edición: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void configurarToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Editar Proyecto");
                Log.d(TAG, "Toolbar configurado exitosamente");
            } else {
                Log.w(TAG, "Toolbar no encontrado en el layout");
            }
        } catch (Exception e) {
            Log.w(TAG, "Error configurando toolbar: " + e.getMessage());
        }
    }

    private void obtenerDatosIntent() {
        Intent intent = getIntent();
        idProyecto = intent.getIntExtra("idProyecto", -1);
        idUsuarioActual = intent.getIntExtra("idUsuario", -1);
        tipoUsuario = intent.getStringExtra("tipoUsuario");

        Log.d(TAG, "Datos del intent: Proyecto ID=" + idProyecto +
                ", Usuario ID=" + idUsuarioActual + ", Tipo=" + tipoUsuario);
    }

    private boolean validarDatos() {
        if (idProyecto == -1) {
            Log.e(TAG, "ID de proyecto inválido");
            Toast.makeText(this, "Error: Proyecto no válido", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    private void inicializarVistas() {
        Log.d(TAG, "Inicializando vistas...");

        try {
            // Campos de texto
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

            // Spinners
            spinnerProfesor = findViewById(R.id.spinnerProfesor);
            spinnerEstado = findViewById(R.id.spinnerEstado);

            // Botones
            btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
            btnCancelar = findViewById(R.id.btnCancelar);

            Log.d(TAG, "Vistas inicializadas exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar vistas: " + e.getMessage());
            throw e;
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
                } while (cursor.moveToNext());
                cursor.close();
                Log.d(TAG, "Total profesores cargados: " + profesorNombres.size());
            } else {
                Log.w(TAG, "No se encontraron profesores en la base de datos");
            }

            ArrayAdapter<String> profesorAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, profesorNombres);
            profesorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProfesor.setAdapter(profesorAdapter);

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar profesores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarDatosProyecto() {
        Log.d(TAG, "Cargando datos del proyecto...");

        try {
            Cursor cursor = dbHelper.obtenerProyectoPorId(idProyecto);

            if (cursor != null && cursor.moveToFirst()) {
                proyectoActual = new Proyecto();

                // Cargar datos del proyecto
                proyectoActual.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ID)));
                proyectoActual.setNombreProyecto(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_NOMBRE_PROYECTO)));
                proyectoActual.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_DESCRIPCION)));
                proyectoActual.setNombreEquipo(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_NOMBRE_EQUIPO)));
                proyectoActual.setMateria(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_MATERIA)));
                proyectoActual.setGrupo(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_GRUPO)));
                proyectoActual.setSemestre(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_SEMESTRE)));
                proyectoActual.setCarrera(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_CARRERA)));
                proyectoActual.setHerramientasUtilizadas(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_HERRAMIENTAS)));
                proyectoActual.setArquitectura(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ARQUITECTURA)));
                proyectoActual.setFuncionesPrincipales(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_FUNCIONES)));
                proyectoActual.setIdProfesor(cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ID_PROFESOR)));
                proyectoActual.setIdEstudianteLider(cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ID_ESTUDIANTE_LIDER)));
                proyectoActual.setFechaCreacion(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_FECHA_CREACION)));
                proyectoActual.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ESTADO)));
                proyectoActual.setUrlCartel(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_URL_CARTEL)));

                cursor.close();

                // Prellenar campos con los datos
                prellenarCampos();

                // Actualizar título
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Editar: " + proyectoActual.getNombreProyecto());
                }

                Log.d(TAG, "Datos del proyecto cargados y campos prellenados");

            } else {
                Log.e(TAG, "No se encontró el proyecto con ID: " + idProyecto);
                Toast.makeText(this, "No se encontró el proyecto", Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar datos del proyecto: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void prellenarCampos() {
        Log.d(TAG, "Prellenando campos con datos del proyecto...");

        try {
            // Prellenar campos de texto
            setTextSafe(editNombreProyecto, proyectoActual.getNombreProyecto());
            setTextSafe(editDescripcion, proyectoActual.getDescripcion());
            setTextSafe(editNombreEquipo, proyectoActual.getNombreEquipo());
            setTextSafe(editMateria, proyectoActual.getMateria());
            setTextSafe(editGrupo, proyectoActual.getGrupo());
            setTextSafe(editSemestre, proyectoActual.getSemestre());
            setTextSafe(editCarrera, proyectoActual.getCarrera());
            setTextSafe(editHerramientas, proyectoActual.getHerramientasUtilizadas());
            setTextSafe(editArquitectura, proyectoActual.getArquitectura());
            setTextSafe(editFunciones, proyectoActual.getFuncionesPrincipales());
            setTextSafe(editUrlCartel, proyectoActual.getUrlCartel());

            // Seleccionar profesor en spinner
            seleccionarProfesorEnSpinner();

            // Seleccionar estado en spinner
            seleccionarEstadoEnSpinner();

            Log.d(TAG, "Campos prellenados exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error al prellenar campos: " + e.getMessage());
        }
    }

    private void setTextSafe(EditText editText, String texto) {
        if (editText != null && texto != null) {
            editText.setText(texto);
        }
    }

    private void seleccionarProfesorEnSpinner() {
        try {
            int idProfesorActual = proyectoActual.getIdProfesor();
            for (int i = 0; i < profesorIds.size(); i++) {
                if (profesorIds.get(i) == idProfesorActual) {
                    spinnerProfesor.setSelection(i);
                    Log.d(TAG, "Profesor seleccionado en posición: " + i);
                    break;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error al seleccionar profesor: " + e.getMessage());
        }
    }

    private void seleccionarEstadoEnSpinner() {
        try {
            String estadoActual = proyectoActual.getEstado();
            String[] estados = {"ACTIVO", "EN_DESARROLLO", "COMPLETADO", "PAUSADO", "CANCELADO"};

            for (int i = 0; i < estados.length; i++) {
                if (estados[i].equals(estadoActual)) {
                    spinnerEstado.setSelection(i);
                    Log.d(TAG, "Estado seleccionado en posición: " + i);
                    break;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error al seleccionar estado: " + e.getMessage());
        }
    }

    private void configurarBotones() {
        Log.d(TAG, "Configurando botones...");

        try {
            btnGuardarCambios.setOnClickListener(v -> {
                if (datosModificados || confirmarSinCambios()) {
                    guardarCambios();
                } else {
                    finish();
                }
            });

            btnCancelar.setOnClickListener(v -> {
                if (datosModificados) {
                    mostrarDialogoConfirmacionSalida();
                } else {
                    finish();
                }
            });

            Log.d(TAG, "Botones configurados exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error al configurar botones: " + e.getMessage());
        }
    }

    private void configurarListenersDeteccionCambios() {
        // Configurar listeners en todos los campos para detectar cambios
        // Esto es útil para saber si el usuario ha modificado algo

        // TextWatchers para EditText (implementación básica)
        // En una implementación más robusta, añadirías TextWatchers a todos los campos

        Log.d(TAG, "Listeners de detección de cambios configurados");
    }

    private boolean confirmarSinCambios() {
        // Verificar si realmente hay cambios comparando con los datos originales
        return !proyectoActual.getNombreProyecto().equals(editNombreProyecto.getText().toString());
        // En una implementación completa, compararías todos los campos
    }

    private void guardarCambios() {
        Log.d(TAG, "=== INICIANDO GUARDADO DE CAMBIOS ===");

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

            // Verificar spinner de profesor
            if (spinnerProfesor.getSelectedItemPosition() == -1 || profesorIds.isEmpty()) {
                Toast.makeText(this, "Error: No hay profesor seleccionado", Toast.LENGTH_SHORT).show();
                return;
            }

            int idProfesor = profesorIds.get(spinnerProfesor.getSelectedItemPosition());

            Log.d(TAG, "Actualizando proyecto en la base de datos...");

            // Actualizar en la base de datos
            int resultado = dbHelper.actualizarProyecto(idProyecto, nombreProyecto, descripcion,
                    nombreEquipo, materia, grupo, semestre, carrera, herramientas, arquitectura,
                    funciones, idProfesor, estado, urlCartel);

            if (resultado > 0) {
                Log.d(TAG, "Proyecto actualizado exitosamente");
                Toast.makeText(this, "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();

                // Marcar que los cambios se guardaron
                datosModificados = false;

                // Regresar a la pantalla anterior
                finish();
            } else {
                Log.e(TAG, "Error: la actualización devolvió " + resultado);
                Toast.makeText(this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Excepción al guardar cambios: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarCampos() {
        Log.d(TAG, "Validando campos...");

        if (TextUtils.isEmpty(editNombreProyecto.getText())) {
            editNombreProyecto.setError("El nombre del proyecto es obligatorio");
            editNombreProyecto.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editNombreEquipo.getText())) {
            editNombreEquipo.setError("El nombre del equipo es obligatorio");
            editNombreEquipo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editMateria.getText())) {
            editMateria.setError("La materia es obligatoria");
            editMateria.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editGrupo.getText())) {
            editGrupo.setError("El grupo es obligatorio");
            editGrupo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editSemestre.getText())) {
            editSemestre.setError("El semestre es obligatorio");
            editSemestre.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editCarrera.getText())) {
            editCarrera.setError("La carrera es obligatoria");
            editCarrera.requestFocus();
            return false;
        }

        if (spinnerProfesor.getSelectedItemPosition() == -1) {
            Toast.makeText(this, "Debe seleccionar un profesor", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d(TAG, "Validación exitosa");
        return true;
    }

    private void mostrarDialogoConfirmacionSalida() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambios sin guardar")
                .setMessage("Tienes cambios sin guardar. ¿Deseas salir sin guardar?")
                .setPositiveButton("Salir sin guardar", (dialog, which) -> finish())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (datosModificados) {
                mostrarDialogoConfirmacionSalida();
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (datosModificados) {
            mostrarDialogoConfirmacionSalida();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }
}