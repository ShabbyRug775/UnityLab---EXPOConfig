package com.example.unitylab_expoconfig.ui.equipos;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.AlumnoBD;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EquipoBD;
import com.example.unitylab_expoconfig.SQLite.ProcedimientosEquipo;

import java.util.ArrayList;
import java.util.List;

public class UnirseAEquipoActivity extends AppCompatActivity {

    private static final String TAG = "UnirseAEquipoActivity";

    private DbmsSQLiteHelper dbHelper;

    // Datos del usuario
    private int idUsuario = -1;
    private int boletaUsuario = -1;
    private String nombreUsuario;

    // Views
    private CardView cardEstadoActual;
    private LinearLayout layoutOpciones, layoutSinEquipos;
    private RecyclerView recyclerEquiposDisponibles;
    private TextView tvMensajeEstado, tvEquipoActual;
    private Button btnCrearEquipo, btnBuscarPorCodigo, btnActualizar;

    // Adaptadores
    private EquiposDisponiblesAdapter equiposAdapter;
    private List<EquipoDisponible> equiposDisponibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unirse_equipo);

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Obtener datos del intent
        obtenerDatosIntent();

        // Configurar toolbar
        configurarToolbar();

        // Inicializar views
        inicializarViews();

        // Configurar listeners
        configurarListeners();

        // Verificar estado actual del alumno
        verificarEstadoActual();

        // Cargar equipos disponibles
        cargarEquiposDisponibles();
    }

    private void obtenerDatosIntent() {
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", -1);
        boletaUsuario = intent.getIntExtra("boleta", -1);
        nombreUsuario = intent.getStringExtra("nombreUsuario");

        if (boletaUsuario == -1 && idUsuario != -1) {
            boletaUsuario = idUsuario; // Fallback
        }
    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Unirse a Equipo");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void inicializarViews() {
        cardEstadoActual = findViewById(R.id.cardEstadoActual);
        layoutOpciones = findViewById(R.id.layoutOpciones);
        layoutSinEquipos = findViewById(R.id.layoutSinEquipos);
        recyclerEquiposDisponibles = findViewById(R.id.recyclerEquiposDisponibles);
        tvMensajeEstado = findViewById(R.id.tvMensajeEstado);
        tvEquipoActual = findViewById(R.id.tvEquipoActual);
        btnCrearEquipo = findViewById(R.id.btnCrearEquipo);
        btnBuscarPorCodigo = findViewById(R.id.btnBuscarPorCodigo);
        btnActualizar = findViewById(R.id.btnActualizar);

        // Configurar RecyclerView
        equiposDisponibles = new ArrayList<>();
        equiposAdapter = new EquiposDisponiblesAdapter(equiposDisponibles, this::solicitarUnirse);
        recyclerEquiposDisponibles.setLayoutManager(new LinearLayoutManager(this));
        recyclerEquiposDisponibles.setAdapter(equiposAdapter);
    }

    private void configurarListeners() {
        btnCrearEquipo.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrarEquipoActivity.class);
            intent.putExtra("idUsuario", idUsuario);
            intent.putExtra("boleta", boletaUsuario);
            intent.putExtra("nombreUsuario", nombreUsuario);
            intent.putExtra("modo", "crear");
            startActivity(intent);
        });

        btnBuscarPorCodigo.setOnClickListener(v -> mostrarDialogoBuscarPorCodigo());

        btnActualizar.setOnClickListener(v -> {
            verificarEstadoActual();
            cargarEquiposDisponibles();
            Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show();
        });
    }

    private void verificarEstadoActual() {
        try {
            Cursor cursor = dbHelper.obtenerAlumnoPorBoleta(boletaUsuario);

            if (cursor != null && cursor.moveToFirst()) {
                String estadoRegistro = cursor.getString(cursor.getColumnIndexOrThrow(AlumnoBD.COL_ESTADO_REGISTRO));
                int idEquipoIndex = cursor.getColumnIndex(AlumnoBD.COL_ID_EQUIPO);

                if (!cursor.isNull(idEquipoIndex) && "ConEquipo".equals(estadoRegistro)) {
                    int idEquipo = cursor.getInt(idEquipoIndex);
                    mostrarEstadoConEquipo(idEquipo);
                } else {
                    mostrarEstadoSinEquipo();
                }
                cursor.close();
            } else {
                mostrarEstadoSinEquipo();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar estado: " + e.getMessage());
            mostrarEstadoSinEquipo();
        }
    }

    private void mostrarEstadoConEquipo(int idEquipo) {
        try {
            Cursor cursorEquipo = dbHelper.obtenerEquipoPorId(idEquipo);
            if (cursorEquipo != null && cursorEquipo.moveToFirst()) {
                String nombreEquipo = cursorEquipo.getString(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE));
                String nombreProyecto = cursorEquipo.getString(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE_PROYECTO));

                cardEstadoActual.setVisibility(View.VISIBLE);
                tvMensajeEstado.setText("Ya formas parte de un equipo");
                tvEquipoActual.setText("Equipo: " + nombreEquipo + "\nProyecto: " + nombreProyecto);
                tvEquipoActual.setVisibility(View.VISIBLE);

                // Ocultar opciones de unirse a otros equipos
                layoutOpciones.setVisibility(View.GONE);
                recyclerEquiposDisponibles.setVisibility(View.GONE);

                cursorEquipo.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al mostrar estado con equipo: " + e.getMessage());
        }
    }

    private void mostrarEstadoSinEquipo() {
        cardEstadoActual.setVisibility(View.VISIBLE);
        tvMensajeEstado.setText("Aún no formas parte de ningún equipo");
        tvEquipoActual.setVisibility(View.GONE);

        layoutOpciones.setVisibility(View.VISIBLE);
        recyclerEquiposDisponibles.setVisibility(View.VISIBLE);
    }

    private void cargarEquiposDisponibles() {
        try {
            equiposDisponibles.clear();

            // Obtener equipos que están en formación y tienen espacio
            Cursor cursor = dbHelper.obtenerEquiposPorEstado("EnFormacion");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idEquipo = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_ID_EQUIPO));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE));
                    String nombreProyecto = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE_PROYECTO));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_DESCRIPCION));
                    int numeroAlumnos = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_NUMERO_ALUMNOS));
                    int boletaLider = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_BOLETA_LIDER));

                    // Obtener información del líder
                    String nombreLider = obtenerNombreLider(boletaLider);

                    EquipoDisponible equipo = new EquipoDisponible(
                            idEquipo, nombre, nombreProyecto, descripcion,
                            numeroAlumnos, nombreLider, boletaLider
                    );
                    equiposDisponibles.add(equipo);

                } while (cursor.moveToNext());
                cursor.close();
            }

            equiposAdapter.notifyDataSetChanged();

            if (equiposDisponibles.isEmpty()) {
                layoutSinEquipos.setVisibility(View.VISIBLE);
                recyclerEquiposDisponibles.setVisibility(View.GONE);
            } else {
                layoutSinEquipos.setVisibility(View.GONE);
                recyclerEquiposDisponibles.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar equipos: " + e.getMessage());
            Toast.makeText(this, "Error al cargar equipos disponibles", Toast.LENGTH_SHORT).show();
        }
    }

    private String obtenerNombreLider(int boletaLider) {
        try {
            Cursor cursor = dbHelper.obtenerAlumnoPorBoleta(boletaLider);
            if (cursor != null && cursor.moveToFirst()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(AlumnoBD.COL_NOMBRE));
                String apellido1 = cursor.getString(cursor.getColumnIndexOrThrow(AlumnoBD.COL_APELLIDO1));
                cursor.close();

                String nombreCompleto = nombre;
                if (apellido1 != null && !apellido1.isEmpty()) {
                    nombreCompleto += " " + apellido1;
                }
                return nombreCompleto;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener nombre del líder: " + e.getMessage());
        }
        return "Líder desconocido";
    }

    private void solicitarUnirse(EquipoDisponible equipo) {
        new AlertDialog.Builder(this)
                .setTitle("Unirse al equipo")
                .setMessage("¿Deseas enviar una solicitud para unirte al equipo \"" + equipo.nombre + "\"?\n\n" +
                        "El líder del equipo (" + equipo.nombreLider + ") recibirá tu solicitud.")
                .setPositiveButton("Sí, unirme", (dialog, which) -> {
                    enviarSolicitudUnirse(equipo);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void enviarSolicitudUnirse(EquipoDisponible equipo) {
        try {
            // Usar el procedimiento para agregar integrante
            ProcedimientosEquipo.ResultadoProcedimiento resultado =
                    ProcedimientosEquipo.agregarIntegrante(dbHelper, equipo.boletaLider, boletaUsuario);

            if (resultado.exito) {
                Toast.makeText(this, "Te has unido al equipo exitosamente", Toast.LENGTH_LONG).show();

                // Actualizar estado
                verificarEstadoActual();
                cargarEquiposDisponibles();

                // Volver a la actividad anterior
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, resultado.mensaje, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al unirse al equipo: " + e.getMessage());
            Toast.makeText(this, "Error al unirse al equipo", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoBuscarPorCodigo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_buscar_equipo, null);

        EditText editCodigo = dialogView.findViewById(R.id.editCodigoEquipo);
        Button btnBuscar = dialogView.findViewById(R.id.btnBuscar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("Buscar equipo por código")
                .create();

        btnBuscar.setOnClickListener(v -> {
            String codigo = editCodigo.getText().toString().trim();
            if (!codigo.isEmpty()) {
                buscarEquipoPorCodigo(codigo);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Ingresa un código", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void buscarEquipoPorCodigo(String codigo) {
        // Por ahora, mostrar mensaje de que la función está en desarrollo
        Toast.makeText(this, "Función de búsqueda por código en desarrollo", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar estado cuando regrese de crear equipo
        verificarEstadoActual();
        cargarEquiposDisponibles();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }

    // Clase para representar un equipo disponible
    public static class EquipoDisponible {
        public final int idEquipo;
        public final String nombre;
        public final String nombreProyecto;
        public final String descripcion;
        public final int numeroAlumnos;
        public final String nombreLider;
        public final int boletaLider;

        public EquipoDisponible(int idEquipo, String nombre, String nombreProyecto,
                                String descripcion, int numeroAlumnos, String nombreLider, int boletaLider) {
            this.idEquipo = idEquipo;
            this.nombre = nombre;
            this.nombreProyecto = nombreProyecto;
            this.descripcion = descripcion;
            this.numeroAlumnos = numeroAlumnos;
            this.nombreLider = nombreLider;
            this.boletaLider = boletaLider;
        }
    }

    // Interfaz para callback del adapter
    public interface OnEquipoSeleccionadoListener {
        void onEquipoSeleccionado(EquipoDisponible equipo);
    }
}