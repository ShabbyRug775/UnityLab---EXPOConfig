package com.example.unitylab_expoconfig.ui.equipos;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.ProcedimientosEquipo;
import com.example.unitylab_expoconfig.SQLite.ProyectoBD;

import java.util.ArrayList;
import java.util.List;

public class RegistrarEquipoActivity extends AppCompatActivity {

    private static final String TAG = "RegistrarEquipoActivity";
    private static final int REQUEST_IMAGE_PICK = 1001;

    private DbmsSQLiteHelper dbHelper;

    // Datos del usuario
    private int idUsuario = -1;
    private int boletaUsuario = -1;
    private String nombreUsuario;
    private String modo = "crear"; // "crear" o "editar"
    private int idEquipoEditar = -1;

    // Views principales
    private EditText editClaveAcceso, editNombreEquipo, editNumIntegrantes, editDescripcionProyecto;
    private TextView tvNombreProyecto, tvProfesorProyecto, tvMateriaProyecto;
    private CardView cardInfoProyecto, cardInfoEquipo, cardDescripcionProyecto, cardHorario;
    private Button btnValidarClave, btnAgregarIntegrante, btnSeleccionarImagen, btnRegistrarEquipo;
    private Spinner spinnerHorario;
    private RecyclerView recyclerIntegrantes;

    // Datos del proyecto validado
    private int idProyectoSeleccionado = -1;
    private String claveProyectoValidada = "";

    // Lista de integrantes
    private List<IntegranteEquipo> listaIntegrantes;
    private IntegrantesAdapter integrantesAdapter;

