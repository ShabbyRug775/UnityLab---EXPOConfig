package com.example.unitylab_expoconfig.ui.inicio;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.ui.proyectos.CrearProyectoActivity;
import com.example.unitylab_expoconfig.ui.proyectos.ListaProyectosActivity;
import com.example.unitylab_expoconfig.ui.equipos.GestionarEquiposActivity;
import com.example.unitylab_expoconfig.ui.claves.GestionarClavesActivity;
import com.example.unitylab_expoconfig.ui.agenda.AgendaActivity;
import com.example.unitylab_expoconfig.ui.evaluacion.EvaluarEquiposActivity;
import com.example.unitylab_expoconfig.adapters.ProyectosRecientesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProfesorActivity extends AppCompatActivity {
    private static final String TAG = "ProfesorActivity";

    private int idUsuarioActual;
    private String tipoUsuario;
    private String nombreUsuario;
    private FloatingActionButton fabAgregarProyecto;

    // Views para estadísticas
    private TextView tvMisProyectos, tvEquiposInscritos, tvEvaluacionesPendientes;
    private TextView tvBienvenidaProfesor;

    // RecyclerView para proyectos recientes
    private RecyclerView recyclerProyectosRecientes;
    private ProyectosRecientesAdapter proyectosAdapter;

    // Base de datos
    private DbmsSQLiteHelper dbHelper;

    // Handler para actualizaciones automáticas
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable actualizadorEstadisticas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_profesor);

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Obtener datos del intent
        Intent intent = getIntent();
        if (intent != null) {
            idUsuarioActual = intent.getIntExtra("idUsuario", -1);
            tipoUsuario = intent.getStringExtra("tipoUsuario");
            nombreUsuario = intent.getStringExtra("nombreUsuario");
        }

        // Configurar elementos de la UI
        inicializarVistas();
        configurarBienvenida();
        configurarCards();
        configurarAccionesAdicionales();
        configurarProyectosRecientes();
        configurarBotonCerrarSesion();
        configurarFAB();
        configurarNotificaciones();

        // Cargar datos
        cargarEstadisticas();
        configurarActualizacionAutomatica();
    }

    private void inicializarVistas() {
        tvBienvenidaProfesor = findViewById(R.id.tvBienvenidaProfesor);
        tvMisProyectos = findViewById(R.id.tvMisProyectos);
        tvEquiposInscritos = findViewById(R.id.tvEquiposInscritos);
        tvEvaluacionesPendientes = findViewById(R.id.tvEvaluacionesPendientes);
        recyclerProyectosRecientes = findViewById(R.id.recyclerProyectosRecientes);
        fabAgregarProyecto = findViewById(R.id.fabAgregarProyecto);
    }

    private void configurarBienvenida() {
        if (tvBienvenidaProfesor != null && nombreUsuario != null) {
            String mensajeBienvenida = "¡Bienvenido, " + nombreUsuario + "!";
            tvBienvenidaProfesor.setText(mensajeBienvenida);
        }
    }

    private void configurarCards() {
        CardView cardCrearProyecto = findViewById(R.id.cardCrearProyecto);
        CardView cardMisProyectos = findViewById(R.id.cardMisProyectos);
        CardView cardEvaluarEquipos = findViewById(R.id.cardEvaluarEquipos);
        CardView cardAgenda = findViewById(R.id.cardAgenda);

        if (cardCrearProyecto != null) {
            cardCrearProyecto.setOnClickListener(v -> {
                Intent intent = new Intent(this, CrearProyectoActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("tipoUsuario", tipoUsuario);
                intent.putExtra("nombreUsuario", nombreUsuario);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (cardMisProyectos != null) {
            cardMisProyectos.setOnClickListener(v -> {
                Intent intent = new Intent(this, ListaProyectosActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("filtroProfesor", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (cardEvaluarEquipos != null) {
            cardEvaluarEquipos.setOnClickListener(v -> {
                Intent intent = new Intent(this, EvaluarEquiposActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("tipoUsuario", tipoUsuario);
                intent.putExtra("nombreUsuario", nombreUsuario);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (cardAgenda != null) {
            cardAgenda.setOnClickListener(v -> {
                Intent intent = new Intent(this, AgendaActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("tipoUsuario", tipoUsuario);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

    private void configurarAccionesAdicionales() {
        View linearGenerarClaves = findViewById(R.id.linearGenerarClaves);
        View linearGestionarEquipos = findViewById(R.id.linearGestionarEquipos);
        View linearCambiarPassword = findViewById(R.id.linearCambiarPassword);

        if (linearGenerarClaves != null) {
            linearGenerarClaves.setOnClickListener(v -> {
                Intent intent = new Intent(this, GestionarClavesActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("tipoUsuario", tipoUsuario);
                intent.putExtra("nombreUsuario", nombreUsuario);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (linearGestionarEquipos != null) {
            linearGestionarEquipos.setOnClickListener(v -> {
                Intent intent = new Intent(this, GestionarEquiposActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("tipoUsuario", tipoUsuario);
                intent.putExtra("numeroEmpleadoProfesor", idUsuarioActual);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (linearCambiarPassword != null) {
            linearCambiarPassword.setOnClickListener(v -> mostrarDialogoCambiarPassword());
        }
    }

    private void configurarProyectosRecientes() {
        if (recyclerProyectosRecientes != null) {
            recyclerProyectosRecientes.setLayoutManager(new LinearLayoutManager(this));
            proyectosAdapter = new ProyectosRecientesAdapter(new ArrayList<>(), this::onProyectoClick);
            recyclerProyectosRecientes.setAdapter(proyectosAdapter);
        }

        TextView btnVerTodos = findViewById(R.id.btnVerTodosProyectos);
        if (btnVerTodos != null) {
            btnVerTodos.setOnClickListener(v -> {
                Intent intent = new Intent(this, ListaProyectosActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("filtroProfesor", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

    private void configurarBotonCerrarSesion() {
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionProfesor);
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());
        }
    }

    private void configurarFAB() {
        Log.d(TAG, "Configurando FAB...");
        try {
            if (fabAgregarProyecto != null) {
                // Ocultar el FAB si no hay sesión activa o si el usuario es estudiante
                if (idUsuarioActual == -1 || "estudiante".equals(tipoUsuario)) {
                    fabAgregarProyecto.setVisibility(View.GONE);
                    Log.d(TAG, "FAB oculto - No hay sesión activa o usuario es estudiante");
                } else {
                    fabAgregarProyecto.setVisibility(View.VISIBLE);
                }

                fabAgregarProyecto.setOnClickListener(v -> {
                    Log.d(TAG, "FAB clickeado");
                    try {
                        Intent intent = new Intent(this, CrearProyectoActivity.class);
                        intent.putExtra("idUsuario", idUsuarioActual);
                        intent.putExtra("tipoUsuario", tipoUsuario);
                        intent.putExtra("nombreUsuario", nombreUsuario);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } catch (Exception e) {
                        Log.e(TAG, "Error al abrir CrearProyecto: " + e.getMessage());
                        Toast.makeText(this, "Error al abrir formulario", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "FAB configurado exitosamente");
            } else {
                Log.w(TAG, "FAB no disponible, saltando configuración");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar FAB: " + e.getMessage());
        }
    }

    private void configurarNotificaciones() {
        View btnNotificaciones = findViewById(R.id.btnNotificaciones);
        if (btnNotificaciones != null) {
            btnNotificaciones.setOnClickListener(v -> {
                // TODO: Implementar sistema de notificaciones
                Toast.makeText(this, "Sistema de notificaciones en desarrollo", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // ==================== MÉTODOS DE CARGA DE DATOS ====================

    private void cargarEstadisticas() {
        try {
            // Cargar número de proyectos del profesor
            Cursor cursorProyectos = dbHelper.obtenerProyectosPorProfesor(idUsuarioActual);
            int totalProyectos = cursorProyectos != null ? cursorProyectos.getCount() : 0;
            if (tvMisProyectos != null) {
                tvMisProyectos.setText(String.valueOf(totalProyectos));
            }
            if (cursorProyectos != null) cursorProyectos.close();

            // Cargar número de equipos inscritos en proyectos del profesor
            int totalEquipos = contarEquiposDelProfesor();
            if (tvEquiposInscritos != null) {
                tvEquiposInscritos.setText(String.valueOf(totalEquipos));
            }

            // Cargar evaluaciones pendientes (simulado por ahora)
            int evaluacionesPendientes = contarEvaluacionesPendientes();
            if (tvEvaluacionesPendientes != null) {
                tvEvaluacionesPendientes.setText(String.valueOf(evaluacionesPendientes));
            }

            // Cargar proyectos recientes
            cargarProyectosRecientes();

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar estadísticas: " + e.getMessage());
            Toast.makeText(this, "Error al cargar estadísticas", Toast.LENGTH_SHORT).show();
        }
    }

    private int contarEquiposDelProfesor() {
        int totalEquipos = 0;
        try {
            // Obtener todos los proyectos del profesor
            Cursor cursorProyectos = dbHelper.obtenerProyectosPorProfesor(idUsuarioActual);
            if (cursorProyectos != null && cursorProyectos.moveToFirst()) {
                do {
                    int idProyecto = cursorProyectos.getInt(cursorProyectos.getColumnIndexOrThrow("IdProyecto"));

                    // Contar equipos en este proyecto
                    Cursor cursorEquipos = dbHelper.obtenerEquiposPorProyecto(idProyecto);
                    if (cursorEquipos != null) {
                        totalEquipos += cursorEquipos.getCount();
                        cursorEquipos.close();
                    }
                } while (cursorProyectos.moveToNext());
                cursorProyectos.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al contar equipos: " + e.getMessage());
        }
        return totalEquipos;
    }

    private int contarEvaluacionesPendientes() {
        // Por ahora retorna un número aleatorio entre 0-5
        // TODO: Implementar lógica real de evaluaciones pendientes
        return (int) (Math.random() * 6);
    }

    private void cargarProyectosRecientes() {
        try {
            List<ProyectoReciente> proyectosRecientes = new ArrayList<>();
            Cursor cursor = dbHelper.obtenerProyectosPorProfesor(idUsuarioActual);

            if (cursor != null && cursor.moveToFirst()) {
                int count = 0;
                do {
                    if (count >= 3) break; // Mostrar solo los 3 más recientes

                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("IdProyecto"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                    String clave = cursor.getString(cursor.getColumnIndexOrThrow("Clave"));

                    // Contar equipos inscritos
                    Cursor cursorEquipos = dbHelper.obtenerEquiposPorProyecto(id);
                    int numEquipos = cursorEquipos != null ? cursorEquipos.getCount() : 0;
                    if (cursorEquipos != null) cursorEquipos.close();

                    proyectosRecientes.add(new ProyectoReciente(id, nombre, descripcion, clave, numEquipos));
                    count++;
                } while (cursor.moveToNext());
                cursor.close();
            }

            if (proyectosAdapter != null) {
                proyectosAdapter.actualizarProyectos(proyectosRecientes);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar proyectos recientes: " + e.getMessage());
        }
    }

    // ==================== MÉTODOS DE DIÁLOGOS ====================

    private void mostrarDialogoCambiarPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cambiar_password, null);

        EditText editPasswordActual = dialogView.findViewById(R.id.editPasswordActual);
        EditText editPasswordNueva = dialogView.findViewById(R.id.editPasswordNueva);
        EditText editPasswordConfirmar = dialogView.findViewById(R.id.editPasswordConfirmar);

        builder.setView(dialogView)
                .setTitle("Cambiar Contraseña")
                .setPositiveButton("Cambiar", (dialog, which) -> {
                    String passwordActual = editPasswordActual.getText().toString().trim();
                    String passwordNueva = editPasswordNueva.getText().toString().trim();
                    String passwordConfirmar = editPasswordConfirmar.getText().toString().trim();

                    if (passwordActual.isEmpty() || passwordNueva.isEmpty() || passwordConfirmar.isEmpty()) {
                        Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!passwordNueva.equals(passwordConfirmar)) {
                        Toast.makeText(this, "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (passwordNueva.length() < 6) {
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Verificar contraseña actual
                    Cursor cursor = dbHelper.verificarCredencialesProfesor(idUsuarioActual, passwordActual);
                    if (cursor != null && cursor.moveToFirst()) {
                        // Actualizar contraseña
                        Cursor profesorCursor = dbHelper.obtenerProfesorPorNumeroEmpleado(idUsuarioActual);
                        if (profesorCursor != null && profesorCursor.moveToFirst()) {
                            String nombre = profesorCursor.getString(profesorCursor.getColumnIndexOrThrow("Nombre"));
                            String apellido1 = profesorCursor.getString(profesorCursor.getColumnIndexOrThrow("Apellido1"));
                            String apellido2 = profesorCursor.getString(profesorCursor.getColumnIndexOrThrow("Apellido2"));
                            String correo = profesorCursor.getString(profesorCursor.getColumnIndexOrThrow("Correo"));
                            String telefono = profesorCursor.getString(profesorCursor.getColumnIndexOrThrow("Telefono"));
                            String estado = profesorCursor.getString(profesorCursor.getColumnIndexOrThrow("Estado"));
                            int idAcademia = profesorCursor.getInt(profesorCursor.getColumnIndexOrThrow("IdAcademia"));
                            int numEmpleadoAdmin = profesorCursor.getInt(profesorCursor.getColumnIndexOrThrow("NumeroEmpleadoAdministrador"));

                            int resultado = dbHelper.actualizarProfesor(idUsuarioActual, nombre, apellido1, apellido2,
                                    passwordNueva, correo, telefono, estado, idAcademia, numEmpleadoAdmin);

                            if (resultado > 0) {
                                Toast.makeText(this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error al actualizar contraseña", Toast.LENGTH_SHORT).show();
                            }
                            profesorCursor.close();
                        }
                    } else {
                        Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
                    }

                    if (cursor != null) cursor.close();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Está seguro de que desea cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ==================== MÉTODOS DE EVENTOS ====================

    private void onProyectoClick(ProyectoReciente proyecto) {
        Intent intent = new Intent(this, ListaProyectosActivity.class);
        intent.putExtra("idUsuario", idUsuarioActual);
        intent.putExtra("proyectoSeleccionado", proyecto.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void configurarActualizacionAutomatica() {
        actualizadorEstadisticas = new Runnable() {
            @Override
            public void run() {
                cargarEstadisticas();
                handler.postDelayed(this, 30000); // Actualizar cada 30 segundos
            }
        };
        handler.postDelayed(actualizadorEstadisticas, 30000);
    }

    // ==================== CICLO DE VIDA ====================

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar estadísticas cuando se regrese a esta actividad
        cargarEstadisticas();
    }

    @Override
    protected void onDestroy() {
        if (handler != null && actualizadorEstadisticas != null) {
            handler.removeCallbacks(actualizadorEstadisticas);
        }
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // Mostrar confirmación antes de salir
        new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Desea salir del panel de profesor?")
                .setPositiveButton("Sí", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("No", null)
                .show();
    }

    // ==================== CLASE INTERNA ====================

    public static class ProyectoReciente {
        private int id;
        private String nombre;
        private String descripcion;
        private String clave;
        private int numEquipos;

        public ProyectoReciente(int id, String nombre, String descripcion, String clave, int numEquipos) {
            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.clave = clave;
            this.numEquipos = numEquipos;
        }

        // Getters
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public String getClave() { return clave; }
        public int getNumEquipos() { return numEquipos; }
    }
}