package com.example.unitylab_expoconfig.ui.cartel;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.utils.CartelGenerator;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EquipoBD;

import java.io.File;

public class GenerarCartelActivity extends AppCompatActivity {

    private static final String TAG = "GenerarCartelActivity";

    private TextView tvNombreEquipo;
    private TextView tvNombreProyecto;
    private TextView tvInfoAdicional;
    private TextView tvEstadoCartel;
    private TextView tvMensajeEstado;
    private ImageView ivEstadoCartel;
    private ProgressBar progressBar;
    private Button btnGenerarCartel;
    private Button btnCompartirCartel;
    private Button btnVerCartel;

    private int equipoId;
    private int boletaUsuario;
    private String rutaCartelGenerado;
    private CartelGenerator cartelGenerator;
    private DbmsSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_generar_cartel);

            Log.d(TAG, "onCreate - Iniciando GenerarCartelActivity");

            initializeViews();
            setupToolbar();

            cartelGenerator = new CartelGenerator(this);
            dbHelper = new DbmsSQLiteHelper(this);

            // Obtener datos de intent
            obtenerDatosIntent();

            cargarInformacionEquipo();
            verificarEstadoCartel();

            // Configurar listeners - SIN verificación de permisos
            setupClickListeners();

        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error al inicializar la actividad", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            tvNombreEquipo = findViewById(R.id.tvNombreEquipoCartel);
            tvNombreProyecto = findViewById(R.id.tvNombreProyecto);
            tvInfoAdicional = findViewById(R.id.tvInfoAdicional);
            tvEstadoCartel = findViewById(R.id.tvEstadoCartel);
            tvMensajeEstado = findViewById(R.id.tvMensajeEstado);
            ivEstadoCartel = findViewById(R.id.ivEstadoCartel);
            progressBar = findViewById(R.id.progressBar);
            btnGenerarCartel = findViewById(R.id.btnGenerarCartel);
            btnCompartirCartel = findViewById(R.id.btnCompartirCartel);
            btnVerCartel = findViewById(R.id.btnVerCartel);

            Log.d(TAG, "Views inicializadas correctamente");

        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar views: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupToolbar() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Generar Cartel");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar toolbar: " + e.getMessage(), e);
        }
    }

    private void obtenerDatosIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            equipoId = intent.getIntExtra("idEquipo", -1);
            boletaUsuario = intent.getIntExtra("boleta", -1);
            Log.d(TAG, "Datos recibidos - Equipo ID: " + equipoId + ", Boleta: " + boletaUsuario);

            if (equipoId == -1) {
                Log.e(TAG, "ID de equipo inválido");
                Toast.makeText(this, "Error: ID de equipo no válido", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Log.e(TAG, "Intent nulo");
            Toast.makeText(this, "Error: Datos no recibidos", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupClickListeners() {
        // CORRECCIÓN: Ya no se necesita verificar permisos
        btnGenerarCartel.setOnClickListener(v -> generarCartel());
        btnCompartirCartel.setOnClickListener(v -> compartirCartel());
        btnVerCartel.setOnClickListener(v -> abrirCartel());
    }

    private void cargarInformacionEquipo() {
        Cursor cursor = null;
        try {
            cursor = dbHelper.obtenerEquipoPorId(equipoId);
            if (cursor != null && cursor.moveToFirst()) {
                Log.d(TAG, "Equipo encontrado");
                String nombreEquipo = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE));
                String nombreProyecto = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE_PROYECTO));
                int numAlumnos = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_NUMERO_ALUMNOS));
                int lugar = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_LUGAR));
                int cantVisitas = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_CANT_VISITAS));
                int cantEval = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_CANT_EVAL));
                float promedio = cursor.getFloat(cursor.getColumnIndexOrThrow(EquipoBD.COL_PROMEDIO));
                String cartel = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_CARTEL));

                Log.d(TAG, "Datos del equipo cargados correctamente");

                // Actualizar UI de forma segura
                updateUI(nombreEquipo, nombreProyecto, numAlumnos, lugar, cantVisitas, cantEval, promedio, cartel);

            } else {
                Log.e(TAG, "No se encontró el equipo con ID: " + equipoId);
                Toast.makeText(this, "Error: Equipo no encontrado", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar información del equipo: " + e.getMessage(), e);
            Toast.makeText(this, "Error al cargar información del equipo", Toast.LENGTH_SHORT).show();
            finish();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void updateUI(String nombreEquipo, String nombreProyecto, int numAlumnos,
                          int lugar, int cantVisitas, int cantEval, float promedio, String cartel) {
        try {
            // Actualizar información básica
            if (tvNombreEquipo != null) {
                tvNombreEquipo.setText(nombreEquipo != null ? nombreEquipo : "Sin nombre");
            }

            if (tvNombreProyecto != null) {
                tvNombreProyecto.setText(nombreProyecto != null ? nombreProyecto : "Sin proyecto");
            }

            // Información adicional
            if (tvInfoAdicional != null) {
                StringBuilder infoAdicional = new StringBuilder();
                infoAdicional.append("Integrantes: ").append(numAlumnos);
                if (lugar > 0) {
                    infoAdicional.append(" • Lugar: Mesa ").append(lugar);
                }
                infoAdicional.append(" • Visitas: ").append(cantVisitas);
                if (cantEval > 0) {
                    infoAdicional.append(" • Evaluaciones: ").append(cantEval);
                    infoAdicional.append(" • Promedio: ").append(String.format("%.1f", promedio));
                }
                tvInfoAdicional.setText(infoAdicional.toString());
            }

            // Verificar si ya tiene cartel
            if (cartel != null && !cartel.isEmpty()) {
                rutaCartelGenerado = cartel;
                mostrarCartelExistente();
            } else {
                mostrarCartelPorGenerar();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar UI: " + e.getMessage(), e);
        }
    }

    private void verificarEstadoCartel() {
        if (rutaCartelGenerado != null && !rutaCartelGenerado.isEmpty()) {
            // Verificar si el archivo existe
            File archivoCartel = new File(rutaCartelGenerado);
            if (archivoCartel.exists()) {
                mostrarCartelExistente();
            } else {
                mostrarCartelPorGenerar();
            }
        } else {
            mostrarCartelPorGenerar();
        }
    }

    private void mostrarCartelPorGenerar() {
        try {
            if (tvEstadoCartel != null) {
                tvEstadoCartel.setText("Listo para generar");
            }
            if (tvMensajeEstado != null) {
                tvMensajeEstado.setText("Tu cartel está listo para generar. Se guardará en el almacenamiento de la aplicación.");
            }
            if (ivEstadoCartel != null) {
                ivEstadoCartel.setImageResource(R.drawable.ic_info);
            }
            if (btnGenerarCartel != null) {
                btnGenerarCartel.setText("Generar Cartel");
            }
            if (btnCompartirCartel != null) {
                btnCompartirCartel.setVisibility(View.GONE);
            }
            if (btnVerCartel != null) {
                btnVerCartel.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en mostrarCartelPorGenerar: " + e.getMessage(), e);
        }
    }

    private void mostrarCartelExistente() {
        try {
            if (tvEstadoCartel != null) {
                tvEstadoCartel.setText("Cartel Generado");
            }
            if (tvMensajeEstado != null) {
                tvMensajeEstado.setText("Tu cartel ya ha sido generado. Puedes regenerarlo, compartirlo o enviarlo a impresión.");
            }
            if (ivEstadoCartel != null) {
                ivEstadoCartel.setImageResource(R.drawable.ic_check);
            }
            if (btnGenerarCartel != null) {
                btnGenerarCartel.setText("Regenerar Cartel");
            }
            if (btnCompartirCartel != null) {
                btnCompartirCartel.setVisibility(View.VISIBLE);
            }
            if (btnVerCartel != null) {
                btnVerCartel.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en mostrarCartelExistente: " + e.getMessage(), e);
        }
    }

    private void generarCartel() {
        Log.d(TAG, "Iniciando generación de cartel");
        mostrarProgreso(true);
        if (btnGenerarCartel != null) {
            btnGenerarCartel.setEnabled(false);
        }

        // Ejecutar en hilo secundario
        new Thread(() -> {
            try {
                CartelGenerator.CartelResult resultado = cartelGenerator.generarCartel(equipoId);

                runOnUiThread(() -> {
                    mostrarProgreso(false);
                    if (btnGenerarCartel != null) {
                        btnGenerarCartel.setEnabled(true);
                    }

                    if (resultado.exito) {
                        rutaCartelGenerado = resultado.rutaPdf;
                        mostrarCartelGenerado();
                        Toast.makeText(this, "Cartel generado exitosamente", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Cartel generado en: " + rutaCartelGenerado);
                    } else {
                        mostrarError(resultado.mensaje);
                        Toast.makeText(this, "Error: " + resultado.mensaje, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error al generar cartel: " + resultado.mensaje);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error al generar cartel: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    mostrarProgreso(false);
                    if (btnGenerarCartel != null) {
                        btnGenerarCartel.setEnabled(true);
                    }
                    Toast.makeText(this, "Error inesperado al generar cartel", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void mostrarCartelGenerado() {
        try {
            if (tvEstadoCartel != null) {
                tvEstadoCartel.setText("Cartel Generado");
            }
            if (tvMensajeEstado != null) {
                tvMensajeEstado.setText("Tu cartel ha sido generado exitosamente. Puedes descargarlo, compartirlo o enviarlo a impresión.");
            }
            if (ivEstadoCartel != null) {
                ivEstadoCartel.setImageResource(R.drawable.ic_check);
            }

            // Mostrar botones de acción
            if (btnCompartirCartel != null) {
                btnCompartirCartel.setVisibility(View.VISIBLE);
            }
            if (btnVerCartel != null) {
                btnVerCartel.setVisibility(View.VISIBLE);
            }

            // Cambiar texto del botón principal
            if (btnGenerarCartel != null) {
                btnGenerarCartel.setText("Regenerar Cartel");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en mostrarCartelGenerado: " + e.getMessage(), e);
        }
    }

    private void mostrarError(String mensaje) {
        try {
            if (tvEstadoCartel != null) {
                tvEstadoCartel.setText("Error");
            }
            if (tvMensajeEstado != null) {
                tvMensajeEstado.setText(mensaje != null ? mensaje : "Error desconocido");
            }
            if (ivEstadoCartel != null) {
                ivEstadoCartel.setImageResource(R.drawable.ic_warning);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en mostrarError: " + e.getMessage(), e);
        }
    }

    private void mostrarProgreso(boolean mostrar) {
        try {
            if (progressBar != null) {
                progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en mostrarProgreso: " + e.getMessage(), e);
        }
    }

    private void compartirCartel() {
        if (rutaCartelGenerado == null) {
            Toast.makeText(this, "No hay cartel para compartir", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File archivo = new File(rutaCartelGenerado);
            if (!archivo.exists()) {
                Toast.makeText(this, "El archivo del cartel no existe", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider", archivo);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Cartel - " + (tvNombreEquipo != null ? tvNombreEquipo.getText() : "Proyecto"));
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Cartel del proyecto: " + (tvNombreProyecto != null ? tvNombreProyecto.getText() : ""));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Compartir cartel"));

        } catch (Exception e) {
            Log.e(TAG, "Error al compartir cartel: " + e.getMessage(), e);
            Toast.makeText(this, "Error al compartir el cartel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirCartel() {
        if (rutaCartelGenerado == null) {
            Toast.makeText(this, "No hay cartel para mostrar", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File archivo = new File(rutaCartelGenerado);
            if (!archivo.exists()) {
                Toast.makeText(this, "El archivo del cartel no existe", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider", archivo);

            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(uri, "application/pdf");
            viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(viewIntent);

        } catch (Exception e) {
            Log.e(TAG, "Error al abrir cartel: " + e.getMessage(), e);
            Toast.makeText(this, "No se pudo abrir el cartel. Asegúrate de tener un lector de PDF instalado.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
    }
}