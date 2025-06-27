package com.example.unitylab_expoconfig.ui.claves;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GestionarClavesActivity extends AppCompatActivity {

    private RecyclerView recyclerClaves;
    private ClavesAdapter clavesAdapter;
    private DbmsSQLiteHelper dbHelper;
    private int idUsuario;
    private LinearLayout tvSinClaves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_claves);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Gestionar Claves de Acceso");
        }

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);
        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        // Inicializar vistas
        inicializarVistas();
        configurarRecyclerView();
        cargarClaves();
    }

    private void inicializarVistas() {
        recyclerClaves = findViewById(R.id.recyclerClaves);
        tvSinClaves = findViewById(R.id.tvSinClaves);

        FloatingActionButton fabRegenerarTodas = findViewById(R.id.fabRegenerarTodas);
        fabRegenerarTodas.setOnClickListener(v -> mostrarDialogoRegenerarTodas());
    }

    private void configurarRecyclerView() {
        recyclerClaves.setLayoutManager(new LinearLayoutManager(this));
        clavesAdapter = new ClavesAdapter(new ArrayList<>(), this::onClaveClick);
        recyclerClaves.setAdapter(clavesAdapter);
    }

    private void cargarClaves() {
        List<ClaveProyecto> claves = new ArrayList<>();

        try {
            Cursor cursor = dbHelper.obtenerProyectosPorProfesor(idUsuario);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("IdProyecto"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String clave = cursor.getString(cursor.getColumnIndexOrThrow("Clave"));

                    // Contar equipos inscritos
                    Cursor cursorEquipos = dbHelper.obtenerEquiposPorProyecto(id);
                    int numEquipos = cursorEquipos != null ? cursorEquipos.getCount() : 0;
                    if (cursorEquipos != null) cursorEquipos.close();

                    claves.add(new ClaveProyecto(id, nombre, clave, numEquipos));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar claves", Toast.LENGTH_SHORT).show();
        }

        if (claves.isEmpty()) {
            recyclerClaves.setVisibility(View.GONE);
            tvSinClaves.setVisibility(View.VISIBLE);
        } else {
            recyclerClaves.setVisibility(View.VISIBLE);
            tvSinClaves.setVisibility(View.GONE);
            clavesAdapter.actualizarClaves(claves);
        }
    }

    private void onClaveClick(ClaveProyecto clave) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_detalle_clave, null);

        TextView tvNombreProyecto = dialogView.findViewById(R.id.tvNombreProyecto);
        TextView tvClaveActual = dialogView.findViewById(R.id.tvClaveActual);
        TextView tvNumEquipos = dialogView.findViewById(R.id.tvNumEquipos);
        TextView tvFechaGeneracion = dialogView.findViewById(R.id.tvFechaGeneracion);

        tvNombreProyecto.setText(clave.getNombreProyecto());
        tvClaveActual.setText(clave.getClave());
        tvNumEquipos.setText(String.valueOf(clave.getNumEquipos()) + " equipos inscritos");
        tvFechaGeneracion.setText("Generada: " + getCurrentDate());

        dialogView.findViewById(R.id.btnCopiarClave).setOnClickListener(v -> {
            copiarAlPortapapeles(clave.getClave());
            Toast.makeText(this, "Clave copiada al portapapeles", Toast.LENGTH_SHORT).show();
        });

        builder.setView(dialogView)
                .setTitle("Detalles de la Clave")
                .setPositiveButton("Regenerar Clave", (dialog, which) -> regenerarClave(clave))
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void regenerarClave(ClaveProyecto clave) {
        new AlertDialog.Builder(this)
                .setTitle("Regenerar Clave")
                .setMessage("¿Está seguro de que desea regenerar la clave para \"" + clave.getNombreProyecto() + "\"?\n\n" +
                        "Esta acción invalidará la clave actual y los equipos necesitarán la nueva clave para registrarse.")
                .setPositiveButton("Regenerar", (dialog, which) -> {
                    String nuevaClave = generarNuevaClave();

                    // Obtener datos actuales del proyecto
                    Cursor cursor = dbHelper.obtenerProyectoPorId(clave.getIdProyecto());
                    if (cursor != null && cursor.moveToFirst()) {
                        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                        int numeroEmpleadoProfesor = cursor.getInt(cursor.getColumnIndexOrThrow("NumeroEmpleadoProfesor"));
                        int idEvento = cursor.getInt(cursor.getColumnIndexOrThrow("IdEvento"));

                        int resultado = dbHelper.actualizarProyecto(clave.getIdProyecto(), nombre, descripcion,
                                nuevaClave, numeroEmpleadoProfesor, idEvento);

                        if (resultado > 0) {
                            Toast.makeText(this, "Clave regenerada exitosamente", Toast.LENGTH_SHORT).show();
                            cargarClaves(); // Recargar la lista
                        } else {
                            Toast.makeText(this, "Error al regenerar clave", Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoRegenerarTodas() {
        new AlertDialog.Builder(this)
                .setTitle("Regenerar Todas las Claves")
                .setMessage("¿Está seguro de que desea regenerar TODAS las claves de sus proyectos?\n\n" +
                        "Esta acción invalidará todas las claves actuales.")
                .setPositiveButton("Regenerar Todas", (dialog, which) -> regenerarTodasLasClaves())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void regenerarTodasLasClaves() {
        try {
            Cursor cursor = dbHelper.obtenerProyectosPorProfesor(idUsuario);
            int clavesRegeneradas = 0;

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idProyecto = cursor.getInt(cursor.getColumnIndexOrThrow("IdProyecto"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                    int numeroEmpleadoProfesor = cursor.getInt(cursor.getColumnIndexOrThrow("NumeroEmpleadoProfesor"));
                    int idEvento = cursor.getInt(cursor.getColumnIndexOrThrow("IdEvento"));

                    String nuevaClave = generarNuevaClave();

                    int resultado = dbHelper.actualizarProyecto(idProyecto, nombre, descripcion,
                            nuevaClave, numeroEmpleadoProfesor, idEvento);

                    if (resultado > 0) {
                        clavesRegeneradas++;
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }

            Toast.makeText(this, clavesRegeneradas + " claves regeneradas exitosamente", Toast.LENGTH_SHORT).show();
            cargarClaves();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al regenerar claves", Toast.LENGTH_SHORT).show();
        }
    }

    private String generarNuevaClave() {
        Random random = new Random();
        StringBuilder clave = new StringBuilder("PROJ-");

        // Agregar año actual
        clave.append(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        clave.append("-");

        // Agregar 6 caracteres alfanuméricos aleatorios
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 6; i++) {
            clave.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }

        return clave.toString();
    }

    private void copiarAlPortapapeles(String texto) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Clave de Proyecto", texto);
        clipboard.setPrimaryClip(clip);
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
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

    public static class ClaveProyecto {
        private int idProyecto;
        private String nombreProyecto;
        private String clave;
        private int numEquipos;

        public ClaveProyecto(int idProyecto, String nombreProyecto, String clave, int numEquipos) {
            this.idProyecto = idProyecto;
            this.nombreProyecto = nombreProyecto;
            this.clave = clave;
            this.numEquipos = numEquipos;
        }

        // Getters
        public int getIdProyecto() { return idProyecto; }
        public String getNombreProyecto() { return nombreProyecto; }
        public String getClave() { return clave; }
        public int getNumEquipos() { return numEquipos; }
    }

    private static class ClavesAdapter extends RecyclerView.Adapter<ClavesAdapter.ClaveViewHolder> {
        private List<ClaveProyecto> claves;
        private OnClaveClickListener listener;

        public interface OnClaveClickListener {
            void onClaveClick(ClaveProyecto clave);
        }

        public ClavesAdapter(List<ClaveProyecto> claves, OnClaveClickListener listener) {
            this.claves = claves;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ClaveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_clave_proyecto, parent, false);
            return new ClaveViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ClaveViewHolder holder, int position) {
            ClaveProyecto clave = claves.get(position);
            holder.bind(clave);
        }

        @Override
        public int getItemCount() {
            return claves.size();
        }

        public void actualizarClaves(List<ClaveProyecto> nuevasClaves) {
            this.claves.clear();
            this.claves.addAll(nuevasClaves);
            notifyDataSetChanged();
        }

        class ClaveViewHolder extends RecyclerView.ViewHolder {
            private CardView cardView;
            private TextView tvNombreProyecto, tvClave, tvNumEquipos;
            private View btnCopiar, btnRegenerar;

            public ClaveViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardClave);
                tvNombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
                tvClave = itemView.findViewById(R.id.tvClave);
                tvNumEquipos = itemView.findViewById(R.id.tvNumEquipos);
                btnCopiar = itemView.findViewById(R.id.btnCopiar);
                btnRegenerar = itemView.findViewById(R.id.btnRegenerar);
            }

            public void bind(ClaveProyecto clave) {
                tvNombreProyecto.setText(clave.getNombreProyecto());
                tvClave.setText(clave.getClave());
                tvNumEquipos.setText(clave.getNumEquipos() + " equipos");

                cardView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onClaveClick(clave);
                    }
                });

                btnCopiar.setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager) itemView.getContext()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Clave", clave.getClave());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(itemView.getContext(), "Clave copiada", Toast.LENGTH_SHORT).show();
                });

                btnRegenerar.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onClaveClick(clave);
                    }
                });
            }
        }
    }
}