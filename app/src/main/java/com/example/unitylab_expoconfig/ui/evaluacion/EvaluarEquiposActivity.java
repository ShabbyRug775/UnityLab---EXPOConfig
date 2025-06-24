package com.example.unitylab_expoconfig.ui.evaluacion;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EvaluarEquiposActivity extends AppCompatActivity {

    private RecyclerView recyclerEquipos;
    private EquiposEvaluacionAdapter equiposAdapter;
    private DbmsSQLiteHelper dbHelper;
    private int idUsuario;
    private String tipoUsuario;
    private TextView tvEquiposEvaluados, tvEquiposPendientes;
    private LinearLayout tvSinEquipos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluar_equipos);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Evaluar Equipos");
        }

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);
        idUsuario = getIntent().getIntExtra("idUsuario", -1);
        tipoUsuario = getIntent().getStringExtra("tipoUsuario");

        // Inicializar vistas
        inicializarVistas();
        configurarRecyclerView();
        cargarEstadisticas();
        cargarEquipos();
    }

    private void inicializarVistas() {
        recyclerEquipos = findViewById(R.id.recyclerEquipos);
        tvSinEquipos = findViewById(R.id.tvSinEquipos);
        tvEquiposEvaluados = findViewById(R.id.tvEquiposEvaluados);
        tvEquiposPendientes = findViewById(R.id.tvEquiposPendientes);

        FloatingActionButton fabEscanearQR = findViewById(R.id.fabEscanearQR);
        fabEscanearQR.setOnClickListener(v -> abrirEscanerQR());
    }

    private void configurarRecyclerView() {
        recyclerEquipos.setLayoutManager(new LinearLayoutManager(this));
        equiposAdapter = new EquiposEvaluacionAdapter(new ArrayList<>(), this::onEquipoClick);
        recyclerEquipos.setAdapter(equiposAdapter);
    }

    private void cargarEstadisticas() {
        try {
            int evaluados = 0;
            int pendientes = 0;

            // Obtener todos los equipos disponibles para evaluar
            Cursor cursor = dbHelper.obtenerTodosEquipos();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idEquipo = cursor.getInt(cursor.getColumnIndexOrThrow("IdEquipo"));

                    // Verificar si ya evaluó este equipo
                    boolean yaEvaluo = dbHelper.yaEvaluoEquipo(idEquipo, idUsuario, null, null);

                    if (yaEvaluo) {
                        evaluados++;
                    } else {
                        pendientes++;
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }

            tvEquiposEvaluados.setText(String.valueOf(evaluados));
            tvEquiposPendientes.setText(String.valueOf(pendientes));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar estadísticas", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarEquipos() {
        List<EquipoEvaluacion> equipos = new ArrayList<>();

        try {
            // Cargar equipos registrados (estado "Registrado")
            Cursor cursor = dbHelper.obtenerEquiposPorEstado("Registrado");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idEquipo = cursor.getInt(cursor.getColumnIndexOrThrow("IdEquipo"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String nombreProyecto = cursor.getString(cursor.getColumnIndexOrThrow("NombreProyecto"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                    String lugar = cursor.getString(cursor.getColumnIndexOrThrow("Lugar"));
                    int cantEval = cursor.getInt(cursor.getColumnIndexOrThrow("CantEval"));
                    double promedio = cursor.getDouble(cursor.getColumnIndexOrThrow("Promedio"));

                    // Verificar si ya evaluó este equipo
                    boolean yaEvaluo = dbHelper.yaEvaluoEquipo(idEquipo, idUsuario, null, null);

                    equipos.add(new EquipoEvaluacion(idEquipo, nombre, nombreProyecto, descripcion,
                            lugar, cantEval, promedio, yaEvaluo));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar equipos", Toast.LENGTH_SHORT).show();
        }

        if (equipos.isEmpty()) {
            recyclerEquipos.setVisibility(View.GONE);
            tvSinEquipos.setVisibility(View.VISIBLE);
        } else {
            recyclerEquipos.setVisibility(View.VISIBLE);
            tvSinEquipos.setVisibility(View.GONE);
            equiposAdapter.actualizarEquipos(equipos);
        }
    }

    private void onEquipoClick(EquipoEvaluacion equipo) {
        if (equipo.isYaEvaluado()) {
            mostrarEvaluacionExistente(equipo);
        } else {
            mostrarFormularioEvaluacion(equipo);
        }
    }

    private void mostrarFormularioEvaluacion(EquipoEvaluacion equipo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_evaluar_equipo, null);

        TextView tvNombreEquipo = dialogView.findViewById(R.id.tvNombreEquipo);
        TextView tvProyectoEquipo = dialogView.findViewById(R.id.tvProyectoEquipo);
        TextView tvDescripcionEquipo = dialogView.findViewById(R.id.tvDescripcionEquipo);
        RatingBar ratingBarCalificacion = dialogView.findViewById(R.id.ratingBarCalificacion);
        EditText editComentarios = dialogView.findViewById(R.id.editComentarios);
        TextView tvCalificacionTexto = dialogView.findViewById(R.id.tvCalificacionTexto);

        tvNombreEquipo.setText(equipo.getNombre());
        tvProyectoEquipo.setText("Proyecto: " + equipo.getNombreProyecto());
        tvDescripcionEquipo.setText(equipo.getDescripcion());

        // Configurar rating bar
        ratingBarCalificacion.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                String[] textos = {"", "Muy Malo", "Malo", "Regular", "Bueno", "Excelente"};
                int calificacion = Math.round(rating);
                if (calificacion >= 0 && calificacion < textos.length) {
                    tvCalificacionTexto.setText(textos[calificacion] + " (" + calificacion + "/5)");
                }
            }
        });

        builder.setView(dialogView)
                .setTitle("Evaluar Equipo")
                .setPositiveButton("Evaluar", (dialog, which) -> {
                    float rating = ratingBarCalificacion.getRating();
                    String comentarios = editComentarios.getText().toString().trim();

                    if (rating == 0) {
                        Toast.makeText(this, "Debe asignar una calificación", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    enviarEvaluacion(equipo.getId(), Math.round(rating), comentarios);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarEvaluacionExistente(EquipoEvaluacion equipo) {
        try {
            Cursor cursor = dbHelper.obtenerEvaluacionesPorEquipo(equipo.getId());
            if (cursor != null && cursor.moveToFirst()) {
                // Buscar la evaluación del profesor actual
                do {
                    int numeroEmpleadoEvaluador = cursor.getInt(cursor.getColumnIndexOrThrow("NumeroEmpleadoEvaluador"));
                    if (numeroEmpleadoEvaluador == idUsuario) {
                        int calificacion = cursor.getInt(cursor.getColumnIndexOrThrow("Calificacion"));
                        String comentarios = cursor.getString(cursor.getColumnIndexOrThrow("Comentarios"));
                        String fecha = cursor.getString(cursor.getColumnIndexOrThrow("FechaEvaluacion"));

                        new AlertDialog.Builder(this)
                                .setTitle("Evaluación Enviada")
                                .setMessage("Ya evaluaste este equipo:\n\n" +
                                        "Calificación: " + calificacion + "/5\n" +
                                        "Comentarios: " + (comentarios.isEmpty() ? "Sin comentarios" : comentarios) + "\n" +
                                        "Fecha: " + fecha)
                                .setPositiveButton("Cerrar", null)
                                .show();
                        break;
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar evaluación", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarEvaluacion(int idEquipo, int calificacion, String comentarios) {
        try {
            long resultado = dbHelper.insertarEvaluacionProfesor(calificacion, comentarios, idEquipo, idUsuario);

            if (resultado != -1) {
                Toast.makeText(this, "Evaluación enviada exitosamente", Toast.LENGTH_SHORT).show();
                // Actualizar promedio del equipo
                actualizarPromedioEquipo(idEquipo);
                // Recargar datos
                cargarEstadisticas();
                cargarEquipos();
            } else {
                Toast.makeText(this, "Error al enviar evaluación", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar evaluación", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarPromedioEquipo(int idEquipo) {
        try {
            // Obtener todas las evaluaciones del equipo
            Cursor cursor = dbHelper.obtenerEvaluacionesPorEquipo(idEquipo);
            if (cursor != null) {
                double sumaCalificaciones = 0;
                int totalEvaluaciones = cursor.getCount();

                if (cursor.moveToFirst()) {
                    do {
                        int calificacion = cursor.getInt(cursor.getColumnIndexOrThrow("Calificacion"));
                        sumaCalificaciones += calificacion;
                    } while (cursor.moveToNext());
                }
                cursor.close();

                if (totalEvaluaciones > 0) {
                    double nuevoPromedio = sumaCalificaciones / totalEvaluaciones;
                    dbHelper.actualizarPromedioEquipo(idEquipo, nuevoPromedio, totalEvaluaciones);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirEscanerQR() {
        // TODO: Implementar escáner QR
        Toast.makeText(this, "Escáner QR en desarrollo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }

    // ==================== CLASES INTERNAS ====================

    public static class EquipoEvaluacion {
        private int id;
        private String nombre;
        private String nombreProyecto;
        private String descripcion;
        private String lugar;
        private int cantEval;
        private double promedio;
        private boolean yaEvaluado;

        public EquipoEvaluacion(int id, String nombre, String nombreProyecto, String descripcion,
                                String lugar, int cantEval, double promedio, boolean yaEvaluado) {
            this.id = id;
            this.nombre = nombre;
            this.nombreProyecto = nombreProyecto;
            this.descripcion = descripcion;
            this.lugar = lugar;
            this.cantEval = cantEval;
            this.promedio = promedio;
            this.yaEvaluado = yaEvaluado;
        }

        // Getters
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getNombreProyecto() { return nombreProyecto; }
        public String getDescripcion() { return descripcion; }
        public String getLugar() { return lugar; }
        public int getCantEval() { return cantEval; }
        public double getPromedio() { return promedio; }
        public boolean isYaEvaluado() { return yaEvaluado; }
    }

    // Adapter para la lista de equipos a evaluar
    private static class EquiposEvaluacionAdapter extends RecyclerView.Adapter<EquiposEvaluacionAdapter.EquipoViewHolder> {
        private List<EquipoEvaluacion> equipos;
        private OnEquipoClickListener listener;

        public interface OnEquipoClickListener {
            void onEquipoClick(EquipoEvaluacion equipo);
        }

        public EquiposEvaluacionAdapter(List<EquipoEvaluacion> equipos, OnEquipoClickListener listener) {
            this.equipos = equipos;
            this.listener = listener;
        }

        @NonNull
        @Override
        public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_equipo_evaluacion, parent, false);
            return new EquipoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
            EquipoEvaluacion equipo = equipos.get(position);
            holder.bind(equipo);
        }

        @Override
        public int getItemCount() {
            return equipos.size();
        }

        public void actualizarEquipos(List<EquipoEvaluacion> nuevosEquipos) {
            this.equipos.clear();
            this.equipos.addAll(nuevosEquipos);
            notifyDataSetChanged();
        }

        class EquipoViewHolder extends RecyclerView.ViewHolder {
            private CardView cardView;
            private TextView tvNombreEquipo, tvProyecto, tvLugar, tvEstadoEvaluacion, tvPromedio;
            private Button btnEvaluar;

            public EquipoViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardEquipo);
                tvNombreEquipo = itemView.findViewById(R.id.tvNombreEquipo);
                tvProyecto = itemView.findViewById(R.id.tvProyecto);
                tvLugar = itemView.findViewById(R.id.tvLugar);
                tvEstadoEvaluacion = itemView.findViewById(R.id.tvEstadoEvaluacion);
                tvPromedio = itemView.findViewById(R.id.tvPromedio);
                btnEvaluar = itemView.findViewById(R.id.btnEvaluar);
            }

            public void bind(EquipoEvaluacion equipo) {
                tvNombreEquipo.setText(equipo.getNombre());
                tvProyecto.setText(equipo.getNombreProyecto());
                tvLugar.setText("Ubicación: " + equipo.getLugar());

                if (equipo.getCantEval() > 0) {
                    tvPromedio.setText(String.format("★ %.1f (%d eval.)", equipo.getPromedio(), equipo.getCantEval()));
                } else {
                    tvPromedio.setText("Sin evaluaciones");
                }

                if (equipo.isYaEvaluado()) {
                    tvEstadoEvaluacion.setText("✓ Evaluado");
                    tvEstadoEvaluacion.setTextColor(itemView.getContext().getColor(R.color.success));
                    btnEvaluar.setText("Ver Evaluación");
                    btnEvaluar.setBackgroundColor(itemView.getContext().getColor(R.color.success_background));
                } else {
                    tvEstadoEvaluacion.setText("● Pendiente");
                    tvEstadoEvaluacion.setTextColor(itemView.getContext().getColor(R.color.warning));
                    btnEvaluar.setText("Evaluar");
                    btnEvaluar.setBackgroundColor(itemView.getContext().getColor(R.color.primary_color));
                }

                cardView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEquipoClick(equipo);
                    }
                });

                btnEvaluar.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEquipoClick(equipo);
                    }
                });
            }
        }
    }
}