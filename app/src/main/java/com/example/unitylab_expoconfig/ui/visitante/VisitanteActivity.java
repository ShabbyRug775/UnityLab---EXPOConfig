package com.example.unitylab_expoconfig.ui.visitante;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.ui.agenda.AgendaActivity;
import com.example.unitylab_expoconfig.ui.proyectos.ListaProyectosActivity;

import java.util.ArrayList;
import java.util.List;

public class VisitanteActivity extends AppCompatActivity {
    private static final String TAG = "VisitanteActivity";
    private static final String PREFS_NAME = "VisitantePrefs";
    private static final String PREF_VISITANTE_ID = "visitante_id";
    private static final int REFRESH_DELAY = 1500; // Delay para SwipeRefresh

    // Views principales
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerProyectosDestacados;
    private View layoutSinProyectosDestacados;
    private TextView tvTotalProyectosVisitante, tvTotalEquiposVisitante, tvMisEvaluaciones;
    private TextView btnVerTodosProyectos;

    // Base de datos y datos
    private DbmsSQLiteHelper dbHelper;
    private ProyectosDestacadosAdapter adapter;
    private int idVisitante = -1;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_visitante);

        // Inicializar handler para UI
        mainHandler = new Handler(Looper.getMainLooper());

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Obtener o crear ID de visitante
        obtenerOCrearIdVisitante();

        // Inicializar vistas
        initViews();

        // Configurar SwipeRefresh
        setupSwipeRefresh();

        // Cargar datos iniciales
        cargarDatosIniciales();

        // Configurar listeners
        setupListeners();

        // Limpiar visitantes antiguos en background
        limpiarVisitantesAntiguos();
    }

    private void obtenerOCrearIdVisitante() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        idVisitante = prefs.getInt(PREF_VISITANTE_ID, -1);

        // Si no existe ID o el visitante ya no existe en BD, crear uno nuevo
        if (idVisitante == -1 || !dbHelper.existeVisitante(idVisitante)) {
            generarNuevoIdVisitante();
        }

        Log.d(TAG, "ID Visitante: " + idVisitante);
    }

    private void generarNuevoIdVisitante() {
        try {
            // Crear un visitante temporal anónimo
            long resultado = dbHelper.insertarVisitante("Visitante", "Anónimo", "", 1);
            if (resultado != -1) {
                idVisitante = (int) resultado;

                // Guardar en SharedPreferences
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().putInt(PREF_VISITANTE_ID, idVisitante).apply();

                Log.d(TAG, "Nuevo visitante creado con ID: " + idVisitante);
            } else {
                Log.e(TAG, "Error al crear visitante");
                Toast.makeText(this, "Error al inicializar sesión de visitante", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al generar ID de visitante", e);
            Toast.makeText(this, "Error al inicializar sesión", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerProyectosDestacados = findViewById(R.id.recyclerProyectosDestacados);
        layoutSinProyectosDestacados = findViewById(R.id.layoutSinProyectosDestacados);
        tvTotalProyectosVisitante = findViewById(R.id.tvTotalProyectosVisitante);
        tvTotalEquiposVisitante = findViewById(R.id.tvTotalEquiposVisitante);
        tvMisEvaluaciones = findViewById(R.id.tvMisEvaluaciones);
        btnVerTodosProyectos = findViewById(R.id.btnVerTodosProyectosVisitante);

        // Configurar RecyclerView
        recyclerProyectosDestacados.setLayoutManager(new LinearLayoutManager(this));
        recyclerProyectosDestacados.setHasFixedSize(true);
    }

    private void setupSwipeRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(
                    R.color.primary_color,
                    R.color.accent_color,
                    R.color.primary_dark
            );
            swipeRefreshLayout.setOnRefreshListener(this::refrescarDatos);
        }
    }

    private void cargarDatosIniciales() {
        mostrarCargando(true);

        // Cargar en background thread para evitar bloquear UI
        new Thread(() -> {
            try {
                setupStats();
                setupProyectosDestacados();

                // Actualizar UI en main thread
                mainHandler.post(() -> mostrarCargando(false));

            } catch (Exception e) {
                Log.e(TAG, "Error al cargar datos iniciales", e);
                mainHandler.post(() -> {
                    mostrarCargando(false);
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void mostrarCargando(boolean mostrar) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(mostrar);
        }
    }

    private void refrescarDatos() {
        // Simular delay mínimo para mejor UX
        mainHandler.postDelayed(() -> {
            cargarDatosIniciales();
        }, REFRESH_DELAY);
    }

    private void setupStats() {
        try {
            // Obtener estadísticas reales de la base de datos usando los nuevos metodos
            Cursor cursorEstadisticas = dbHelper.obtenerEstadisticasVisitante();

            if (cursorEstadisticas != null && cursorEstadisticas.moveToFirst()) {
                int totalProyectos = cursorEstadisticas.getInt(cursorEstadisticas.getColumnIndexOrThrow("TotalProyectos"));
                int totalEquipos = cursorEstadisticas.getInt(cursorEstadisticas.getColumnIndexOrThrow("TotalEquipos"));

                mainHandler.post(() -> {
                    tvTotalProyectosVisitante.setText(String.valueOf(totalProyectos));
                    tvTotalEquiposVisitante.setText(String.valueOf(totalEquipos));
                });

                cursorEstadisticas.close();
            } else {
                // Fallback a metodo original si el nuevo falla
                setupStatsTradicional();
            }

            // Mis evaluaciones como visitante
            int misEvaluaciones = dbHelper.contarEvaluacionesVisitante(idVisitante);
            mainHandler.post(() -> tvMisEvaluaciones.setText(String.valueOf(misEvaluaciones)));

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar estadísticas", e);
            setupStatsTradicional();
        }
    }

    private void setupStatsTradicional() {
        try {
            // metodo tradicional como fallback
            Cursor cursorProyectos = dbHelper.obtenerTodosProyectos();
            int totalProyectos = cursorProyectos != null ? cursorProyectos.getCount() : 0;
            if (cursorProyectos != null) cursorProyectos.close();

            Cursor cursorEquipos = dbHelper.obtenerTodosEquipos();
            int totalEquipos = cursorEquipos != null ? cursorEquipos.getCount() : 0;
            if (cursorEquipos != null) cursorEquipos.close();

            mainHandler.post(() -> {
                tvTotalProyectosVisitante.setText(String.valueOf(totalProyectos));
                tvTotalEquiposVisitante.setText(String.valueOf(totalEquipos));
            });

        } catch (Exception e) {
            Log.e(TAG, "Error en estadísticas tradicionales", e);
            mainHandler.post(() -> {
                tvTotalProyectosVisitante.setText("0");
                tvTotalEquiposVisitante.setText("0");
                tvMisEvaluaciones.setText("0");
            });
        }
    }

    private void setupProyectosDestacados() {
        try {
            List<ProyectoDestacado> proyectos = getProyectosDestacados();

            mainHandler.post(() -> {
                if (proyectos.isEmpty()) {
                    recyclerProyectosDestacados.setVisibility(View.GONE);
                    layoutSinProyectosDestacados.setVisibility(View.VISIBLE);
                } else {
                    recyclerProyectosDestacados.setVisibility(View.VISIBLE);
                    layoutSinProyectosDestacados.setVisibility(View.GONE);

                    if (adapter == null) {
                        adapter = new ProyectosDestacadosAdapter(proyectos, this::onProyectoClick);
                        recyclerProyectosDestacados.setAdapter(adapter);
                    } else {
                        adapter.updateProyectos(proyectos);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar proyectos destacados", e);
            mainHandler.post(() -> {
                recyclerProyectosDestacados.setVisibility(View.GONE);
                layoutSinProyectosDestacados.setVisibility(View.VISIBLE);
            });
        }
    }

    private List<ProyectoDestacado> getProyectosDestacados() {
        List<ProyectoDestacado> proyectos = new ArrayList<>();

        try {
            // Usar metodo optimizado para obtener proyectos mejor calificados
            Cursor cursor = dbHelper.obtenerProyectosMejorCalificados(5);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idEquipo = cursor.getInt(cursor.getColumnIndexOrThrow("IdEquipo"));
                    String nombreEquipo = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String nombreProyecto = cursor.getString(cursor.getColumnIndexOrThrow("NombreProyecto"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                    double promedio = cursor.getDouble(cursor.getColumnIndexOrThrow("Promedio"));
                    int cantEvaluaciones = cursor.getInt(cursor.getColumnIndexOrThrow("CantEval"));
                    String lugar = cursor.getString(cursor.getColumnIndexOrThrow("Lugar"));

                    proyectos.add(new ProyectoDestacado(
                            idEquipo, nombreEquipo, nombreProyecto, descripcion,
                            promedio, cantEvaluaciones, lugar != null ? lugar : "Sin ubicación"
                    ));

                } while (cursor.moveToNext());
                cursor.close();
            }

            // Si no hay suficientes proyectos mejor calificados, complementar con populares
            if (proyectos.size() < 3) {
                Cursor cursorPopulares = dbHelper.obtenerProyectosMasPopulares(5 - proyectos.size());
                if (cursorPopulares != null && cursorPopulares.moveToFirst()) {
                    do {
                        int idEquipo = cursorPopulares.getInt(cursorPopulares.getColumnIndexOrThrow("IdEquipo"));

                        // Evitar duplicados
                        boolean yaTiene = false;
                        for (ProyectoDestacado p : proyectos) {
                            if (p.getIdEquipo() == idEquipo) {
                                yaTiene = true;
                                break;
                            }
                        }

                        if (!yaTiene) {
                            String nombreEquipo = cursorPopulares.getString(cursorPopulares.getColumnIndexOrThrow("Nombre"));
                            String nombreProyecto = cursorPopulares.getString(cursorPopulares.getColumnIndexOrThrow("NombreProyecto"));
                            String descripcion = cursorPopulares.getString(cursorPopulares.getColumnIndexOrThrow("Descripcion"));
                            double promedio = cursorPopulares.getDouble(cursorPopulares.getColumnIndexOrThrow("Promedio"));
                            int cantEvaluaciones = cursorPopulares.getInt(cursorPopulares.getColumnIndexOrThrow("CantEval"));
                            String lugar = cursorPopulares.getString(cursorPopulares.getColumnIndexOrThrow("Lugar"));

                            proyectos.add(new ProyectoDestacado(
                                    idEquipo, nombreEquipo, nombreProyecto, descripcion,
                                    promedio, cantEvaluaciones, lugar != null ? lugar : "Sin ubicación"
                            ));
                        }

                    } while (cursorPopulares.moveToNext() && proyectos.size() < 5);
                    cursorPopulares.close();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener proyectos destacados", e);
        }

        return proyectos;
    }

    private void onProyectoClick(ProyectoDestacado proyecto) {
        try {
            Intent intent = new Intent(this, DetalleProyectoVisitanteActivity.class);
            intent.putExtra("idEquipo", proyecto.getIdEquipo());
            intent.putExtra("idVisitante", idVisitante);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } catch (Exception e) {
            Log.e(TAG, "Error al abrir detalle del proyecto", e);
            Toast.makeText(this, "Error al abrir proyecto", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        // Cards de funciones principales
        CardView cardEvaluarProyectos = findViewById(R.id.cardEvaluarProyectos);
        CardView cardVerAgenda = findViewById(R.id.cardVerAgenda);

        // Items de información útil
        View btnComoEvaluar = findViewById(R.id.linearComoEvaluar);
        View btnMapaUbicaciones = findViewById(R.id.linearMapaUbicaciones);
        View btnFeedbackEvento = findViewById(R.id.linearFeedbackEvento);

        // Botón salir
        Button btnSalir = findViewById(R.id.btnSalir);

        // Configurar listeners con manejo de errores
        setClickListenerSafe(cardEvaluarProyectos, () -> {
            Intent intent = new Intent(this, EvaluarProyectosVisitanteActivity.class);
            intent.putExtra("idVisitante", idVisitante);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        setClickListenerSafe(cardVerAgenda, () -> {
            Intent intent = new Intent(this, AgendaActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        setClickListenerSafe(btnComoEvaluar, () -> {
            Intent intent = new Intent(this, GuiaEvaluacionActivity.class);
            startActivity(intent);
        });

        setClickListenerSafe(btnMapaUbicaciones, () -> {
            Intent intent = new Intent(this, MapaUbicacionesActivity.class);
            startActivity(intent);
        });

        setClickListenerSafe(btnFeedbackEvento, () -> {
            Intent intent = new Intent(this, FeedbackEventoActivity.class);
            intent.putExtra("idVisitante", idVisitante);
            startActivity(intent);
        });

        setClickListenerSafe(btnVerTodosProyectos, () -> {
            Intent intent = new Intent(this, ListaProyectosActivity.class);
            intent.putExtra("esVisitante", true);
            intent.putExtra("idVisitante", idVisitante);
            startActivity(intent);
        });

        setClickListenerSafe(btnSalir, () -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void setClickListenerSafe(View view, Runnable action) {
        if (view != null) {
            view.setOnClickListener(v -> {
                try {
                    action.run();
                } catch (Exception e) {
                    Log.e(TAG, "Error en click listener", e);
                    Toast.makeText(this, "Error al ejecutar acción", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void limpiarVisitantesAntiguos() {
        // Ejecutar en background para no bloquear UI
        new Thread(() -> {
            try {
                int eliminados = dbHelper.limpiarVisitantesAntiguos();
                if (eliminados > 0) {
                    Log.d(TAG, "Visitantes antiguos eliminados: " + eliminados);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al limpiar visitantes antiguos", e);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar datos al volver a la actividad
        if (dbHelper != null) {
            cargarDatosIniciales();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        try {
            if (dbHelper != null) {
                dbHelper.cerrarConexion();
            }
            if (mainHandler != null) {
                mainHandler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al destruir actividad", e);
        }
        super.onDestroy();
    }

    // ==================== CLASES INTERNAS ====================

    /**
     * Clase para representar un proyecto destacado
     */
    public static class ProyectoDestacado {
        private int idEquipo;
        private String nombreEquipo;
        private String nombreProyecto;
        private String descripcion;
        private double promedio;
        private int cantEvaluaciones;
        private String lugar;

        public ProyectoDestacado(int idEquipo, String nombreEquipo, String nombreProyecto,
                                 String descripcion, double promedio, int cantEvaluaciones, String lugar) {
            this.idEquipo = idEquipo;
            this.nombreEquipo = nombreEquipo != null ? nombreEquipo : "Equipo sin nombre";
            this.nombreProyecto = nombreProyecto != null ? nombreProyecto : "Proyecto sin nombre";
            this.descripcion = descripcion != null ? descripcion : "Sin descripción disponible";
            this.promedio = promedio;
            this.cantEvaluaciones = cantEvaluaciones;
            this.lugar = lugar != null ? lugar : "Sin ubicación";
        }

        // Getters
        public int getIdEquipo() { return idEquipo; }
        public String getNombreEquipo() { return nombreEquipo; }
        public String getNombreProyecto() { return nombreProyecto; }
        public String getDescripcion() { return descripcion; }
        public double getPromedio() { return promedio; }
        public int getCantEvaluaciones() { return cantEvaluaciones; }
        public String getLugar() { return lugar; }
    }
}