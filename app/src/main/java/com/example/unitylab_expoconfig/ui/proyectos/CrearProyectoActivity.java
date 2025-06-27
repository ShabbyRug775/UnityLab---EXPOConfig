package com.example.unitylab_expoconfig.ui.proyectos;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EventoBD;
import com.example.unitylab_expoconfig.SQLite.ProfesorBD;
import com.example.unitylab_expoconfig.SQLite.ProyectoBD;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrearProyectoActivity extends AppCompatActivity {
    private static final String TAG = "CrearProyecto"; // Para logs

    // Campos del proyecto
    private EditText editNombreProyecto, editDescripcionProyecto, editProfesorResponsable;

    // Clave de acceso
    private TextView tvClaveGenerada;
    private LinearLayout layoutClaveGenerada;

    // Botones
    private Button btnCrearProyecto, btnCancelar, btnGenerarClave;

    private String claveAcceso = "";
    private DbmsSQLiteHelper dbHelper;
    private SQLiteDatabase db;
    private int idUsuarioActual;
    private String tipoUsuario;
    private String nombreUsuario;
    private Spinner spinnerEvento;
    private ArrayAdapter<String> eventoAdapter;
    private int[] eventoIds = new int[0]; // Para almacenar los IDs de los eventos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_proyecto);
        Log.d(TAG, "=== INICIANDO CrearProyectoActivity ===");

        // Obtener datos del usuario actual
        // Obtener datos del intent
        Intent intent = getIntent();
        if (intent != null) {
            idUsuarioActual = intent.getIntExtra("idUsuario", -1);
            tipoUsuario = intent.getStringExtra("tipoUsuario");
            nombreUsuario = intent.getStringExtra("nombreUsuario");
        }
        Log.d(TAG, "Usuario actual: ID=" + idUsuarioActual + ", Tipo=" + tipoUsuario);

        dbHelper = new DbmsSQLiteHelper(this);
        db = dbHelper.getWritableDatabase();

        inicializarVistas();
        configurarBotones();
        cargarEventos();

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
            spinnerEvento = findViewById(R.id.spinnerEvento);

            // Clave de acceso
            tvClaveGenerada = findViewById(R.id.tvClaveGenerada);
            layoutClaveGenerada = findViewById(R.id.layoutClaveGenerada);

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

    private void prellenarDatosProfesor() {
        Log.d(TAG, "Prellenando datos del profesor...");
        try {
            if ("profesor".equals(tipoUsuario)) {
                // Obtener datos del profesor actual
                Cursor cursor = ProfesorBD.obtenerProfesorPorNumeroEmpleado(db,idUsuarioActual);
                if (cursor != null && cursor.moveToFirst()) {
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ProfesorBD.COL_NOMBRE));
                    String apellido = cursor.getString(cursor.getColumnIndexOrThrow(ProfesorBD.COL_APELLIDO1));

                    editProfesorResponsable.setText(nombre + " " + apellido);
                    Log.d(TAG, "Datos prellenados: Nombre=" + nombre + " " + apellido);
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
        btnCrearProyecto.setOnClickListener(v -> guardarProyecto());
        btnCancelar.setOnClickListener(v -> finish());

        btnGenerarClave.setOnClickListener(v -> {
            claveAcceso = generarCodigoAleatorio(6);
            tvClaveGenerada.setText(claveAcceso);
            layoutClaveGenerada.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Clave generada: " + claveAcceso, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnCopiarClave).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Código proyecto", claveAcceso);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Clave copiada", Toast.LENGTH_SHORT).show();
        });
    }

    private void guardarProyecto() {
        Log.d(TAG, "=== INICIANDO GUARDADO DEL PROYECTO ===");

        try {
            if (!validarCampos()) {
                Log.w(TAG, "Validación de campos falló");
                return;
            }

            if (TextUtils.isEmpty(claveAcceso)) {
                Toast.makeText(this, "Debe generar una clave de acceso", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener datos del formulario
            String nombreProyecto = editNombreProyecto.getText().toString().trim();
            String descripcionProyecto = editDescripcionProyecto.getText().toString().trim();

            // Obtener evento seleccionado (puede ser null)
            Integer idEvento = null;
            if (eventoAdapter != null && eventoAdapter.getCount() > 0 &&
                    spinnerEvento.getSelectedItemPosition() >= 0) {
                int posicion = spinnerEvento.getSelectedItemPosition();
                idEvento = eventoIds[posicion];
            }

            // Obtener fecha actual
            String fechaCreacion = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());

            // Insertar el proyecto
            Log.d(TAG, "Insertando proyecto en la base de datos...");
            long idProyecto = ProyectoBD.insertarProyecto(
                    db,
                    nombreProyecto,
                    descripcionProyecto,
                    claveAcceso,
                    idUsuarioActual,
                    idEvento  // Ahora pasamos el evento seleccionado
            );

            if (idProyecto != -1) {
                Log.d(TAG, "Proyecto creado exitosamente con ID: " + idProyecto);
                Toast.makeText(this, "Proyecto creado exitosamente", Toast.LENGTH_SHORT).show();

                // Regresar a la lista de proyectos
                Intent intent = new Intent(this, ListaProyectosActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("tipoUsuario", tipoUsuario);
                startActivity(intent);
                finish();
            } else {
                Log.e(TAG, "Error: la inserción del proyecto devolvió -1");
                Toast.makeText(this, "Error al crear el proyecto", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Excepción al guardar el proyecto: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarCampos() {
        Log.d(TAG, "Validando campos...");

        // Validar nombre del proyecto
        if (TextUtils.isEmpty(editNombreProyecto.getText())) {
            editNombreProyecto.setError("El nombre del proyecto es obligatorio");
            editNombreProyecto.requestFocus();
            return false;
        }

        // Validar descripción del proyecto (opcional)
        // Si quieres hacerla obligatoria, descomenta estas líneas:
        /*
        if (TextUtils.isEmpty(editDescripcionProyecto.getText())) {
            editDescripcionProyecto.setError("La descripción del proyecto es obligatoria");
            editDescripcionProyecto.requestFocus();
            return false;
        }
        */

        return true;
    }
    private void cargarEventos() {
        Log.d(TAG, "Cargando eventos desde la base de datos...");
        try {
            Cursor cursor = EventoBD.obtenerTodosEventos(db);
            if (cursor != null && cursor.getCount() > 0) {
                eventoIds = new int[cursor.getCount()];
                String[] nombresEventos = new String[cursor.getCount()];

                int i = 0;
                while (cursor.moveToNext()) {
                    eventoIds[i] = cursor.getInt(cursor.getColumnIndexOrThrow(EventoBD.COL_ID_EVENTO));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(EventoBD.COL_NOMBRE));
                    String fecha = cursor.getString(cursor.getColumnIndexOrThrow(EventoBD.COL_FECHA_INICIO));

                    // Formatear nombre con fecha
                    nombresEventos[i] = nombre + " (" + fecha.split(" ")[0] + ")";
                    i++;
                }

                eventoAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, nombresEventos);
                eventoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEvento.setAdapter(eventoAdapter);

                Log.d(TAG, "Eventos cargados exitosamente: " + cursor.getCount());
            } else {
                Log.w(TAG, "No hay eventos registrados");
                spinnerEvento.setEnabled(false);
                Toast.makeText(this, "No hay eventos registrados", Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar eventos: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar eventos", Toast.LENGTH_SHORT).show();
        }
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