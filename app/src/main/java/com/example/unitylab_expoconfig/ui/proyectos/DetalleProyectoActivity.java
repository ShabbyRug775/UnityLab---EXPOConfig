package com.example.unitylab_expoconfig.ui.proyectos;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EstudianteBD;
import com.example.unitylab_expoconfig.SQLite.ProfesorBD;
import com.example.unitylab_expoconfig.SQLite.ProyectoBD;

public class DetalleProyectoActivity extends AppCompatActivity {
    private static final String TAG = "DetalleProyecto";

    // Views del layout
    private TextView tvNombreProyecto, tvDescripcion, tvNombreEquipo, tvMateria, tvGrupo,
            tvSemestre, tvCarrera, tvHerramientas, tvArquitectura, tvFunciones,
            tvProfesor, tvEstudianteLider, tvFechaCreacion, tvEstado;
    private Button btnVerCartel, btnEditarProyecto, btnCambiarEstado;

    private DbmsSQLiteHelper dbHelper;
    private int idProyecto;
    private int idUsuarioActual;
    private String tipoUsuario;
    private Proyecto proyectoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "=== INICIANDO DetalleProyectoActivity CON LAYOUT XML ===");

        try {
            // Usar el layout XML
            setContentView(R.layout.activity_detalle_proyecto);
            Log.d(TAG, "Layout XML cargado exitosamente");

            // Configurar toolbar
            configurarToolbar();

            // Obtener datos del intent
            obtenerDatosIntent();

            // Verificar datos válidos
            if (!validarDatos()) {
                return;
            }

            dbHelper = new DbmsSQLiteHelper(this);

            // Inicializar views
            inicializarVistas();

            // Cargar y mostrar datos del proyecto
            cargarDetallesProyecto();

            // Configurar botones
            configurarBotones();

            Log.d(TAG, "=== DetalleProyectoActivity CREADA EXITOSAMENTE ===");

        } catch (Exception e) {
            Log.e(TAG, "ERROR CRÍTICO EN onCreate(): " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar los detalles: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void configurarToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Detalle del Proyecto");
                Log.d(TAG, "Toolbar configurado exitosamente");
            } else {
                Log.w(TAG, "Toolbar no encontrado en el layout");
            }
        } catch (Exception e) {
            Log.w(TAG, "Error configurando toolbar: " + e.getMessage());
        }
    }

    private void obtenerDatosIntent() {
        Intent intent = getIntent();
        idProyecto = intent.getIntExtra("idProyecto", -1);
        idUsuarioActual = intent.getIntExtra("idUsuario", -1);
        tipoUsuario = intent.getStringExtra("tipoUsuario");

        Log.d(TAG, "Datos del intent: Proyecto ID=" + idProyecto +
                ", Usuario ID=" + idUsuarioActual + ", Tipo=" + tipoUsuario);
    }

    private boolean validarDatos() {
        if (idProyecto == -1) {
            Log.e(TAG, "ID de proyecto inválido");
            Toast.makeText(this, "Error: Proyecto no válido", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    private void inicializarVistas() {
        Log.d(TAG, "Inicializando vistas del layout...");

        try {
            // Información básica
            tvNombreProyecto = findViewById(R.id.tvNombreProyecto);
            tvDescripcion = findViewById(R.id.tvDescripcion);
            tvNombreEquipo = findViewById(R.id.tvNombreEquipo);
            tvFechaCreacion = findViewById(R.id.tvFechaCreacion);
            tvEstado = findViewById(R.id.tvEstado);

            // Información académica
            tvMateria = findViewById(R.id.tvMateria);
            tvGrupo = findViewById(R.id.tvGrupo);
            tvSemestre = findViewById(R.id.tvSemestre);
            tvCarrera = findViewById(R.id.tvCarrera);

            // Información técnica
            tvHerramientas = findViewById(R.id.tvHerramientas);
            tvArquitectura = findViewById(R.id.tvArquitectura);
            tvFunciones = findViewById(R.id.tvFunciones);

            // Equipo de trabajo
            tvProfesor = findViewById(R.id.tvProfesor);
            tvEstudianteLider = findViewById(R.id.tvEstudianteLider);

            // Botones
            btnVerCartel = findViewById(R.id.btnVerCartel);
            btnEditarProyecto = findViewById(R.id.btnEditarProyecto);
            btnCambiarEstado = findViewById(R.id.btnCambiarEstado);

            Log.d(TAG, "Vistas inicializadas exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar vistas: " + e.getMessage());
            throw e;
        }
    }

    private void cargarDetallesProyecto() {
        Log.d(TAG, "Cargando detalles del proyecto...");

        try {
            Cursor cursor = dbHelper.obtenerProyectoPorId(idProyecto);

            if (cursor != null && cursor.moveToFirst()) {
                proyectoActual = new Proyecto();

                // Cargar datos básicos del proyecto
                cargarDatosBasicos(cursor);

                cursor.close();

                // Cargar datos del profesor y estudiante
                cargarDatosProfesor();
                cargarDatosEstudiante();

                // Mostrar los datos en la UI
                mostrarDatosEnUI();

                Log.d(TAG, "Detalles del proyecto cargados exitosamente");

            } else {
                Log.e(TAG, "No se encontró el proyecto con ID: " + idProyecto);
                Toast.makeText(this, "No se encontró el proyecto", Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar detalles: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void cargarDatosBasicos(Cursor cursor) {
        proyectoActual.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ID)));
        proyectoActual.setNombreProyecto(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_NOMBRE_PROYECTO)));
        proyectoActual.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_DESCRIPCION)));
        proyectoActual.setNombreEquipo(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_NOMBRE_EQUIPO)));
        proyectoActual.setMateria(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_MATERIA)));
        proyectoActual.setGrupo(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_GRUPO)));
        proyectoActual.setSemestre(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_SEMESTRE)));
        proyectoActual.setCarrera(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_CARRERA)));
        proyectoActual.setHerramientasUtilizadas(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_HERRAMIENTAS)));
        proyectoActual.setArquitectura(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ARQUITECTURA)));
        proyectoActual.setFuncionesPrincipales(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_FUNCIONES)));
        proyectoActual.setIdProfesor(cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ID_PROFESOR)));
        proyectoActual.setIdEstudianteLider(cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ID_ESTUDIANTE_LIDER)));
        proyectoActual.setFechaCreacion(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_FECHA_CREACION)));
        proyectoActual.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ESTADO)));
        proyectoActual.setUrlCartel(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_URL_CARTEL)));
    }

    private void cargarDatosProfesor() {
        if (proyectoActual.getIdProfesor() > 0) {
            try {
                Cursor cursor = dbHelper.obtenerProfesorPorId(proyectoActual.getIdProfesor());
                if (cursor != null && cursor.moveToFirst()) {
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ProfesorBD.COL_NOMBRE));
                    String apellidos = cursor.getString(cursor.getColumnIndexOrThrow(ProfesorBD.COL_APELLIDOS));
                    proyectoActual.setNombreProfesor(nombre + " " + apellidos);
                    cursor.close();
                    Log.d(TAG, "Datos del profesor cargados: " + proyectoActual.getNombreProfesor());
                }
            } catch (Exception e) {
                Log.w(TAG, "Error al cargar datos del profesor: " + e.getMessage());
            }
        }
    }

    private void cargarDatosEstudiante() {
        if (proyectoActual.getIdEstudianteLider() > 0) {
            try {
                Cursor cursor = dbHelper.obtenerEstudiantePorId(proyectoActual.getIdEstudianteLider());
                if (cursor != null && cursor.moveToFirst()) {
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(EstudianteBD.COL_NOMBRE));
                    String apellidos = cursor.getString(cursor.getColumnIndexOrThrow(EstudianteBD.COL_APELLIDOS));
                    proyectoActual.setNombreEstudianteLider(nombre + " " + apellidos);
                    cursor.close();
                    Log.d(TAG, "Datos del estudiante cargados: " + proyectoActual.getNombreEstudianteLider());
                }
            } catch (Exception e) {
                Log.w(TAG, "Error al cargar datos del estudiante: " + e.getMessage());
            }
        }
    }

    private void mostrarDatosEnUI() {
        Log.d(TAG, "Mostrando datos en la UI...");

        try {
            // Actualizar título de la toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(proyectoActual.getNombreProyecto());
            }

            // Información básica
            setTextSafe(tvNombreProyecto, proyectoActual.getNombreProyecto());
            setTextSafe(tvDescripcion, proyectoActual.getDescripcion(), "Sin descripción");
            setTextSafe(tvNombreEquipo, proyectoActual.getNombreEquipo());
            setTextSafe(tvFechaCreacion, "Creado: " + (proyectoActual.getFechaCreacion() != null ?
                    proyectoActual.getFechaCreacion() : "No disponible"));
            setTextSafe(tvEstado, proyectoActual.getEstado());

            // Información académica
            setTextSafe(tvMateria, proyectoActual.getMateria());
            setTextSafe(tvGrupo, proyectoActual.getGrupo());
            setTextSafe(tvSemestre, proyectoActual.getSemestre());
            setTextSafe(tvCarrera, proyectoActual.getCarrera());

            // Información técnica
            setTextSafe(tvHerramientas, proyectoActual.getHerramientasUtilizadas(), "No especificadas");
            setTextSafe(tvArquitectura, proyectoActual.getArquitectura(), "No especificada");
            setTextSafe(tvFunciones, proyectoActual.getFuncionesPrincipales(), "No especificadas");

            // Equipo de trabajo
            setTextSafe(tvProfesor, proyectoActual.getNombreProfesor(), "No asignado");
            setTextSafe(tvEstudianteLider, proyectoActual.getNombreEstudianteLider(), "No asignado");

            Log.d(TAG, "Datos mostrados en la UI exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error al mostrar datos en UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setTextSafe(TextView textView, String text) {
        setTextSafe(textView, text, null);
    }

    private void setTextSafe(TextView textView, String text, String defaultText) {
        if (textView != null) {
            if (text != null && !text.trim().isEmpty()) {
                textView.setText(text);
            } else if (defaultText != null) {
                textView.setText(defaultText);
            }
        }
    }

    private void configurarBotones() {
        Log.d(TAG, "Configurando botones...");

        try {
            // Configurar botón de ver cartel
            if (btnVerCartel != null) {
                btnVerCartel.setOnClickListener(v -> {
                    if (proyectoActual.getUrlCartel() != null && !proyectoActual.getUrlCartel().trim().isEmpty()) {
                        abrirCartel();
                    } else {
                        Toast.makeText(this, "No hay cartel disponible", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Configurar botón de editar
            if (btnEditarProyecto != null) {
                btnEditarProyecto.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(this, EditarProyectoActivity.class);
                        intent.putExtra("idProyecto", idProyecto);
                        intent.putExtra("idUsuario", idUsuarioActual);
                        intent.putExtra("tipoUsuario", tipoUsuario);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error al abrir editar: " + e.getMessage());
                        Toast.makeText(this, "Función de editar temporalmente no disponible", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Configurar botón de cambiar estado
            if (btnCambiarEstado != null) {
                btnCambiarEstado.setOnClickListener(v -> mostrarDialogoCambiarEstado());
            }

            // Configurar visibilidad según permisos
            configurarVisibilidadBotones();

            Log.d(TAG, "Botones configurados exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error al configurar botones: " + e.getMessage());
        }
    }

    private void configurarVisibilidadBotones() {
        // Solo el profesor responsable y el estudiante líder pueden editar
        boolean puedeEditar = false;

        if ("profesor".equals(tipoUsuario) && proyectoActual.getIdProfesor() == idUsuarioActual) {
            puedeEditar = true;
        } else if ("estudiante".equals(tipoUsuario) && proyectoActual.getIdEstudianteLider() == idUsuarioActual) {
            puedeEditar = true;
        }

        if (btnEditarProyecto != null) {
            btnEditarProyecto.setVisibility(puedeEditar ? View.VISIBLE : View.GONE);
        }
        if (btnCambiarEstado != null) {
            btnCambiarEstado.setVisibility(puedeEditar ? View.VISIBLE : View.GONE);
        }

        Log.d(TAG, "Usuario puede editar: " + puedeEditar);
    }

    private void abrirCartel() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(proyectoActual.getUrlCartel()));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error al abrir cartel: " + e.getMessage());
            Toast.makeText(this, "No se puede abrir el cartel", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoCambiarEstado() {
        String[] estados = {"ACTIVO", "EN_DESARROLLO", "COMPLETADO", "PAUSADO", "CANCELADO"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Estado")
                .setItems(estados, (dialog, which) -> {
                    String nuevoEstado = estados[which];
                    cambiarEstado(nuevoEstado);
                });
        builder.show();
    }

    private void cambiarEstado(String nuevoEstado) {
        try {
            int resultado = dbHelper.cambiarEstadoProyecto(idProyecto, nuevoEstado);

            if (resultado > 0) {
                proyectoActual.setEstado(nuevoEstado);
                setTextSafe(tvEstado, nuevoEstado);
                Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Estado cambiado a: " + nuevoEstado);
            } else {
                Toast.makeText(this, "Error al actualizar estado", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cambiar estado: " + e.getMessage());
            Toast.makeText(this, "Error al cambiar estado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos cuando se regrese a esta pantalla
        if (proyectoActual != null) {
            cargarDetallesProyecto();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }
}