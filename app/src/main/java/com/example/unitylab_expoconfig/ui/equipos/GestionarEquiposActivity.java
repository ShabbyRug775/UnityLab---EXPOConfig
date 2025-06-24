package com.example.unitylab_expoconfig.ui.equipos;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class GestionarEquiposActivity extends AppCompatActivity {

    private RecyclerView recyclerEquipos;
    private EquiposAdapter equiposAdapter;
    private DbmsSQLiteHelper dbHelper;
    private int numeroEmpleadoProfesor;
    private TextView  tvTotalEquipos, tvPromedioGeneral;
    private LinearLayout tvSinEquipos;
    private Spinner spinnerFiltroProyecto;

    private List<ProyectoSpinner> proyectos = new ArrayList<>();
    private int proyectoSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_equipos);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Gestionar Equipos");
        }

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);
        numeroEmpleadoProfesor = getIntent().getIntExtra("numeroEmpleadoProfesor", -1);

        // Inicializar vistas
        inicializarVistas();
        configurarRecyclerView();
        configurarSpinnerProyectos();
        cargarEstadisticas();
        cargarEquipos();
    }

    private void inicializarVistas() {
        recyclerEquipos = findViewById(R.id.recyclerEquipos);
        tvSinEquipos = findViewById(R.id.tvSinEquipos);
        tvTotalEquipos = findViewById(R.id.tvTotalEquipos);
        tvPromedioGeneral = findViewById(R.id.tvPromedioGeneral);
        spinnerFiltroProyecto = findViewById(R.id.spinnerFiltroProyecto);

        FloatingActionButton fabEstadisticas = findViewById(R.id.fabEstadisticas);
        fabEstadisticas.setOnClickListener(v -> mostrarEstadisticasDetalladas());
    }

    private void configurarRecyclerView() {
        recyclerEquipos.setLayoutManager(new LinearLayoutManager(this));
        equiposAdapter = new EquiposAdapter(new ArrayList<>(), this::onEquipoClick);
        recyclerEquipos.setAdapter(equiposAdapter);
    }

    private void configurarSpinnerProyectos() {
        // Cargar proyectos del profesor
        proyectos.clear();
        proyectos.add(new ProyectoSpinner(-1, "Todos los proyectos"));

        try {
            Cursor cursor = dbHelper.obtenerProyectosPorProfesor(numeroEmpleadoProfesor);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("IdProyecto"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    proyectos.add(new ProyectoSpinner(id, nombre));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configurar adapter del spinner
        ArrayAdapter<ProyectoSpinner> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, proyectos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroProyecto.setAdapter(adapter);

        spinnerFiltroProyecto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                proyectoSeleccionado = proyectos.get(position).getId();
                cargarEquipos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarEstadisticas() {
        try {
            int totalEquipos = 0;
            double sumaPromedios = 0;
            int equiposConPromedio = 0;

            // Obtener todos los proyectos del profesor
            Cursor cursorProyectos = dbHelper.obtenerProyectosPorProfesor(numeroEmpleadoProfesor);
            if (cursorProyectos != null && cursorProyectos.moveToFirst()) {
                do {
                    int idProyecto = cursorProyectos.getInt(cursorProyectos.getColumnIndexOrThrow("IdProyecto"));

                    // Contar equipos en este proyecto
                    Cursor cursorEquipos = dbHelper.obtenerEquiposPorProyecto(idProyecto);
                    if (cursorEquipos != null) {
                        totalEquipos += cursorEquipos.getCount();

                        // Calcular promedios
                        if (cursorEquipos.moveToFirst()) {
                            do {
                                double promedio = cursorEquipos.getDouble(cursorEquipos.getColumnIndexOrThrow("Promedio"));
                                int cantEval = cursorEquipos.getInt(cursorEquipos.getColumnIndexOrThrow("CantEval"));
                                if (cantEval > 0) {
                                    sumaPromedios += promedio;
                                    equiposConPromedio++;
                                }
                            } while (cursorEquipos.moveToNext());
                        }
                        cursorEquipos.close();
                    }
                } while (cursorProyectos.moveToNext());
                cursorProyectos.close();
            }

            tvTotalEquipos.setText(String.valueOf(totalEquipos));

            if (equiposConPromedio > 0) {
                double promedioGeneral = sumaPromedios / equiposConPromedio;
                tvPromedioGeneral.setText(String.format("%.1f", promedioGeneral));
            } else {
                tvPromedioGeneral.setText("N/A");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar estadísticas", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarEquipos() {
        List<EquipoInfo> equipos = new ArrayList<>();

        try {
            if (proyectoSeleccionado == -1) {
                // Cargar todos los equipos de todos los proyectos del profesor
                Cursor cursorProyectos = dbHelper.obtenerProyectosPorProfesor(numeroEmpleadoProfesor);
                if (cursorProyectos != null && cursorProyectos.moveToFirst()) {
                    do {
                        int idProyecto = cursorProyectos.getInt(cursorProyectos.getColumnIndexOrThrow("IdProyecto"));
                        String nombreProyecto = cursorProyectos.getString(cursorProyectos.getColumnIndexOrThrow("Nombre"));

                        Cursor cursorEquipos = dbHelper.obtenerEquiposPorProyecto(idProyecto);
                        if (cursorEquipos != null && cursorEquipos.moveToFirst()) {
                            do {
                                equipos.add(crearEquipoInfo(cursorEquipos, nombreProyecto));
                            } while (cursorEquipos.moveToNext());
                            cursorEquipos.close();
                        }
                    } while (cursorProyectos.moveToNext());
                    cursorProyectos.close();
                }
            } else {
                // Cargar equipos del proyecto seleccionado
                Cursor cursorProyecto = dbHelper.obtenerProyectoPorId(proyectoSeleccionado);
                String nombreProyecto = "";
                if (cursorProyecto != null && cursorProyecto.moveToFirst()) {
                    nombreProyecto = cursorProyecto.getString(cursorProyecto.getColumnIndexOrThrow("Nombre"));
                    cursorProyecto.close();
                }

                Cursor cursorEquipos = dbHelper.obtenerEquiposPorProyecto(proyectoSeleccionado);
                if (cursorEquipos != null && cursorEquipos.moveToFirst()) {
                    do {
                        equipos.add(crearEquipoInfo(cursorEquipos, nombreProyecto));
                    } while (cursorEquipos.moveToNext());
                    cursorEquipos.close();
                }
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

    private EquipoInfo crearEquipoInfo(Cursor cursor, String nombreProyecto) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("IdEquipo"));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
        int numeroAlumnos = cursor.getInt(cursor.getColumnIndexOrThrow("Numero_Alumnos"));
        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
        String turno = cursor.getString(cursor.getColumnIndexOrThrow("Turno"));
        String lugar = cursor.getString(cursor.getColumnIndexOrThrow("Lugar"));
        int cantEval = cursor.getInt(cursor.getColumnIndexOrThrow("CantEval"));
        double promedio = cursor.getDouble(cursor.getColumnIndexOrThrow("Promedio"));
        int cantVisitas = cursor.getInt(cursor.getColumnIndexOrThrow("CantVisitas"));
        String estado = cursor.getString(cursor.getColumnIndexOrThrow("EstadoEquipo"));

        return new EquipoInfo(id, nombre, nombreProyecto, numeroAlumnos, descripcion,
                turno, lugar, cantEval, promedio, cantVisitas, estado);
    }

    private void onEquipoClick(EquipoInfo equipo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_detalle_equipo, null);

        // Llenar información del equipo
        TextView tvNombreEquipo = dialogView.findViewById(R.id.tvNombreEquipo);
        TextView tvProyectoEquipo = dialogView.findViewById(R.id.tvProyectoEquipo);
        TextView tvDescripcionEquipo = dialogView.findViewById(R.id.tvDescripcionEquipo);
        TextView tvNumAlumnos = dialogView.findViewById(R.id.tvNumAlumnos);
        TextView tvTurnoLugar = dialogView.findViewById(R.id.tvTurnoLugar);
        TextView tvEstadoEquipo = dialogView.findViewById(R.id.tvEstadoEquipo);
        TextView tvEvaluaciones = dialogView.findViewById(R.id.tvEvaluaciones);
        TextView tvPromedio = dialogView.findViewById(R.id.tvPromedio);
        TextView tvVisitas = dialogView.findViewById(R.id.tvVisitas);

        tvNombreEquipo.setText(equipo.getNombre());
        tvProyectoEquipo.setText("Proyecto: " + equipo.getNombreProyecto());
        tvDescripcionEquipo.setText(equipo.getDescripcion());
        tvNumAlumnos.setText(equipo.getNumeroAlumnos() + " integrantes");
        tvTurnoLugar.setText(equipo.getTurno() + " - " + equipo.getLugar());
        tvEstadoEquipo.setText(equipo.getEstado());
        tvEvaluaciones.setText(String.valueOf(equipo.getCantEval()));
        tvPromedio.setText(equipo.getCantEval() > 0 ?
                String.format("%.1f", equipo.getPromedio()) : "Sin evaluar");
        tvVisitas.setText(String.valueOf(equipo.getCantVisitas()));

        builder.setView(dialogView)
                .setTitle("Detalles del Equipo")
                .setPositiveButton("Ver Integrantes", (dialog, which) -> mostrarIntegrantes(equipo.getId()))
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void mostrarIntegrantes(int idEquipo) {
        try {
            Cursor cursor = dbHelper.obtenerAlumnosPorEquipo(idEquipo);
            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(this, "No se encontraron integrantes", Toast.LENGTH_SHORT).show();
                if (cursor != null) cursor.close();
                return;
            }

            StringBuilder integrantes = new StringBuilder();
            if (cursor.moveToFirst()) {
                do {
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String apellido1 = cursor.getString(cursor.getColumnIndexOrThrow("Apellido1"));
                    int boleta = cursor.getInt(cursor.getColumnIndexOrThrow("Boleta"));
                    String carrera = cursor.getString(cursor.getColumnIndexOrThrow("Carrera"));
                    String grupo = cursor.getString(cursor.getColumnIndexOrThrow("Grupo"));

                    integrantes.append("• ").append(nombre).append(" ").append(apellido1 != null ? apellido1 : "")
                            .append("\n  Boleta: ").append(boleta)
                            .append("\n  ").append(carrera).append(" - ").append(grupo)
                            .append("\n\n");
                } while (cursor.moveToNext());
            }
            cursor.close();

            new AlertDialog.Builder(this)
                    .setTitle("Integrantes del Equipo")
                    .setMessage(integrantes.toString())
                    .setPositiveButton("Cerrar", null)
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar integrantes", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarEstadisticasDetalladas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_estadisticas_equipos, null);

        // TODO: Llenar estadísticas detalladas
        TextView tvEquiposRegistrados = dialogView.findViewById(R.id.tvEquiposRegistrados);
        TextView tvEquiposEnFormacion = dialogView.findViewById(R.id.tvEquiposEnFormacion);
        TextView tvTotalAlumnos = dialogView.findViewById(R.id.tvTotalAlumnos);
        TextView tvPromedioEvaluaciones = dialogView.findViewById(R.id.tvPromedioEvaluaciones);

        // Calcular estadísticas
        calcularEstadisticasDetalladas(tvEquiposRegistrados, tvEquiposEnFormacion,
                tvTotalAlumnos, tvPromedioEvaluaciones);

        builder.setView(dialogView)
                .setTitle("Estadísticas Detalladas")
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void calcularEstadisticasDetalladas(TextView tvRegistrados, TextView tvFormacion,
                                                TextView tvAlumnos, TextView tvPromedio) {
        // TODO: Implementar cálculo de estadísticas detalladas
        tvRegistrados.setText("0");
        tvFormacion.setText("0");
        tvAlumnos.setText("0");
        tvPromedio.setText("N/A");
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

    public static class EquipoInfo {
        private int id;
        private String nombre;
        private String nombreProyecto;
        private int numeroAlumnos;
        private String descripcion;
        private String turno;
        private String lugar;
        private int cantEval;
        private double promedio;
        private int cantVisitas;
        private String estado;

        public EquipoInfo(int id, String nombre, String nombreProyecto, int numeroAlumnos,
                          String descripcion, String turno, String lugar, int cantEval,
                          double promedio, int cantVisitas, String estado) {
            this.id = id;
            this.nombre = nombre;
            this.nombreProyecto = nombreProyecto;
            this.numeroAlumnos = numeroAlumnos;
            this.descripcion = descripcion;
            this.turno = turno;
            this.lugar = lugar;
            this.cantEval = cantEval;
            this.promedio = promedio;
            this.cantVisitas = cantVisitas;
            this.estado = estado;
        }

        // Getters
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getNombreProyecto() { return nombreProyecto; }
        public int getNumeroAlumnos() { return numeroAlumnos; }
        public String getDescripcion() { return descripcion; }
        public String getTurno() { return turno; }
        public String getLugar() { return lugar; }
        public int getCantEval() { return cantEval; }
        public double getPromedio() { return promedio; }
        public int getCantVisitas() { return cantVisitas; }
        public String getEstado() { return estado; }
    }

    public static class ProyectoSpinner {
        private int id;
        private String nombre;

        public ProyectoSpinner(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }

        @Override
        public String toString() {
            return nombre;
        }
    }

    // Adapter para la lista de equipos
    private static class EquiposAdapter extends RecyclerView.Adapter<EquiposAdapter.EquipoViewHolder> {
        private List<EquipoInfo> equipos;
        private OnEquipoClickListener listener;

        public interface OnEquipoClickListener {
            void onEquipoClick(EquipoInfo equipo);
        }

        public EquiposAdapter(List<EquipoInfo> equipos, OnEquipoClickListener listener) {
            this.equipos = equipos;
            this.listener = listener;
        }

        @NonNull
        @Override
        public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_equipo_gestion, parent, false);
            return new EquipoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
            EquipoInfo equipo = equipos.get(position);
            holder.bind(equipo);
        }

        @Override
        public int getItemCount() {
            return equipos.size();
        }

        public void actualizarEquipos(List<EquipoInfo> nuevosEquipos) {
            this.equipos.clear();
            this.equipos.addAll(nuevosEquipos);
            notifyDataSetChanged();
        }

        class EquipoViewHolder extends RecyclerView.ViewHolder {
            private CardView cardView;
            private TextView tvNombreEquipo, tvProyecto, tvIntegrantes, tvEstado, tvPromedio;

            public EquipoViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardEquipo);
                tvNombreEquipo = itemView.findViewById(R.id.tvNombreEquipo);
                tvProyecto = itemView.findViewById(R.id.tvProyecto);
                tvIntegrantes = itemView.findViewById(R.id.tvIntegrantes);
                tvEstado = itemView.findViewById(R.id.tvEstado);
                tvPromedio = itemView.findViewById(R.id.tvPromedio);
            }

            public void bind(EquipoInfo equipo) {
                tvNombreEquipo.setText(equipo.getNombre());
                tvProyecto.setText(equipo.getNombreProyecto());
                tvIntegrantes.setText(equipo.getNumeroAlumnos() + " integrantes");
                tvEstado.setText(equipo.getEstado());

                if (equipo.getCantEval() > 0) {
                    tvPromedio.setText(String.format("★ %.1f", equipo.getPromedio()));
                } else {
                    tvPromedio.setText("Sin evaluar");
                }

                cardView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEquipoClick(equipo);
                    }
                });
            }
        }
    }
}