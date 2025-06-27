package com.example.unitylab_expoconfig.ui.constancia;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.AlumnoBD;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EquipoBD;
import com.example.unitylab_expoconfig.SQLite.EvaluacionBD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GenerarConstanciaActivity extends AppCompatActivity {

    private DbmsSQLiteHelper dbHelper;
    private int idEquipo = -1;
    private int boletaUsuario = -1;
    private String nombreUsuario;

    // Views
    private TextView tvNombreEquipo, tvNombreProyecto, tvNumeroEvaluaciones;
    private TextView tvCalificacionPromedio, tvIntegrantes, tvMensajeNoDisponible;
    private TextView tvNombresConstancia, tvProyectoConstancia, tvFechaConstancia;
    private ImageView iconRequisito1, iconRequisito2, iconRequisito3;
    private CardView cardRequisitos, cardVistaPrevia, cardNoDisponible;
    private LinearLayout linearBotonesAccion;
    private Button btnGenerarConstancia, btnDescargarPDF;
    private CheckBox checkIncluirCalificaciones, checkIncluirComentarios, checkIncluirQR;

    private boolean constanciaDisponible = false;
    private ArrayList<String> nombresIntegrantes = new ArrayList<>();
    private String nombreProyecto = "";
    private int totalEvaluaciones = 0;
    private double promedioCalificaciones = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_constancia);

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Obtener datos del intent
        Intent intent = getIntent();
        idEquipo = intent.getIntExtra("idEquipo", -1);
        boletaUsuario = intent.getIntExtra("boleta", -1);
        nombreUsuario = intent.getStringExtra("nombreUsuario");

        // Inicializar views
        inicializarViews();

        // Cargar datos del equipo
        cargarDatosEquipo();

        // Verificar requisitos
        verificarRequisitos();

        // Configurar listeners
        configurarListeners();
    }

    private void inicializarViews() {
        tvNombreEquipo = findViewById(R.id.tvNombreEquipo);
        tvNombreProyecto = findViewById(R.id.tvNombreProyecto);
        tvNumeroEvaluaciones = findViewById(R.id.tvNumeroEvaluaciones);
        tvCalificacionPromedio = findViewById(R.id.tvCalificacionPromedio);
        tvIntegrantes = findViewById(R.id.tvIntegrantes);
        tvMensajeNoDisponible = findViewById(R.id.tvMensajeNoDisponible);
        tvNombresConstancia = findViewById(R.id.tvNombresConstancia);
        tvProyectoConstancia = findViewById(R.id.tvProyectoConstancia);
        tvFechaConstancia = findViewById(R.id.tvFechaConstancia);

        iconRequisito1 = findViewById(R.id.iconRequisito1);
        iconRequisito2 = findViewById(R.id.iconRequisito2);
        iconRequisito3 = findViewById(R.id.iconRequisito3);

        cardRequisitos = findViewById(R.id.cardRequisitos);
        cardVistaPrevia = findViewById(R.id.cardVistaPrevia);
        cardNoDisponible = findViewById(R.id.cardNoDisponible);
        linearBotonesAccion = findViewById(R.id.linearBotonesAccion);

        btnGenerarConstancia = findViewById(R.id.btnGenerarConstancia);
        btnDescargarPDF = findViewById(R.id.btnDescargarPDF);

        checkIncluirCalificaciones = findViewById(R.id.checkIncluirCalificaciones);
        checkIncluirComentarios = findViewById(R.id.checkIncluirComentarios);
        checkIncluirQR = findViewById(R.id.checkIncluirQR);

        // Configurar fecha actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (tvFechaConstancia != null) {
            tvFechaConstancia.setText(sdf.format(new Date()));
        }
    }

    private void cargarDatosEquipo() {
        try {
            // Cargar datos del equipo
            Cursor cursorEquipo = dbHelper.obtenerEquipoPorId(idEquipo);
            if (cursorEquipo != null && cursorEquipo.moveToFirst()) {
                String nombreEquipo = cursorEquipo.getString(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE));
                nombreProyecto = cursorEquipo.getString(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE_PROYECTO));
                totalEvaluaciones = cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_CANT_EVAL));
                promedioCalificaciones = cursorEquipo.getDouble(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_PROMEDIO));

                if (tvNombreEquipo != null) tvNombreEquipo.setText(nombreEquipo);
                if (tvNombreProyecto != null) tvNombreProyecto.setText(nombreProyecto);
                if (tvNumeroEvaluaciones != null) tvNumeroEvaluaciones.setText(String.valueOf(totalEvaluaciones));
                if (tvCalificacionPromedio != null) tvCalificacionPromedio.setText(String.format("%.1f", promedioCalificaciones));

                cursorEquipo.close();
            }

            // Cargar integrantes del equipo
            Cursor cursorIntegrantes = dbHelper.obtenerAlumnosPorEquipo(idEquipo);
            nombresIntegrantes.clear();

            if (cursorIntegrantes != null && cursorIntegrantes.moveToFirst()) {
                do {
                    String nombre = cursorIntegrantes.getString(cursorIntegrantes.getColumnIndexOrThrow(AlumnoBD.COL_NOMBRE));
                    String apellido1 = cursorIntegrantes.getString(cursorIntegrantes.getColumnIndexOrThrow(AlumnoBD.COL_APELLIDO1));
                    String apellido2 = cursorIntegrantes.getString(cursorIntegrantes.getColumnIndexOrThrow(AlumnoBD.COL_APELLIDO2));

                    String nombreCompleto = nombre;
                    if (apellido1 != null && !apellido1.isEmpty()) {
                        nombreCompleto += " " + apellido1;
                    }
                    if (apellido2 != null && !apellido2.isEmpty()) {
                        nombreCompleto += " " + apellido2;
                    }

                    nombresIntegrantes.add(nombreCompleto);
                } while (cursorIntegrantes.moveToNext());

                cursorIntegrantes.close();
            }

            if (tvIntegrantes != null) {
                tvIntegrantes.setText(String.valueOf(nombresIntegrantes.size()));
            }

            // Actualizar vista previa de constancia
            actualizarVistaPrevia();

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos del equipo", Toast.LENGTH_SHORT).show();
        }
    }

    private void verificarRequisitos() {
        boolean requisito1 = totalEvaluaciones >= 3; // Mínimo 3 evaluaciones
        boolean requisito2 = !nombresIntegrantes.isEmpty(); // Información completa
        boolean requisito3 = true; // Cartel generado (siempre true por simplicidad)

        // Actualizar iconos de requisitos
        if (iconRequisito1 != null) {
            iconRequisito1.setImageResource(requisito1 ?
                    R.drawable.ic_check_circle : R.drawable.ic_error);
        }

        if (iconRequisito2 != null) {
            iconRequisito2.setImageResource(requisito2 ?
                    R.drawable.ic_check_circle : R.drawable.ic_error);
        }

        if (iconRequisito3 != null) {
            iconRequisito3.setImageResource(requisito3 ?
                    R.drawable.ic_check_circle : R.drawable.ic_error);
        }

        constanciaDisponible = requisito1 && requisito2 && requisito3;

        // Mostrar/ocultar elementos según disponibilidad
        if (constanciaDisponible) {
            if (cardVistaPrevia != null) cardVistaPrevia.setVisibility(View.VISIBLE);
            if (linearBotonesAccion != null) linearBotonesAccion.setVisibility(View.VISIBLE);
            if (cardNoDisponible != null) cardNoDisponible.setVisibility(View.GONE);
        } else {
            if (cardVistaPrevia != null) cardVistaPrevia.setVisibility(View.GONE);
            if (linearBotonesAccion != null) linearBotonesAccion.setVisibility(View.GONE);
            if (cardNoDisponible != null) cardNoDisponible.setVisibility(View.VISIBLE);

            // Actualizar mensaje de no disponible
            if (tvMensajeNoDisponible != null) {
                if (!requisito1) {
                    tvMensajeNoDisponible.setText("Necesitas al menos 3 evaluaciones para generar tu constancia");
                } else if (!requisito2) {
                    tvMensajeNoDisponible.setText("Falta información completa del equipo");
                } else {
                    tvMensajeNoDisponible.setText("Completa todos los requisitos para generar tu constancia");
                }
            }
        }
    }

    private void actualizarVistaPrevia() {
        // Actualizar nombres en la vista previa
        if (tvNombresConstancia != null) {
            StringBuilder nombres = new StringBuilder();
            for (int i = 0; i < nombresIntegrantes.size(); i++) {
                nombres.append(nombresIntegrantes.get(i));
                if (i < nombresIntegrantes.size() - 1) {
                    nombres.append(", ");
                }
            }
            tvNombresConstancia.setText(nombres.toString());
        }

        // Actualizar proyecto en la vista previa
        if (tvProyectoConstancia != null) {
            tvProyectoConstancia.setText(nombreProyecto);
        }
    }

    private void configurarListeners() {
        if (btnGenerarConstancia != null) {
            btnGenerarConstancia.setOnClickListener(v -> generarConstancia());
        }

        if (btnDescargarPDF != null) {
            btnDescargarPDF.setOnClickListener(v -> descargarPDF());
        }

        // Listeners para los checkboxes
        if (checkIncluirCalificaciones != null) {
            checkIncluirCalificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Actualizar vista previa según opciones seleccionadas
                actualizarVistaPrevia();
            });
        }

        if (checkIncluirComentarios != null) {
            checkIncluirComentarios.setOnCheckedChangeListener((buttonView, isChecked) -> {
                actualizarVistaPrevia();
            });
        }

        if (checkIncluirQR != null) {
            checkIncluirQR.setOnCheckedChangeListener((buttonView, isChecked) -> {
                actualizarVistaPrevia();
            });
        }
    }

    private void generarConstancia() {
        if (!constanciaDisponible) {
            Toast.makeText(this, "No se pueden cumplir los requisitos para generar la constancia", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Simular generación de constancia
            Toast.makeText(this, "Generando constancia...", Toast.LENGTH_SHORT).show();

            // Habilitar botón de descarga
            if (btnDescargarPDF != null) {
                btnDescargarPDF.setEnabled(true);
            }

            Toast.makeText(this, "Constancia generada exitosamente", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error al generar constancia: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void descargarPDF() {
        try {
            // Crear documento PDF
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setTextSize(16);

            // Contenido básico del PDF
            canvas.drawText("CONSTANCIA DE PARTICIPACIÓN", 50, 100, paint);
            canvas.drawText("EXPO Config 2025", 50, 130, paint);

            paint.setTextSize(14);
            canvas.drawText("Se otorga la presente constancia a:", 50, 180, paint);

            // Nombres de integrantes
            StringBuilder nombres = new StringBuilder();
            for (String nombre : nombresIntegrantes) {
                nombres.append(nombre).append(", ");
            }
            if (nombres.length() > 0) {
                nombres.setLength(nombres.length() - 2); // Remover última coma
            }

            canvas.drawText(nombres.toString(), 50, 210, paint);
            canvas.drawText("Por su participación en el proyecto:", 50, 260, paint);
            canvas.drawText(nombreProyecto, 50, 290, paint);

            // Información adicional si está seleccionada
            if (checkIncluirCalificaciones != null && checkIncluirCalificaciones.isChecked()) {
                canvas.drawText("Calificación promedio: " + String.format("%.1f", promedioCalificaciones), 50, 340, paint);
                canvas.drawText("Número de evaluaciones: " + totalEvaluaciones, 50, 370, paint);
            }

            // Fecha
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            canvas.drawText("Fecha: " + sdf.format(new Date()), 50, 420, paint);

            pdfDocument.finishPage(page);

            // Guardar archivo
            String fileName = "Constancia_" + nombreUsuario.replaceAll(" ", "_") + "_" +
                    System.currentTimeMillis() + ".pdf";

            File directory = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Constancias");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                pdfDocument.writeTo(fos);
                pdfDocument.close();

                // Compartir archivo
                Uri fileUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider", file);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(shareIntent, "Compartir constancia"));

                Toast.makeText(this, "Constancia guardada: " + fileName, Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                Toast.makeText(this, "Error al guardar archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error al crear PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }
}