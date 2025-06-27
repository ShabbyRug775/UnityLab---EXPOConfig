package com.example.unitylab_expoconfig.ui.visitante;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;

import java.util.Locale;

public class DetalleProyectoVisitanteActivity extends AppCompatActivity {

    private DbmsSQLiteHelper dbHelper;
    private int idEquipo;
    private int idVisitante;
    private TextView tvNombreProyecto, tvNombreEquipo, tvDescripcion, tvLugar,
            tvPromedio, tvCantEvaluaciones;
    private Button btnEvaluar;

    private String nombreEquipo,nombreProyecto,descripcion,lugar;
    private double promedio ;
    private int cantEval,calificacion ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_proyecto_visitante);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle del Proyecto");
        }

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Obtener datos del intent
        idEquipo = getIntent().getIntExtra("idEquipo", -1);
        idVisitante = getIntent().getIntExtra("idVisitante", -1);

        // Inicializar vistas
        initViews();

        // Cargar datos del proyecto
        cargarDatosProyecto();

        // Configurar botón de evaluación
        configurarBotonEvaluar();
    }

    private void initViews() {
        tvNombreProyecto = findViewById(R.id.tvNombreProyecto);
        tvNombreEquipo = findViewById(R.id.tvNombreEquipo);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvLugar = findViewById(R.id.tvLugar);
        tvPromedio = findViewById(R.id.tvPromedio);
        tvCantEvaluaciones = findViewById(R.id.tvCantEvaluaciones);
        btnEvaluar = findViewById(R.id.btnEvaluar);
    }

    private void cargarDatosProyecto() {
        try {
            Cursor cursor = dbHelper.obtenerEquipoPorId(idEquipo);
            if (cursor != null && cursor.moveToFirst()) {
                 nombreEquipo = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                 nombreProyecto = cursor.getString(cursor.getColumnIndexOrThrow("NombreProyecto"));
                 descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                 lugar = cursor.getString(cursor.getColumnIndexOrThrow("Lugar"));
                 promedio = cursor.getDouble(cursor.getColumnIndexOrThrow("Promedio"));
                 cantEval = cursor.getInt(cursor.getColumnIndexOrThrow("CantEval"));

                tvNombreProyecto.setText(nombreProyecto);
                tvNombreEquipo.setText(nombreEquipo);
                tvDescripcion.setText(descripcion);
                tvLugar.setText("📍 " + lugar);

                if (cantEval > 0) {
                    tvPromedio.setText(String.format(Locale.getDefault(), "%.1f ⭐", promedio));
                    tvCantEvaluaciones.setText(String.format(Locale.getDefault(),
                            "%d evaluación%s", cantEval, cantEval != 1 ? "es" : ""));
                } else {
                    tvPromedio.setText("Sin evaluar");
                    tvCantEvaluaciones.setText("Sé el primero en evaluar");
                }

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar datos del proyecto", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarBotonEvaluar() {
        // Verificar si ya evaluó este proyecto
        boolean yaEvaluo = dbHelper.yaEvaluoEquipo(idEquipo, null, null, idVisitante);

        if (yaEvaluo) {
            btnEvaluar.setText("Ver mi evaluación");
            btnEvaluar.setOnClickListener(v -> mostrarEvaluacionExistente());
        } else {
            btnEvaluar.setText("Evaluar proyecto");
            btnEvaluar.setOnClickListener(v -> mostrarDialogEvaluacion());
        }
    }

    private void mostrarDialogEvaluacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_evaluar_equipo, null);

        TextView tvNombreEquipo = dialogView.findViewById(R.id.tvNombreEquipo);
        TextView tvProyectoEquipo = dialogView.findViewById(R.id.tvProyectoEquipo);
        TextView tvDescripcionEquipo = dialogView.findViewById(R.id.tvDescripcionEquipo);
        RatingBar ratingBarCalificacion = dialogView.findViewById(R.id.ratingBarCalificacion);
        EditText editComentarios = dialogView.findViewById(R.id.editComentarios);
        TextView tvCalificacionTexto = dialogView.findViewById(R.id.tvCalificacionTexto);

        tvNombreEquipo.setText(nombreEquipo);
        tvProyectoEquipo.setText(nombreProyecto);
        tvDescripcionEquipo.setText(descripcion);

        // Configurar rating bar
        ratingBarCalificacion.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                String[] textos = {"", "Muy Malo", "Malo", "Regular", "Bueno", "Excelente"};
                calificacion = Math.round(rating);
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

                    enviarEvaluacion(idEquipo, calificacion, comentarios);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void enviarEvaluacion(int idEquipo, int calificacion, String comentarios) {
        try {
            long resultado = dbHelper.insertarEvaluacionVisitante(calificacion, comentarios, idEquipo, idVisitante);

            if (resultado != -1) {
                Toast.makeText(this, "Evaluación enviada exitosamente", Toast.LENGTH_SHORT).show();
                // Actualizar promedio del equipo
                actualizarPromedioEquipo();
                // Recargar datos

                //cargarEstadisticas();
                //cargarEquipos();
            } else {
                Toast.makeText(this, "Error al enviar evaluación", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al enviar evaluación", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarEvaluacionExistente() {
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

    private void actualizarPromedioEquipo() {
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
}