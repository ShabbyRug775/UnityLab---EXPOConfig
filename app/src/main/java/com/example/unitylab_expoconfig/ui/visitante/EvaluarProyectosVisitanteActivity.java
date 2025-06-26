package com.example.unitylab_expoconfig.ui.visitante;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EvaluarProyectosVisitanteActivity extends AppCompatActivity {

    private RecyclerView recyclerEquipos;
    private EquiposEvaluacionAdapter equiposAdapter;
    private DbmsSQLiteHelper dbHelper;
    private int idVisitante;
    private TextView tvEquiposEvaluados, tvEquiposPendientes;
    private LinearLayout layoutSinEquipos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluar_proyectos_visitante);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Evaluar Proyectos");
        }

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Obtener datos del intent
        idVisitante = getIntent().getIntExtra("idVisitante", -1);

        // Inicializar vistas
        initViews();

        // Cargar datos
        cargarEstadisticas();
        cargarEquipos();
    }

    private void initViews() {
        recyclerEquipos = findViewById(R.id.recyclerEquipos);
        tvEquiposEvaluados = findViewById(R.id.tvEquiposEvaluados);
        tvEquiposPendientes = findViewById(R.id.tvEquiposPendientes);
        layoutSinEquipos = findViewById(R.id.layoutSinEquipos);

        recyclerEquipos.setLayoutManager(new LinearLayoutManager(this));
    }

    private void cargarEstadisticas() {
        try {
            int evaluados = 0;
            int pendientes = 0;

            // Contar equipos evaluados y pendientes por este visitante
            Cursor cursor = dbHelper.obtenerEquiposPorEstado("Registrado");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idEquipo = cursor.getInt(cursor.getColumnIndexOrThrow("IdEquipo"));
                    boolean yaEvaluo = dbHelper.yaEvaluoEquipo(idEquipo, null, null, idVisitante);

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
            // Cargar equipos registrados
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
                    boolean yaEvaluo = dbHelper.yaEvaluoEquipo(idEquipo, null, null, idVisitante);

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
            layoutSinEquipos.setVisibility(View.VISIBLE);
        } else {
            recyclerEquipos.setVisibility(View.VISIBLE);
            layoutSinEquipos.setVisibility(View.GONE);

            equiposAdapter = new EquiposEvaluacionAdapter(equipos, this::onEquipoClick);
            recyclerEquipos.setAdapter(equiposAdapter);
        }
    }

    private void onEquipoClick(EquipoEvaluacion equipo) {
        if (equipo.isYaEvaluado()) {
            mostrarEvaluacionExistente(equipo.getId());
        } else {
            mostrarDialogEvaluacion(equipo);
        }
    }

    private void mostrarDialogEvaluacion(EquipoEvaluacion equipo) {
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

    private void mostrarEvaluacionExistente(int idEquipo) {
        try {
            Cursor cursor = dbHelper.obtenerEvaluacionesPorEquipo(idEquipo);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idVisitanteEvaluador = cursor.getInt(cursor.getColumnIndexOrThrow("IdVisitanteEvaluador"));

                    if (idVisitanteEvaluador == idVisitante) {
                        int calificacion = cursor.getInt(cursor.getColumnIndexOrThrow("Calificacion"));
                        String comentarios = cursor.getString(cursor.getColumnIndexOrThrow("Comentarios"));
                        String fecha = cursor.getString(cursor.getColumnIndexOrThrow("FechaEvaluacion"));

                        new AlertDialog.Builder(this)
                                .setTitle("Tu Evaluación")
                                .setMessage("Calificación: " + calificacion + " ⭐\n" +
                                        "Comentarios: " + (comentarios != null && !comentarios.isEmpty() ?
                                        comentarios : "Sin comentarios") + "\n" +
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
            long resultado = dbHelper.insertarEvaluacionVisitante(calificacion, comentarios, idEquipo, idVisitante);

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
            Toast.makeText(this, "Error al enviar evaluación", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarPromedioEquipo(int idEquipo) {
        try {
            Cursor cursor = dbHelper.obtenerEvaluacionesPorEquipo(idEquipo);
            if (cursor != null) {
                double sumaCalificaciones = 0;
                int cantEvaluaciones = 0;

                if (cursor.moveToFirst()) {
                    do {
                        int calificacion = cursor.getInt(cursor.getColumnIndexOrThrow("Calificacion"));
                        sumaCalificaciones += calificacion;
                        cantEvaluaciones++;
                    } while (cursor.moveToNext());
                }
                cursor.close();

                if (cantEvaluaciones > 0) {
                    double promedio = sumaCalificaciones / cantEvaluaciones;
                    dbHelper.actualizarPromedioEquipo(idEquipo, promedio, cantEvaluaciones);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    .inflate(R.layout.item_equipo_evaluar, parent, false);
            return new EquipoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
            EquipoEvaluacion equipo = equipos.get(position);

            holder.tvNombreProyecto.setText(equipo.getNombreProyecto());
            holder.tvNombreEquipo.setText(equipo.getNombre());
            holder.tvDescripcion.setText(equipo.getDescripcion());
            holder.tvLugar.setText("📍 " + equipo.getLugar());

            // Estado de evaluación
            if (equipo.isYaEvaluado()) {
                holder.tvEstadoEvaluacion.setText("✅ Ya evaluado");
                holder.tvEstadoEvaluacion.setTextColor(holder.itemView.getContext()
                        .getResources().getColor(R.color.success_text));
                holder.cardEquipo.setAlpha(0.7f);
            } else {
                holder.tvEstadoEvaluacion.setText("⭐ Evaluar");
                holder.tvEstadoEvaluacion.setTextColor(holder.itemView.getContext()
                        .getResources().getColor(R.color.primary_color));
                holder.cardEquipo.setAlpha(1.0f);
            }

            // Promedio y evaluaciones
            if (equipo.getCantEval() > 0) {
                holder.tvPromedio.setText(String.format(Locale.getDefault(),
                        "%.1f ⭐ (%d eval.)", equipo.getPromedio(), equipo.getCantEval()));
                holder.tvPromedio.setVisibility(View.VISIBLE);
            } else {
                holder.tvPromedio.setVisibility(View.GONE);
            }

            holder.cardEquipo.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEquipoClick(equipo);
                }
            });
        }

        @Override
        public int getItemCount() {
            return equipos != null ? equipos.size() : 0;
        }

        static class EquipoViewHolder extends RecyclerView.ViewHolder {
            CardView cardEquipo;
            TextView tvNombreProyecto, tvNombreEquipo, tvDescripcion, tvLugar,
                    tvPromedio, tvEstadoEvaluacion;

            public EquipoViewHolder(@NonNull View itemView) {
                super(itemView);
                cardEquipo = itemView.findViewById(R.id.cardEquipo);
                tvNombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
                tvNombreEquipo = itemView.findViewById(R.id.tvNombreEquipo);
                tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
                tvLugar = itemView.findViewById(R.id.tvLugar);
                tvPromedio = itemView.findViewById(R.id.tvPromedio);
                tvEstadoEvaluacion = itemView.findViewById(R.id.tvEstadoEvaluacion);
            }
        }
    }
}