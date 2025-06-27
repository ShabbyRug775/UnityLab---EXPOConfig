package com.example.unitylab_expoconfig.ui.agenda;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AgendaActivity extends AppCompatActivity {

    private RecyclerView recyclerEventos;
    private EventosAdapter eventosAdapter;
    private DbmsSQLiteHelper dbHelper;
    private TextView tvFechasEvento, tvDias, tvHoras, tvMinutos;
    private LinearLayout tvSinEventos;
    private Spinner spinnerDia, spinnerTipo;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable contadorRunnable;
    private List<EventoAgenda> eventosOriginales = new ArrayList<>();
    private String filtroActual = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_exposicion);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Agenda de la Exposición");
        }

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Inicializar vistas
        inicializarVistas();
        configurarRecyclerView();
        configurarSpinners();
        cargarEventos();
        iniciarContadorTiempo();
    }

    private void inicializarVistas() {
        recyclerEventos = findViewById(R.id.recyclerEventos);
        tvSinEventos = findViewById(R.id.layoutSinEventos);
        tvFechasEvento = findViewById(R.id.tvFechasEvento);
        tvDias = findViewById(R.id.tvDias);
        tvHoras = findViewById(R.id.tvHoras);
        tvMinutos = findViewById(R.id.tvMinutos);
        spinnerDia = findViewById(R.id.spinnerDia);
        spinnerTipo = findViewById(R.id.spinnerTipo);

        FloatingActionButton fabActualizar = findViewById(R.id.fabActualizar);
        fabActualizar.setOnClickListener(v -> {
            cargarEventos();
            Toast.makeText(this, "Agenda actualizada", Toast.LENGTH_SHORT).show();
        });

        FloatingActionButton fabVistaCalendario = findViewById(R.id.fabVistaCalendario);
        fabVistaCalendario.setOnClickListener(v -> {
            // TODO: Implementar vista de calendario
            Toast.makeText(this, "Vista de calendario en desarrollo", Toast.LENGTH_SHORT).show();
        });
    }

    private void configurarRecyclerView() {
        recyclerEventos.setLayoutManager(new LinearLayoutManager(this));
        eventosAdapter = new EventosAdapter(new ArrayList<>());
        recyclerEventos.setAdapter(eventosAdapter);
    }

    private void configurarSpinners() {
        // Configurar spinner de días
        List<String> dias = new ArrayList<>();
        dias.add("Todos los días");
        dias.add("Día 1");
        dias.add("Día 2");
        dias.add("Día 3");

        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDia.setAdapter(adapterDias);

        // Configurar spinner de tipos
        List<String> tipos = new ArrayList<>();
        tipos.add("Todos");
        tipos.add("Exposiciones");
        tipos.add("Conferencias");
        tipos.add("Talleres");
        tipos.add("Inauguración");
        tipos.add("Clausura");

        ArrayAdapter<String> adapterTipos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipos);

        // Configurar listeners
        spinnerDia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroActual = tipos.get(position);
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarEventos() {
        eventosOriginales.clear();

        try {
            Cursor cursor = dbHelper.obtenerTodosEventos();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("IdEvento"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                    String ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("Ubicacion"));
                    String fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("FechaInicio"));
                    String fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("FechaFin"));
                    String diasPresentacion = cursor.getString(cursor.getColumnIndexOrThrow("Diaspresentacion"));
                    String horaInicio = cursor.getString(cursor.getColumnIndexOrThrow("HoraInicio"));
                    String horaFin = cursor.getString(cursor.getColumnIndexOrThrow("HoraFin"));
                    int asistencia = cursor.getInt(cursor.getColumnIndexOrThrow("Asistencia"));

                    String tipo = determinarTipoEvento(nombre, descripcion);

                    eventosOriginales.add(new EventoAgenda(id, nombre, descripcion, ubicacion,
                            fechaInicio, fechaFin, diasPresentacion, horaInicio, horaFin, asistencia, tipo));
                } while (cursor.moveToNext());
                cursor.close();
            }

            // Actualizar fechas del evento principal
            if (!eventosOriginales.isEmpty()) {
                EventoAgenda primerEvento = eventosOriginales.get(0);
                tvFechasEvento.setText(formatearRangoFechas(primerEvento.getFechaInicio(), primerEvento.getFechaFin()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar eventos", Toast.LENGTH_SHORT).show();
        }

        aplicarFiltros();
    }

    private void aplicarFiltros() {
        List<EventoAgenda> eventosFiltrados = new ArrayList<>();

        for (EventoAgenda evento : eventosOriginales) {
            boolean cumpleTipo = filtroActual.equals("Todos") || evento.getTipo().equals(filtroActual);

            if (cumpleTipo) {
                eventosFiltrados.add(evento);
            }
        }

        if (eventosFiltrados.isEmpty()) {
            recyclerEventos.setVisibility(View.GONE);
            tvSinEventos.setVisibility(View.VISIBLE);
        } else {
            recyclerEventos.setVisibility(View.VISIBLE);
            tvSinEventos.setVisibility(View.GONE);
            eventosAdapter.actualizarEventos(eventosFiltrados);
        }
    }

    private String determinarTipoEvento(String nombre, String descripcion) {
        String nombreLower = nombre.toLowerCase();
        String descripcionLower = descripcion != null ? descripcion.toLowerCase() : "";

        if (nombreLower.contains("inauguración") || nombreLower.contains("apertura")) {
            return "Inauguración";
        } else if (nombreLower.contains("clausura") || nombreLower.contains("cierre")) {
            return "Clausura";
        } else if (nombreLower.contains("conferencia") || nombreLower.contains("charla")) {
            return "Conferencias";
        } else if (nombreLower.contains("taller") || nombreLower.contains("workshop")) {
            return "Talleres";
        } else {
            return "Exposiciones";
        }
    }

    private String formatearRangoFechas(String fechaInicio, String fechaFin) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd 'de' MMMM", Locale.getDefault());

            Date inicio = formatoEntrada.parse(fechaInicio);
            Date fin = formatoEntrada.parse(fechaFin);

            if (inicio != null && fin != null) {
                return formatoSalida.format(inicio) + " - " + formatoSalida.format(fin);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fechaInicio + " - " + fechaFin;
    }

    private void iniciarContadorTiempo() {
        contadorRunnable = new Runnable() {
            @Override
            public void run() {
                actualizarContador();
                handler.postDelayed(this, 60000); // Actualizar cada minuto
            }
        };
        handler.post(contadorRunnable);
    }

    private void actualizarContador() {
        try {
            if (eventosOriginales.isEmpty()) {
                tvDias.setText("00");
                tvHoras.setText("00");
                tvMinutos.setText("00");
                return;
            }

            // Usar la fecha del primer evento
            EventoAgenda primerEvento = eventosOriginales.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date fechaEvento = sdf.parse(primerEvento.getFechaInicio());

            if (fechaEvento != null) {
                Date ahora = new Date();
                long diferencia = fechaEvento.getTime() - ahora.getTime();

                if (diferencia > 0) {
                    long dias = TimeUnit.MILLISECONDS.toDays(diferencia);
                    long horas = TimeUnit.MILLISECONDS.toHours(diferencia) - TimeUnit.DAYS.toHours(dias);
                    long minutos = TimeUnit.MILLISECONDS.toMinutes(diferencia) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diferencia));

                    tvDias.setText(String.format("%02d", dias));
                    tvHoras.setText(String.format("%02d", horas));
                    tvMinutos.setText(String.format("%02d", minutos));
                } else {
                    // El evento ya comenzó o terminó
                    tvDias.setText("00");
                    tvHoras.setText("00");
                    tvMinutos.setText("00");
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
        if (handler != null && contadorRunnable != null) {
            handler.removeCallbacks(contadorRunnable);
        }
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }

    // ==================== CLASES INTERNAS ====================

    public static class EventoAgenda {
        private int id;
        private String nombre;
        private String descripcion;
        private String ubicacion;
        private String fechaInicio;
        private String fechaFin;
        private String diasPresentacion;
        private String horaInicio;
        private String horaFin;
        private int asistencia;
        private String tipo;

        public EventoAgenda(int id, String nombre, String descripcion, String ubicacion,
                            String fechaInicio, String fechaFin, String diasPresentacion,
                            String horaInicio, String horaFin, int asistencia, String tipo) {
            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.ubicacion = ubicacion;
            this.fechaInicio = fechaInicio;
            this.fechaFin = fechaFin;
            this.diasPresentacion = diasPresentacion;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
            this.asistencia = asistencia;
            this.tipo = tipo;
        }

        // Getters
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public String getUbicacion() { return ubicacion; }
        public String getFechaInicio() { return fechaInicio; }
        public String getFechaFin() { return fechaFin; }
        public String getDiasPresentacion() { return diasPresentacion; }
        public String getHoraInicio() { return horaInicio; }
        public String getHoraFin() { return horaFin; }
        public int getAsistencia() { return asistencia; }
        public String getTipo() { return tipo; }
    }

    // Adapter para la lista de eventos
    private static class EventosAdapter extends RecyclerView.Adapter<EventosAdapter.EventoViewHolder> {
        private List<EventoAgenda> eventos;

        public EventosAdapter(List<EventoAgenda> eventos) {
            this.eventos = eventos;
        }

        @NonNull
        @Override
        public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_evento_agenda, parent, false);
            return new EventoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
            EventoAgenda evento = eventos.get(position);
            holder.bind(evento);
        }

        @Override
        public int getItemCount() {
            return eventos.size();
        }

        public void actualizarEventos(List<EventoAgenda> nuevosEventos) {
            this.eventos.clear();
            this.eventos.addAll(nuevosEventos);
            notifyDataSetChanged();
        }

        static class EventoViewHolder extends RecyclerView.ViewHolder {
            private CardView cardView;
            private TextView tvNombreEvento, tvDescripcionEvento, tvUbicacionEvento;
            private TextView tvFechaEvento, tvHoraEvento, tvTipoEvento, tvAsistenciaEvento;

            public EventoViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardEvento);
                tvNombreEvento = itemView.findViewById(R.id.tvNombreEvento);
                tvDescripcionEvento = itemView.findViewById(R.id.tvDescripcionEvento);
                tvUbicacionEvento = itemView.findViewById(R.id.tvUbicacionEvento);
                tvFechaEvento = itemView.findViewById(R.id.tvFechaEvento);
                tvHoraEvento = itemView.findViewById(R.id.tvHoraEvento);
                tvTipoEvento = itemView.findViewById(R.id.tvTipoEvento);
                tvAsistenciaEvento = itemView.findViewById(R.id.tvAsistenciaEvento);
            }

            public void bind(EventoAgenda evento) {
                tvNombreEvento.setText(evento.getNombre());
                tvDescripcionEvento.setText(evento.getDescripcion());
                tvUbicacionEvento.setText(evento.getUbicacion());
                tvFechaEvento.setText(evento.getDiasPresentacion());
                tvHoraEvento.setText(evento.getHoraInicio() + " - " + evento.getHoraFin());
                tvTipoEvento.setText(evento.getTipo());
                tvAsistenciaEvento.setText(evento.getAsistencia() + " asistentes");
            }
        }
    }
}