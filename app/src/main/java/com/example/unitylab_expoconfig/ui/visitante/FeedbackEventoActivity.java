package com.example.unitylab_expoconfig.ui.visitante;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;

public class FeedbackEventoActivity extends AppCompatActivity {

    private DbmsSQLiteHelper dbHelper;
    private int idVisitante;

    // Views del formulario
    private RatingBar ratingEventoGeneral, ratingOrganizacion, ratingCalidadProyectos,
            ratingInstalaciones, ratingAtencion;
    private EditText editAspectosMejor, editAreasMejora, editSugerencias, editComentarios;
    private Button btnEnviarFeedback, btnCancelar;
    private LinearLayout layoutFormulario, layoutAgradecimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_evento);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Feedback del Evento");
        }

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Obtener datos del intent
        idVisitante = getIntent().getIntExtra("idVisitante", -1);

        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        // RatingBars
        ratingEventoGeneral = findViewById(R.id.ratingEventoGeneral);
        ratingOrganizacion = findViewById(R.id.ratingOrganizacion);
        ratingCalidadProyectos = findViewById(R.id.ratingCalidadProyectos);
        ratingInstalaciones = findViewById(R.id.ratingInstalaciones);
        ratingAtencion = findViewById(R.id.ratingAtencion);

        // EditTexts
        editAspectosMejor = findViewById(R.id.editAspectosMejor);
        editAreasMejora = findViewById(R.id.editAreasMejora);
        editSugerencias = findViewById(R.id.editSugerencias);
        editComentarios = findViewById(R.id.editComentarios);

        // Botones y layouts
        btnEnviarFeedback = findViewById(R.id.btnEnviarFeedback);
        btnCancelar = findViewById(R.id.btnCancelar);
        layoutFormulario = findViewById(R.id.layoutFormulario);
        layoutAgradecimiento = findViewById(R.id.layoutAgradecimiento);
    }

    private void setupListeners() {
        btnEnviarFeedback.setOnClickListener(v -> enviarFeedback());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void enviarFeedback() {
        // Validar calificaciones obligatorias
        if (ratingEventoGeneral.getRating() == 0 ||
                ratingOrganizacion.getRating() == 0 ||
                ratingCalidadProyectos.getRating() == 0) {

            Toast.makeText(this, "Por favor completa todas las calificaciones obligatorias (*)",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            // Crear string con todo el feedback
            StringBuilder feedback = new StringBuilder();
            feedback.append("FEEDBACK DEL EVENTO\n");
            feedback.append("===================\n");
            feedback.append("Evaluación general: ").append((int)ratingEventoGeneral.getRating()).append("/5\n");
            feedback.append("Organización: ").append((int)ratingOrganizacion.getRating()).append("/5\n");
            feedback.append("Calidad proyectos: ").append((int)ratingCalidadProyectos.getRating()).append("/5\n");
            feedback.append("Instalaciones: ").append((int)ratingInstalaciones.getRating()).append("/5\n");
            feedback.append("Atención: ").append((int)ratingAtencion.getRating()).append("/5\n\n");

            String aspectosMejor = editAspectosMejor.getText().toString().trim();
            if (!aspectosMejor.isEmpty()) {
                feedback.append("Aspectos que más gustaron:\n").append(aspectosMejor).append("\n\n");
            }

            String areasMejora = editAreasMejora.getText().toString().trim();
            if (!areasMejora.isEmpty()) {
                feedback.append("Áreas de mejora:\n").append(areasMejora).append("\n\n");
            }

            String sugerencias = editSugerencias.getText().toString().trim();
            if (!sugerencias.isEmpty()) {
                feedback.append("Sugerencias:\n").append(sugerencias).append("\n\n");
            }

            String comentarios = editComentarios.getText().toString().trim();
            if (!comentarios.isEmpty()) {
                feedback.append("Comentarios adicionales:\n").append(comentarios).append("\n");
            }

            // Guardar como una "evaluación" especial para el feedback del evento
            // Usando idEquipo = -1 para indicar que es feedback del evento
            long resultado = dbHelper.insertarEvaluacionVisitante(
                    (int)ratingEventoGeneral.getRating(),
                    feedback.toString(),
                    -1, // ID especial para feedback del evento
                    idVisitante
            );

            if (resultado != -1) {
                // Mostrar mensaje de agradecimiento
                layoutFormulario.setVisibility(View.GONE);
                layoutAgradecimiento.setVisibility(View.VISIBLE);

                // Auto-cerrar después de 3 segundos
                findViewById(android.R.id.content).postDelayed(() -> finish(), 3000);

            } else {
                Toast.makeText(this, "Error al enviar feedback", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar el feedback", Toast.LENGTH_SHORT).show();
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
}