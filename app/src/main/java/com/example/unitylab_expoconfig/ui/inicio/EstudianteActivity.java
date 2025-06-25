package com.example.unitylab_expoconfig.ui.inicio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.AlumnoBD;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EquipoBD;
import com.example.unitylab_expoconfig.SQLite.EvaluacionBD;
import com.example.unitylab_expoconfig.ui.agenda.AgendaActivity;
import com.example.unitylab_expoconfig.ui.cartel.GenerarCartelActivity;
import com.example.unitylab_expoconfig.ui.constancia.GenerarConstanciaActivity;
import com.example.unitylab_expoconfig.ui.equipos.UnirseAEquipoActivity;
import com.example.unitylab_expoconfig.ui.evaluacion.EvaluarEquiposActivity;
import com.example.unitylab_expoconfig.ui.equipos.IntegrantesAdapter;

import java.util.ArrayList;

public class EstudianteActivity extends AppCompatActivity {
    private static final String TAG = "EstudianteActivity";

    private int idUsuarioActual;
    private String tipoUsuario;
    private String nombreUsuario;
    private int boletaUsuario; // Cambiar a int para que coincida con la BD
    private int idEquipoActual = -1;
    private DbmsSQLiteHelper dbHelper;

    // Views principales
    private TextView tvBienvenidaAlumno, tvBoleta, tvMensajeEstado;
    private TextView tvEstadoParticipacion, tvNombreEquipo, tvProyectoEquipo;
    private TextView tvEvaluacionesRecibidas, tvPromedioCalificacion, tvHorarioAsignado;
    private ImageView ivEstadoParticipacion;
    private CardView cardEstadoEquipo, cardMiEquipo;
    private RecyclerView recyclerIntegrantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_estudiante);

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Obtener datos del intent
        obtenerDatosIntent();

        // Inicializar views
        inicializarViews();

        // Configurar elementos de la UI
        configurarBienvenida();
        configurarCards();
        configurarAccionesRapidas();
        configurarBotonCerrarSesion();

        // Cargar estado del equipo
        cargarEstadoEquipo();


    }

    private void obtenerDatosIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            idUsuarioActual = intent.getIntExtra("idUsuario", -1);
            tipoUsuario = intent.getStringExtra("tipoUsuario");
            nombreUsuario = intent.getStringExtra("nombreUsuario");

            // La boleta puede venir como String o int, manejar ambos casos
            String boletaStr = intent.getStringExtra("boleta");
            if (boletaStr != null) {
                try {
                    boletaUsuario = Integer.parseInt(boletaStr);
                } catch (NumberFormatException e) {
                    boletaUsuario = idUsuarioActual; // Fallback
                }
            } else {
                boletaUsuario = idUsuarioActual;
            }
        }
    }

    private void inicializarViews() {
        tvBienvenidaAlumno = findViewById(R.id.tvBienvenidaAlumno);
        tvBoleta = findViewById(R.id.tvBoleta);
        tvMensajeEstado = findViewById(R.id.tvMensajeEstado);
        tvEstadoParticipacion = findViewById(R.id.tvEstadoParticipacion);
        tvNombreEquipo = findViewById(R.id.tvNombreEquipo);
        tvProyectoEquipo = findViewById(R.id.tvProyectoEquipo);
        tvEvaluacionesRecibidas = findViewById(R.id.tvEvaluacionesRecibidas);
        tvPromedioCalificacion = findViewById(R.id.tvPromedioCalificacion);
        tvHorarioAsignado = findViewById(R.id.tvHorarioAsignado);

        ivEstadoParticipacion = findViewById(R.id.ivEstadoParticipacion);

        cardEstadoEquipo = findViewById(R.id.cardEstadoEquipo);
        cardMiEquipo = findViewById(R.id.cardMiEquipo);

        recyclerIntegrantes = findViewById(R.id.recyclerIntegrantes);
        if (recyclerIntegrantes != null) {
            recyclerIntegrantes.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void configurarBienvenida() {
        if (tvBienvenidaAlumno != null && nombreUsuario != null) {
            String mensajeBienvenida = "¡Bienvenido, " + nombreUsuario + "!";
            tvBienvenidaAlumno.setText(mensajeBienvenida);
        }

        if (tvBoleta != null) {
            tvBoleta.setText("Boleta: " + boletaUsuario);
        }
    }

    private void configurarCards() {
        CardView cardRegistrarEquipo = findViewById(R.id.cardRegistrarEquipo);
        CardView cardGenerarCartel = findViewById(R.id.cardGenerarCartel);
        CardView cardConstancia = findViewById(R.id.cardConstancia);
        CardView cardEvaluarEquipos = findViewById(R.id.cardEvaluarEquipos);

        if (cardRegistrarEquipo != null) {
            cardRegistrarEquipo.setOnClickListener(v -> {
                Intent intent = new Intent(this, UnirseAEquipoActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("boleta", boletaUsuario);
                intent.putExtra("nombreUsuario", nombreUsuario);
                startActivity(intent);
            });
        }

        if (cardGenerarCartel != null) {
            cardGenerarCartel.setOnClickListener(v -> {
                if (idEquipoActual != -1) {
                    Intent intent = new Intent(this, GenerarCartelActivity.class);
                    intent.putExtra("idEquipo", idEquipoActual);
                    intent.putExtra("boleta", boletaUsuario);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Primero debes registrarte en un equipo", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardConstancia != null) {
            cardConstancia.setOnClickListener(v -> {
                if (idEquipoActual != -1) {
                    Intent intent = new Intent(this, GenerarConstanciaActivity.class);
                    intent.putExtra("idEquipo", idEquipoActual);
                    intent.putExtra("boleta", boletaUsuario);
                    intent.putExtra("nombreUsuario", nombreUsuario);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Primero debes registrarte en un equipo", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardEvaluarEquipos != null) {
            cardEvaluarEquipos.setOnClickListener(v -> {
                Intent intent = new Intent(this, EvaluarEquiposActivity.class);
                intent.putExtra("idUsuario", boletaUsuario);
                intent.putExtra("tipoEvaluador", "alumno");
                startActivity(intent);
            });
        }

        // Configurar botón editar equipo
//        findViewById(R.id.btnEditarEquipo).setOnClickListener(v -> {
//            if (idEquipoActual != -1) {
//                // Abrir actividad para editar equipo
//                Intent intent = new Intent(this, RegistrarEquipoActivity.class);
//                intent.putExtra("idEquipo", idEquipoActual);
//                intent.putExtra("modo", "editar");
//                startActivity(intent);
//            }
//        });
    }

    private void configurarAccionesRapidas() {
        View linearVerAgenda = findViewById(R.id.linearVerAgenda);
        View linearProyectosDisponibles = findViewById(R.id.linearProyectosDisponibles);

        if (linearVerAgenda != null) {
            linearVerAgenda.setOnClickListener(v -> {
                Intent intent = new Intent(this, AgendaActivity.class);
                startActivity(intent);
            });
        }

        if (linearProyectosDisponibles != null) {
            linearProyectosDisponibles.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("mostrarProyectos", true);
                startActivity(intent);
            });
        }
    }

    private void cargarEstadoEquipo() {
        try {
            // Verificar si el estudiante tiene equipo
            Cursor cursorAlumno = dbHelper.obtenerAlumnoPorBoleta(boletaUsuario);

            if (cursorAlumno != null && cursorAlumno.moveToFirst()) {
                int idEquipoIndex = cursorAlumno.getColumnIndex(AlumnoBD.COL_ID_EQUIPO);
                String estadoRegistro = cursorAlumno.getString(
                        cursorAlumno.getColumnIndexOrThrow(AlumnoBD.COL_ESTADO_REGISTRO));

                if (!cursorAlumno.isNull(idEquipoIndex)) {
                    idEquipoActual = cursorAlumno.getInt(idEquipoIndex);
                    Log.d(TAG, "idEquipoActual: " + idEquipoActual);
                    mostrarEstadoConEquipo();
                    cargarDatosEquipo();
                } else {
                    mostrarEstadoSinEquipo();
                }
                cursorAlumno.close();
            } else {
                mostrarEstadoSinEquipo();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar estado del equipo: " + e.getMessage());
            mostrarEstadoSinEquipo();
        }
    }

    private void mostrarEstadoSinEquipo() {
        if (cardMiEquipo != null) cardMiEquipo.setVisibility(View.GONE);
        if (cardEstadoEquipo != null) cardEstadoEquipo.setVisibility(View.VISIBLE);

        if (tvMensajeEstado != null) {
            tvMensajeEstado.setText("Aún no te has registrado en ningún proyecto. ¡Busca un proyecto disponible y únete con tu equipo!");
        }

        if (ivEstadoParticipacion != null) {
            ivEstadoParticipacion.setImageResource(R.drawable.ic_refresh);
        }

        if (tvEstadoParticipacion != null) {
            tvEstadoParticipacion.setText("Sin equipo");
        }
    }

    private void mostrarEstadoConEquipo() {
        if (cardMiEquipo != null) cardMiEquipo.setVisibility(View.VISIBLE);
        if (cardEstadoEquipo != null) cardEstadoEquipo.setVisibility(View.GONE);

        if (ivEstadoParticipacion != null) {
            ivEstadoParticipacion.setImageResource(R.drawable.ic_check_circle);
        }

        if (tvEstadoParticipacion != null) {
            tvEstadoParticipacion.setText("Con equipo");
        }
    }

    private void cargarDatosEquipo() {
        try {
            Cursor cursorEquipo = dbHelper.obtenerEquipoPorId(idEquipoActual);
            if (cursorEquipo != null && cursorEquipo.moveToFirst()) {
                // Información básica del equipo
                String nombreEquipo = cursorEquipo.getString(
                        cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE));
                String nombreProyecto = cursorEquipo.getString(
                        cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE_PROYECTO));

                if (tvNombreEquipo != null) {
                    tvNombreEquipo.setText(nombreEquipo);
                }

                if (tvProyectoEquipo != null) {
                    tvProyectoEquipo.setText("Proyecto: " + nombreProyecto);
                }

                // Estadísticas del equipo
                int cantEval = cursorEquipo.getInt(
                        cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_CANT_EVAL));
                double promedio = cursorEquipo.getDouble(
                        cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_PROMEDIO));

                if (tvEvaluacionesRecibidas != null) {
                    tvEvaluacionesRecibidas.setText(String.valueOf(cantEval));
                }

                if (tvPromedioCalificacion != null) {
                    tvPromedioCalificacion.setText(String.format("%.1f", promedio));
                }

                // Horario (por ahora placeholder)
                if (tvHorarioAsignado != null) {
                    tvHorarioAsignado.setText("Por asignar");
                }

                cursorEquipo.close();

                // Cargar integrantes del equipo
                cargarIntegrantesEquipo();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar datos del equipo: " + e.getMessage());
        }
    }

    private void cargarIntegrantesEquipo() {
        try {
            Cursor cursorIntegrantes = dbHelper.obtenerAlumnosPorEquipo(idEquipoActual);
            ArrayList<String> integrantes = new ArrayList<>();

            if (cursorIntegrantes != null && cursorIntegrantes.moveToFirst()) {
                do {
                    String nombre = cursorIntegrantes.getString(
                            cursorIntegrantes.getColumnIndexOrThrow(AlumnoBD.COL_NOMBRE));
                    String apellido1 = cursorIntegrantes.getString(
                            cursorIntegrantes.getColumnIndexOrThrow(AlumnoBD.COL_APELLIDO1));

                    String nombreCompleto = nombre;
                    if (apellido1 != null && !apellido1.isEmpty()) {
                        nombreCompleto += " " + apellido1;
                    }
                    integrantes.add(nombreCompleto);
                } while (cursorIntegrantes.moveToNext());

                cursorIntegrantes.close();
            }

            // Aquí podrías configurar un adapter para el RecyclerView de integrantes
            // Por simplicidad, crearemos un adapter básico
            if (recyclerIntegrantes != null && !integrantes.isEmpty()) {
              //  IntegrantesAdapter adapter = new IntegrantesAdapter(integrantes);
               // recyclerIntegrantes.setAdapter(adapter);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar integrantes: " + e.getMessage());
        }
    }

    private void configurarBotonCerrarSesion() {
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionAlumno);
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar estado del equipo cada vez que la actividad se reanude
        cargarEstadoEquipo();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }
}