    // Estados del formulario
    private boolean claveValidada = false;
    private boolean equipoCompleto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_equipo);

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Obtener datos del intent
        obtenerDatosIntent();

        // Configurar toolbar
        configurarToolbar();

        // Inicializar views
        inicializarViews();

        // Configurar lista de integrantes
        configurarRecyclerIntegrantes();

        // Configurar listeners
        configurarListeners();

        // Configurar spinners
        configurarSpinners();

        // Si es modo editar, cargar datos existentes
        if ("editar".equals(modo)) {
            cargarDatosEquipoExistente();
        }
    }

    private void obtenerDatosIntent() {
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", -1);
        boletaUsuario = intent.getIntExtra("boleta", -1);
        nombreUsuario = intent.getStringExtra("nombreUsuario");
        modo = intent.getStringExtra("modo");
        idEquipoEditar = intent.getIntExtra("idEquipo", -1);

        if (modo == null) modo = "crear";
    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("editar".equals(modo) ?
                    "Editar Equipo" : "Registrar Equipo en Proyecto");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void inicializarViews() {
        // Campos de entrada
        editClaveAcceso = findViewById(R.id.editClaveAcceso);
        editNombreEquipo = findViewById(R.id.editNombreEquipo);
        editNumIntegrantes = findViewById(R.id.editNumIntegrantes);
        editDescripcionProyecto = findViewById(R.id.editDescripcionProyecto);

        // TextViews informativos
        tvNombreProyecto = findViewById(R.id.tvNombreProyecto);
        tvProfesorProyecto = findViewById(R.id.tvProfesorProyecto);
        tvMateriaProyecto = findViewById(R.id.tvMateriaProyecto);

        // Cards y contenedores
        cardInfoProyecto = findViewById(R.id.cardInfoProyecto);
        cardInfoEquipo = findViewById(R.id.cardInfoEquipo);
        cardDescripcionProyecto = findViewById(R.id.cardDescripcionProyecto);
        cardHorario = findViewById(R.id.cardHorario);

        // Botones
        btnValidarClave = findViewById(R.id.btnValidarClave);
        btnAgregarIntegrante = findViewById(R.id.btnAgregarIntegrante);
        //btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnRegistrarEquipo = findViewById(R.id.btnRegistrarEquipo);

        // Otros elementos
        spinnerHorario = findViewById(R.id.spinnerHorario);
        recyclerIntegrantes = findViewById(R.id.recyclerIntegrantes);

        // Inicializar lista de integrantes
        listaIntegrantes = new ArrayList<>();

        // Agregar al usuario actual como primer integrante
        if (nombreUsuario != null) {
            IntegranteEquipo usuarioActual = new IntegranteEquipo(boletaUsuario, nombreUsuario, true, true);
            listaIntegrantes.add(usuarioActual);
        }
    }

    private void configurarRecyclerIntegrantes() {
        recyclerIntegrantes.setLayoutManager(new LinearLayoutManager(this));
        integrantesAdapter = new IntegrantesAdapter(listaIntegrantes, this::eliminarIntegrante);
        recyclerIntegrantes.setAdapter(integrantesAdapter);
    }

    private void configurarListeners() {
        // Validar clave de acceso
        btnValidarClave.setOnClickListener(v -> validarClaveAcceso());

        // Agregar integrante
        btnAgregarIntegrante.setOnClickListener(v -> mostrarDialogoAgregarIntegrante());


        // Registrar equipo
        btnRegistrarEquipo.setOnClickListener(v -> registrarEquipo());

        // Cancelar
        findViewById(R.id.btnCancelar).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cancelar registro")
                    .setMessage("¿Estás seguro de que deseas cancelar? Se perderán los datos ingresados.")
                    .setPositiveButton("Sí", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        });

        // Listener para número de integrantes
        editNumIntegrantes.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                actualizarNumeroIntegrantes();
            }
        });
    }

    private void configurarSpinners() {
        // Configurar spinner de horarios
        List<String> horarios = new ArrayList<>();
        horarios.add("Seleccionar horario preferido");
        horarios.add("Mañana (09:00 - 12:00)");
        horarios.add("Tarde (13:00 - 16:00)");
        horarios.add("Noche (17:00 - 20:00)");

        ArrayAdapter<String> adapterHorarios = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, horarios);
        adapterHorarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHorario.setAdapter(adapterHorarios);
    }

    private void validarClaveAcceso() {
        String clave = editClaveAcceso.getText().toString().trim().toUpperCase();

        if (clave.isEmpty()) {
            Toast.makeText(this, "Ingresa la clave de acceso", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Buscar proyecto por clave
            Cursor cursor = dbHelper.obtenerTodosProyectos();
            boolean proyectoEncontrado = false;

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String claveProyecto = cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_CLAVE));

                    if (clave.equals(claveProyecto.toUpperCase())) {
                        proyectoEncontrado = true;
                        Log.e(TAG, "Clave encontrada: " + clave);
                        idProyectoSeleccionado = cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ID_PROYECTO));

                        String nombreProyecto = cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_NOMBRE));
                        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_DESCRIPCION));
                        Cursor Ncursor = dbHelper.obtenerProfesorPorNumeroEmpleado(cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_NUMERO_EMPLEADO_PROFESOR)));
                        Ncursor.moveToFirst();
                        Log.e(TAG, "Cursor: " + Ncursor);
                        Log.e(TAG, "Columnas: " + Ncursor.getColumnCount());
                        Log.e(TAG, "Nombre: " + Ncursor.getColumnName(Ncursor.getColumnIndexOrThrow("Nombre")));
                        Log.e(TAG, "Apellido1: " + Ncursor.getColumnName(Ncursor.getColumnIndexOrThrow("Apellido1")));
                        Log.e(TAG, "Apellido2: " + Ncursor.getColumnName(Ncursor.getColumnIndexOrThrow("Apellido2")));

                        String nombreProfesor = Ncursor.getString(Ncursor.getColumnIndexOrThrow("Nombre"));
                        Log.e(TAG, "Nombre del profesor: " + nombreProfesor);
                        // Mostrar información del proyecto
                        tvNombreProyecto.setText(nombreProyecto);
                        tvProfesorProyecto.setText( nombreProfesor);
                        tvMateriaProyecto.setText("Descripción: " + descripcion);

                        cardInfoProyecto.setVisibility(View.VISIBLE);
                        habilitarSiguientesPasos();

                        claveValidada = true;
                        claveProyectoValidada = clave;

                        Toast.makeText(this, "¡Proyecto encontrado!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }

            if (!proyectoEncontrado) {
                Toast.makeText(this, "Clave de acceso incorrecta", Toast.LENGTH_SHORT).show();
                cardInfoProyecto.setVisibility(View.GONE);
                deshabilitarSiguientesPasos();
                claveValidada = false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al validar clave: " + e.getMessage());
            Toast.makeText(this, "Error al validar clave", Toast.LENGTH_SHORT).show();
        }
    }

    private void habilitarSiguientesPasos() {
        cardInfoEquipo.setAlpha(1.0f);
        cardDescripcionProyecto.setAlpha(1.0f);
        cardHorario.setAlpha(1.0f);

        editNombreEquipo.setEnabled(true);
        editNumIntegrantes.setEnabled(true);
        editDescripcionProyecto.setEnabled(true);
        btnAgregarIntegrante.setEnabled(true);
        //btnSeleccionarImagen.setEnabled(true);
        spinnerHorario.setEnabled(true);

        verificarCompletitudFormulario();
    }

    private void deshabilitarSiguientesPasos() {
        cardInfoEquipo.setAlpha(0.5f);
        cardDescripcionProyecto.setAlpha(0.5f);
        cardHorario.setAlpha(0.5f);

        editNombreEquipo.setEnabled(false);
        editNumIntegrantes.setEnabled(false);
        editDescripcionProyecto.setEnabled(false);
        btnAgregarIntegrante.setEnabled(false);
        //btnSeleccionarImagen.setEnabled(false);
        spinnerHorario.setEnabled(false);
        btnRegistrarEquipo.setEnabled(false);
    }

    private void mostrarDialogoAgregarIntegrante() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_integrante, null);

        EditText editBoleta = dialogView.findViewById(R.id.editBoleta);
        EditText editNombre = dialogView.findViewById(R.id.editNombre);
        EditText editApellido1 = dialogView.findViewById(R.id.editApellido1);
        EditText editApellido2 = dialogView.findViewById(R.id.editApellido2);
        EditText editCorreo = dialogView.findViewById(R.id.editCorreo);
        EditText editCarrera = dialogView.findViewById(R.id.editCarrera);
        EditText editSemestre = dialogView.findViewById(R.id.editSemestre);
        EditText editGrupo = dialogView.findViewById(R.id.editGrupo);
        EditText editTurno = dialogView.findViewById(R.id.editTurno);

        Button btnBuscarExistente = dialogView.findViewById(R.id.btnBuscarExistente);
        Button btnAgregar = dialogView.findViewById(R.id.btnAgregar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("Agregar Integrante")
                .create();

        // Buscar alumno existente
        btnBuscarExistente.setOnClickListener(v -> {
            String boletaStr = editBoleta.getText().toString().trim();
            if (!boletaStr.isEmpty()) {
                try {
                    int boleta = Integer.parseInt(boletaStr);
                    Cursor cursor = dbHelper.obtenerAlumnoPorBoleta(boleta);

                    if (cursor != null && cursor.moveToFirst()) {
                        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                        String apellido1 = cursor.getString(cursor.getColumnIndexOrThrow("Apellido1"));
                        String apellido2 = cursor.getString(cursor.getColumnIndexOrThrow("Apellido2"));
                        String correo = cursor.getString(cursor.getColumnIndexOrThrow("Correo"));
                        String carrera = cursor.getString(cursor.getColumnIndexOrThrow("Carrera"));
                        String semestre = cursor.getString(cursor.getColumnIndexOrThrow("Semestre"));
                        String grupo = cursor.getString(cursor.getColumnIndexOrThrow("Grupo"));
                        String turno = cursor.getString(cursor.getColumnIndexOrThrow("Turno"));

                        editNombre.setText(nombre);
                        editApellido1.setText(apellido1 != null ? apellido1 : "");
                        editApellido2.setText(apellido2 != null ? apellido2 : "");
                        editCorreo.setText(correo != null ? correo : "");
                        editCarrera.setText(carrera != null ? carrera : "");
                        editSemestre.setText(semestre != null ? semestre : "");
                        editGrupo.setText(grupo != null ? grupo : "");
                        editTurno.setText(turno != null ? turno : "");

                        cursor.close();
                        Toast.makeText(this, "Datos cargados", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Alumno no encontrado", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Boleta inválida", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Agregar integrante
        btnAgregar.setOnClickListener(v -> {
            String boletaStr = editBoleta.getText().toString().trim();
            String nombre = editNombre.getText().toString().trim();

            if (boletaStr.isEmpty() || nombre.isEmpty()) {
                Toast.makeText(this, "Boleta y nombre son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int boleta = Integer.parseInt(boletaStr);

                // Verificar que no esté ya en la lista
                for (IntegranteEquipo integrante : listaIntegrantes) {
                    if (integrante.boleta == boleta) {
                        Toast.makeText(this, "Este integrante ya está en el equipo", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                String apellido1 = editApellido1.getText().toString().trim();
                String apellido2 = editApellido2.getText().toString().trim();
                String nombreCompleto = nombre;
                if (!apellido1.isEmpty()) nombreCompleto += " " + apellido1;
                if (!apellido2.isEmpty()) nombreCompleto += " " + apellido2;

                IntegranteEquipo nuevoIntegrante = new IntegranteEquipo(boleta, nombreCompleto, false, false);
                listaIntegrantes.add(nuevoIntegrante);
                integrantesAdapter.notifyDataSetChanged();

                actualizarNumeroIntegrantes();
                verificarCompletitudFormulario();

                dialog.dismiss();
                Toast.makeText(this, "Integrante agregado", Toast.LENGTH_SHORT).show();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Boleta inválida", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void eliminarIntegrante(int position) {
        IntegranteEquipo integrante = listaIntegrantes.get(position);

        if (integrante.esLider) {
            Toast.makeText(this, "No puedes eliminar al líder del equipo", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar integrante")
                .setMessage("¿Estás seguro de eliminar a " + integrante.nombre + " del equipo?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    listaIntegrantes.remove(position);
                    integrantesAdapter.notifyDataSetChanged();
                    actualizarNumeroIntegrantes();
                    verificarCompletitudFormulario();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarNumeroIntegrantes() {
        int numIntegrantes = listaIntegrantes.size();
        editNumIntegrantes.setText(String.valueOf(numIntegrantes));
    }




    private void verificarCompletitudFormulario() {
        boolean nombreCompleto = !editNombreEquipo.getText().toString().trim().isEmpty();
        Log.e(TAG, "Nombre completo: " + nombreCompleto);
        boolean descripcionCompleta = !editDescripcionProyecto.getText().toString().trim().isEmpty();
        Log.e(TAG, "Descripción completa: " + descripcionCompleta);
        boolean tieneIntegrantes = listaIntegrantes.size() > 0;
        Log.e(TAG, "Tiene integrantes: " + tieneIntegrantes);

        equipoCompleto = claveValidada && nombreCompleto && descripcionCompleta && tieneIntegrantes;
        Log.e(TAG, "Equipo completo: " + equipoCompleto);
        btnRegistrarEquipo.setEnabled(equipoCompleto);
    }

    private void registrarEquipo() {
        if (!equipoCompleto) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String nombreEquipo = editNombreEquipo.getText().toString().trim();
            String descripcion = editDescripcionProyecto.getText().toString().trim();
            String turno = spinnerHorario.getSelectedItem().toString();
            String lugar = "Por asignar"; // Será asignado por administración

            if ("crear".equals(modo)) {
                // Crear nuevo equipo usando procedimientos
                ProcedimientosEquipo.ResultadoProcedimiento resultado =
                        ProcedimientosEquipo.crearEquipo(dbHelper, boletaUsuario, nombreEquipo, descripcion, turno, lugar);

                if (resultado.exito) {
                    // Agregar integrantes adicionales
                    for (IntegranteEquipo integrante : listaIntegrantes) {
                        if (!integrante.esLider && !integrante.yaExiste) {
                            // Registrar nuevo alumno y agregarlo al equipo
                            ProcedimientosEquipo.registrarAlumnoEnEquipo(dbHelper,
                                    integrante.boleta, integrante.nombre, "", "",
                                    "", "Por definir", 1, "A", turno, "123456", boletaUsuario);
                        } else if (!integrante.esLider) {
                            // Agregar alumno existente al equipo
                            ProcedimientosEquipo.agregarIntegrante(dbHelper, boletaUsuario, integrante.boleta);
                        }
                    }
                    //Registrar equipo a proyecto usando procedimientosEquipo

                    ProcedimientosEquipo.ResultadoProcedimiento resultado2 = ProcedimientosEquipo.registrarEquipoAProyecto(dbHelper, resultado.idGenerado, idProyectoSeleccionado);

                    //Si el resultado es exitoso, mostrar mensaje de éxito
                    if (resultado2.exito) {
                        Toast.makeText(this, "Equipo registrado exitosamente", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, resultado2.mensaje, Toast.LENGTH_LONG).show();
                    }


                    //Toast.makeText(this, "Equipo registrado exitosamente", Toast.LENGTH_LONG).show();

                    // Volver a la actividad anterior
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("equipoCreado", true);
                    resultIntent.putExtra("idEquipo", resultado.idGenerado);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, resultado.mensaje, Toast.LENGTH_LONG).show();
                }
            } else {
                // Modo editar - actualizar equipo existente
                Toast.makeText(this, "Función de edición en desarrollo", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al registrar equipo: " + e.getMessage());
            Toast.makeText(this, "Error al registrar equipo", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDatosEquipoExistente() {
        // Implementar carga de datos para modo edición
        if (idEquipoEditar != -1) {
            // Cargar datos del equipo existente
            Toast.makeText(this, "Cargando datos del equipo...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }

    // Clase para representar un integrante del equipo
    public static class IntegranteEquipo {
        public int boleta;
        public String nombre;
        public boolean esLider;
        public boolean yaExiste; // Si ya está registrado en la BD

        public IntegranteEquipo(int boleta, String nombre, boolean esLider, boolean yaExiste) {
            this.boleta = boleta;
            this.nombre = nombre;
            this.esLider = esLider;
            this.yaExiste = yaExiste;
        }
    }
}