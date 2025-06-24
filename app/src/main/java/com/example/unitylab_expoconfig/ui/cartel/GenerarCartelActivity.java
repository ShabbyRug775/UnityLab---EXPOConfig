package com.example.unitylab_expoconfig.ui.cartel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.utils.CartelGenerator;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
//import com.example.unitylab_expoconfig.SQLite.EquipoDB;

import java.io.File;

public class GenerarCartelActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;

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
    private String rutaCartelGenerado;
    private CartelGenerator cartelGenerator;
    private DbmsSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_cartel);

        initializeViews();
        setupToolbar();

        cartelGenerator = new CartelGenerator(this);
        dbHelper = new DbmsSQLiteHelper(this);

        // Obtener ID del equipo de los extras del intent
        equipoId = getIntent().getIntExtra("equipo_id", -1);

        if (equipoId == -1) {
            Toast.makeText(this, "Error: No se especificó el equipo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //cargarInformacionEquipo();
        verificarEstadoCartel();

        btnGenerarCartel.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                //generarCartel();
            } else {
                requestStoragePermission();
            }
        });

        btnCompartirCartel.setOnClickListener(v -> compartirCartel());
        btnVerCartel.setOnClickListener(v -> abrirCartel());
    }

    private void initializeViews() {
        tvNombreEquipo = findViewById(R.id.tvNombreEquipo);
        tvNombreProyecto = findViewById(R.id.tvNombreProyecto);
        //tvInfoAdicional = findViewById(R.id.tvInfoAdicional);
        tvEstadoCartel = findViewById(R.id.tvEstadoCartel);
        tvMensajeEstado = findViewById(R.id.tvMensajeEstado);
        ivEstadoCartel = findViewById(R.id.ivEstadoCartel);
        //progressBar = findViewById(R.id.progressBar);
        btnGenerarCartel = findViewById(R.id.btnGenerarCartel);
        //btnCompartirCartel = findViewById(R.id.btnCompartirCartel);
        btnVerCartel = findViewById(R.id.btnVerCartel);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Generar Cartel");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

//    private void cargarInformacionEquipo() {
//        Cursor cursor = null;
//        try {
//            cursor = dbHelper.obtenerEquipoPorId(equipoId);
//            if (cursor != null && cursor.moveToFirst()) {
//                String nombreEquipo = cursor.getString(cursor.getColumnIndexOrThrow(EquipoDB.COL_NOMBRE));
//                String nombreProyecto = cursor.getString(cursor.getColumnIndexOrThrow(EquipoDB.COL_NOMBRE_PROYECTO));
//                int numAlumnos = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_NUM_ALUMNOS));
//                int lugar = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_LUGAR));
//                int cantVisitas = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_CANT_VISITAS));
//                int cantEval = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_CANT_EVAL));
//                float promedio = cursor.getFloat(cursor.getColumnIndexOrThrow(EquipoDB.COL_PROMEDIO));
//                String cartel = cursor.getString(cursor.getColumnIndexOrThrow(EquipoDB.COL_CARTEL));
//
//                // Actualizar UI
//                tvNombreEquipo.setText(nombreEquipo);
//                tvNombreProyecto.setText(nombreProyecto);
//
//                // Información adicional
//                StringBuilder infoAdicional = new StringBuilder();
//                infoAdicional.append("Integrantes: ").append(numAlumnos);
//                if (lugar > 0) {
//                    infoAdicional.append(" • Lugar: Mesa ").append(lugar);
//                }
//                infoAdicional.append(" • Visitas: ").append(cantVisitas);
//                if (cantEval > 0) {
//                    infoAdicional.append(" • Evaluaciones: ").append(cantEval);
//                    infoAdicional.append(" • Promedio: ").append(String.format("%.1f", promedio));
//                }
//
//                //tvInfoAdicional.setText(infoAdicional.toString());
//
//                // Verificar si ya tiene cartel
//                if (cartel != null && !cartel.isEmpty()) {
//                    rutaCartelGenerado = cartel;
//                    mostrarCartelExistente();
//                }
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Error al cargar información del equipo", Toast.LENGTH_SHORT).show();
//        } finally {
//            if (cursor != null) cursor.close();
//        }
//    }

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
        tvEstadoCartel.setText("Listo para generar");
        tvMensajeEstado.setText("Tu cartel está listo para generar. Una vez creado, enviarlo a impresión.");
        ivEstadoCartel.setImageResource(R.drawable.ic_info);
        btnGenerarCartel.setText("Generar Cartel");
        btnCompartirCartel.setVisibility(View.GONE);
        btnVerCartel.setVisibility(View.GONE);
    }

    private void mostrarCartelExistente() {
        tvEstadoCartel.setText("Cartel Generado");
        tvMensajeEstado.setText("Tu cartel ya ha sido generado. Puedes regenerarlo, compartirlo o enviarlo a impresión.");
        ivEstadoCartel.setImageResource(R.drawable.ic_check);
        btnGenerarCartel.setText("Regenerar Cartel");
        btnCompartirCartel.setVisibility(View.VISIBLE);
        btnVerCartel.setVisibility(View.VISIBLE);
    }

//    private void generarCartel() {
//        mostrarProgreso(true);
//        btnGenerarCartel.setEnabled(false);
//
//        // Ejecutar en hilo secundario
//        new Thread(() -> {
//            CartelGenerator.CartelResult resultado = cartelGenerator.generarCartel(equipoId);
//
//            runOnUiThread(() -> {
//                mostrarProgreso(false);
//                btnGenerarCartel.setEnabled(true);
//
//                if (resultado.exito) {
//                    rutaCartelGenerado = resultado.rutaPdf;
//                    mostrarCartelGenerado();
//                    Toast.makeText(this, "Cartel generado exitosamente", Toast.LENGTH_SHORT).show();
//                } else {
//                    mostrarError(resultado.mensaje);
//                    Toast.makeText(this, "Error: " + resultado.mensaje, Toast.LENGTH_LONG).show();
//                }
//            });
//        }).start();
//    }

    private void mostrarCartelGenerado() {
        tvEstadoCartel.setText("Cartel Generado");
        tvMensajeEstado.setText("Tu cartel ha sido generado exitosamente. Puedes descargarlo, compartirlo o enviarlo a impresión.");
        ivEstadoCartel.setImageResource(R.drawable.ic_check);

        // Mostrar botones de acción
        btnCompartirCartel.setVisibility(View.VISIBLE);
        btnVerCartel.setVisibility(View.VISIBLE);

        // Cambiar texto del botón principal
        btnGenerarCartel.setText("Regenerar Cartel");
    }

    private void mostrarError(String mensaje) {
        tvEstadoCartel.setText("Error");
        tvMensajeEstado.setText(mensaje);
        ivEstadoCartel.setImageResource(R.drawable.ic_warning);
    }

    private void mostrarProgreso(boolean mostrar) {
        //progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
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
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Cartel - " + tvNombreEquipo.getText());
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Cartel del proyecto: " + tvNombreProyecto.getText());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Compartir cartel"));

        } catch (Exception e) {
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
            Toast.makeText(this, "No se pudo abrir el cartel. Asegúrate de tener un lector de PDF instalado.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //generarCartel();
            } else {
                Toast.makeText(this, "Se requiere permiso de almacenamiento para generar el cartel", Toast.LENGTH_LONG).show();
            }
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