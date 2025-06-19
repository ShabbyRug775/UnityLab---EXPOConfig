package com.example.unitylab_expoconfig.ui.proyectos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EquipoDB;
import com.example.unitylab_expoconfig.SQLite.ProfesorBD;
import com.example.unitylab_expoconfig.SQLite.ProyectoBD;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrearProyectoActivity extends AppCompatActivity {
    private static final String TAG = "CrearProyecto"; // Para logs

    // Campos del proyecto
    private EditText editNombreProyecto, editDescripcionProyecto, editProfesorResponsable;

    // Campos del equipo
    private EditText editNombreEquipo, editNumAlumnos, editDescripcionEquipo;
    private Spinner spinnerLugarEquipo;
    private Button btnSubirCartel;
    private ImageView imgPreviewCartel;

    // Otros controles
    private Button btnCrearProyecto, btnCancelar, btnGenerarClave;
    private String cartelPath = ""; // Ruta del archivo del cartel
    private DbmsSQLiteHelper dbHelper;
    private SQLiteDatabase db;
    private int idUsuarioActual;
    private String tipoUsuario;

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
        db = dbHelper.getWritableDatabase();

        inicializarVistas();
        configurarSpinners();
        configurarBotones();

        // Prellenar datos según el tipo de usuario
        if ("profesor".equals(tipoUsuario)) {
            prellenarDatosProfesor();
        }
    }

    private void inicializarVistas() {
        Log.d(TAG, "Inicializando vistas...");
        try {
            // Campos del proyecto
            editNombreProyecto = findViewById(R.id.editNombreProyecto);
            editDescripcionProyecto = findViewById(R.id.editDescripcionProyecto);
            editProfesorResponsable = findViewById(R.id.editProfesorResponsable);

            // Campos del equipo
            editNombreEquipo = findViewById(R.id.editNombreEquipo);
            editNumAlumnos = findViewById(R.id.editNumAlumnos);
            editDescripcionEquipo = findViewById(R.id.editDescripcionEquipo);
            spinnerLugarEquipo = findViewById(R.id.spinnerLugarEquipo);
            btnSubirCartel = findViewById(R.id.btnSubirCartel);
            imgPreviewCartel = findViewById(R.id.imgPreviewCartel);

            // Botones
            btnCrearProyecto = findViewById(R.id.btnCrearProyecto);
            btnCancelar = findViewById(R.id.btnCancelar);
            btnGenerarClave = findViewById(R.id.btnGenerarClave);

            Log.d(TAG, "Vistas inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar vistas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarSpinners() {
        Log.d(TAG, "Configurando spinners...");
        try {
            // Configurar spinner de lugares (stands)
            String[] lugares = new String[50]; // Asumiendo 50 stands disponibles
            for (int i = 0; i < lugares.length; i++) {
                lugares[i] = "Stand " + (i + 1);
            }

            ArrayAdapter<String> lugarAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, lugares);
            lugarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLugarEquipo.setAdapter(lugarAdapter);

            Log.d(TAG, "Spinner de lugares configurado");

        } catch (Exception e) {
            Log.e(TAG, "Error al configurar spinners: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void prellenarDatosProfesor() {
        Log.d(TAG, "Prellenando datos del profesor...");
        try {
            if ("profesor".equals(tipoUsuario)) {
                // Obtener datos del profesor actual
                Cursor cursor = ProfesorBD.obtenerProfesorPorId(db, idUsuarioActual);
                if (cursor != null && cursor.moveToFirst()) {
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ProfesorBD.COL_NOMBRE));
                    String apellidos = cursor.getString(cursor.getColumnIndexOrThrow(ProfesorBD.COL_APELLIDOS));

                    editProfesorResponsable.setText(nombre + " " + apellidos);

                    Log.d(TAG, "Datos prellenados: Nombre=" + nombre + " " + apellidos);
                    cursor.close();
                } else {
                    Log.w(TAG, "No se pudieron obtener datos del profesor con ID: " + idUsuarioActual);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al prellenar datos del profesor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarBotones() {
        btnCrearProyecto.setOnClickListener(v -> guardarProyectoYEquipo());
        btnCancelar.setOnClickListener(v -> finish());

        btnSubirCartel.setOnClickListener(v -> {
            // Implementar lógica para subir imagen del cartel
            // Esto abriría el selector de archivos/galería
            // Por simplicidad, aquí solo simulamos que se subió
            cartelPath = "/path/to/cartel.jpg";
            imgPreviewCartel.setVisibility(View.VISIBLE);
            //imgPreviewCartel.setImageResource(R.drawable.placeholder_image);
            Toast.makeText(this, "Cartel subido (simulado)", Toast.LENGTH_SHORT).show();
        });

        btnGenerarClave.setOnClickListener(v -> {
            // Generar clave aleatoria para el proyecto
            String clave = "PROJ-" + new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()) +
                    "-" + generarCodigoAleatorio(6);
            Toast.makeText(this, "Clave generada: " + clave, Toast.LENGTH_SHORT).show();
        });
    }

    private void guardarProyectoYEquipo() {
        Log.d(TAG, "=== INICIANDO GUARDADO DE PROYECTO Y EQUIPO ===");

        try {
            if (!validarCampos()) {
                Log.w(TAG, "Validación de campos falló");
                return;
            }

            // Obtener datos del formulario para el equipo
            String nombreEquipo = editNombreEquipo.getText().toString().trim();
            int numAlumnos = Integer.parseInt(editNumAlumnos.getText().toString().trim());
            String descripcionEquipo = editDescripcionEquipo.getText().toString().trim();
            int lugar = spinnerLugarEquipo.getSelectedItemPosition() + 1; // +1 para que empiece en 1

            // Obtener datos del formulario para el proyecto
            String nombreProyecto = editNombreProyecto.getText().toString().trim();
            String descripcionProyecto = editDescripcionProyecto.getText().toString().trim();

            // Obtener fecha actual
            String fechaCreacion = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());

            // Primero insertar el equipo
            Log.d(TAG, "Insertando equipo en la base de datos...");
            long idEquipo = EquipoDB.insertarEquipo(
                    db,
                    nombreEquipo,
                    nombreProyecto, // Nombre del proyecto como nombreProyecto en equipo
                    numAlumnos,
                    descripcionEquipo,
                    lugar,
                    cartelPath,
                    0, // cantEval inicial
                    0, // promedio inicial
                    0  // cantVisitas inicial
            );

            if (idEquipo == -1) {
                Log.e(TAG, "Error al insertar equipo");
                Toast.makeText(this, "Error al crear el equipo", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Equipo creado con ID: " + idEquipo);

            // Luego insertar el proyecto con referencia al equipo
            Log.d(TAG, "Insertando proyecto en la base de datos...");
            long idProyecto = ProyectoBD.insertarProyecto(
                    db,
                    nombreProyecto,
                    descripcionProyecto,
                    idUsuarioActual,
                    (int) idEquipo,
                    fechaCreacion
            );

            if (idProyecto != -1) {
                Log.d(TAG, "Proyecto creado exitosamente con ID: " + idProyecto);
                Toast.makeText(this, "Proyecto y equipo creados exitosamente", Toast.LENGTH_SHORT).show();

                // Regresar a la lista de proyectos
                Intent intent = new Intent(this, ListaProyectosActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("tipoUsuario", tipoUsuario);
                startActivity(intent);
                finish();
            } else {
                Log.e(TAG, "Error: la inserción del proyecto devolvió -1");
                // Si falla el proyecto, eliminar el equipo creado para mantener consistencia
                EquipoDB.eliminarEquipo(db, (int) idEquipo);
                Toast.makeText(this, "Error al crear el proyecto", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Excepción al guardar proyecto y equipo: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarCampos() {
        Log.d(TAG, "Validando campos...");

        // Validar campos del proyecto
        if (TextUtils.isEmpty(editNombreProyecto.getText())) {
            editNombreProyecto.setError("El nombre del proyecto es obligatorio");
            editNombreProyecto.requestFocus();
            return false;
        }

        // Validar campos del equipo
        if (TextUtils.isEmpty(editNombreEquipo.getText())) {
            editNombreEquipo.setError("El nombre del equipo es obligatorio");
            editNombreEquipo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editNumAlumnos.getText())) {
            editNumAlumnos.setError("El número de alumnos es obligatorio");
            editNumAlumnos.requestFocus();
            return false;
        }

        try {
            int num = Integer.parseInt(editNumAlumnos.getText().toString());
            if (num <= 0) {
                editNumAlumnos.setError("Debe haber al menos 1 alumno");
                editNumAlumnos.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            editNumAlumnos.setError("Número inválido");
            editNumAlumnos.requestFocus();
            return false;
        }

        return true;
    }

    private String generarCodigoAleatorio(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            int index = (int) (Math.random() * caracteres.length());
            sb.append(caracteres.charAt(index));
        }

        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
        Log.d(TAG, "CrearProyectoActivity destruida");
    }
}