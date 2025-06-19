package com.example.unitylab_expoconfig.ui.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EstudianteActivity extends AppCompatActivity {
    private static final String TAG = "EstudianteActivity";

    private int idUsuarioActual;
    private String tipoUsuario;
    private String nombreUsuario;
    private String boleta; // Nuevo campo para la boleta del estudiante

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_estudiante);

        // Obtener datos del intent
        Intent intent = getIntent();
        if (intent != null) {
            idUsuarioActual = intent.getIntExtra("idUsuario", -1);
            tipoUsuario = intent.getStringExtra("tipoUsuario");
            nombreUsuario = intent.getStringExtra("nombreUsuario");
            boleta = intent.getStringExtra("boleta"); // Obtener boleta si está disponible
        }

        // Configurar elementos de la UI
        configurarBienvenida();
        configurarCards();
        configurarAccionesRapidas();
        configurarEstadoEquipo();
        configurarBotonCerrarSesion();
    }

    private void configurarBienvenida() {
        TextView tvBienvenida = findViewById(R.id.tvBienvenidaAlumno);
        TextView tvBoleta = findViewById(R.id.tvBoleta);

        if (tvBienvenida != null && nombreUsuario != null) {
            String mensajeBienvenida = "¡Bienvenido, " + nombreUsuario + "!";
            tvBienvenida.setText(mensajeBienvenida);
        }

        if (tvBoleta != null && boleta != null) {
            tvBoleta.setText("Boleta: " + boleta);
        }
    }

    private void configurarCards() {
        CardView cardRegistrarEquipo = findViewById(R.id.cardRegistrarEquipo);
        CardView cardGenerarCartel = findViewById(R.id.cardGenerarCartel);
        CardView cardConstancia = findViewById(R.id.cardConstancia);
        CardView cardEvaluarProyectos = findViewById(R.id.cardEvaluarProyectos);

        // Configurar listeners para cada card
        if (cardRegistrarEquipo != null) {
            cardRegistrarEquipo.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("nombreUsuario", nombreUsuario);
                startActivity(intent);
            });
        }

        if (cardGenerarCartel != null) {
            cardGenerarCartel.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                startActivity(intent);
            });
        }

        if (cardConstancia != null) {
            cardConstancia.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                startActivity(intent);
            });
        }

        if (cardEvaluarProyectos != null) {
            cardEvaluarProyectos.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                startActivity(intent);
            });
        }
    }

    private void configurarAccionesRapidas() {
        View linearVerAgenda = findViewById(R.id.linearVerAgenda);
        View linearProyectosDisponibles = findViewById(R.id.linearProyectosDisponibles);

        if (linearVerAgenda != null) {
            linearVerAgenda.setOnClickListener(v -> {
                Intent intent = new Intent(this, AgendaActivity.class);
                startActivity(intent);
            });
        }

        if (linearProyectosDisponibles != null) {
            linearProyectosDisponibles.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                startActivity(intent);
            });
        }
    }

    private void configurarEstadoEquipo() {
        CardView cardMiEquipo = findViewById(R.id.cardMiEquipo);
        CardView cardEstadoEquipo = findViewById(R.id.cardEstadoEquipo);
        TextView tvMensajeEstado = findViewById(R.id.tvMensajeEstado);
        ImageView ivEstadoParticipacion = findViewById(R.id.ivEstadoParticipacion);
        TextView tvEstadoParticipacion = findViewById(R.id.tvEstadoParticipacion);

        // Aquí deberías verificar en tu base de datos si el estudiante tiene equipo registrado
        boolean tieneEquipo = false; // Cambiar según la lógica de tu aplicación

        if (tieneEquipo) {
            cardMiEquipo.setVisibility(View.VISIBLE);
            cardEstadoEquipo.setVisibility(View.GONE);

            // Configurar datos del equipo (deberías obtenerlos de tu base de datos)
            RecyclerView recyclerIntegrantes = findViewById(R.id.recyclerIntegrantes);
            // Configurar adaptador para los integrantes del equipo

        } else {
            cardMiEquipo.setVisibility(View.GONE);
            cardEstadoEquipo.setVisibility(View.VISIBLE);

            // Personalizar mensaje según el estado
            tvMensajeEstado.setText("Aún no te has registrado en ningún proyecto. ¡Busca un proyecto disponible y únete con tu equipo!");
            ivEstadoParticipacion.setImageResource(R.drawable.ic_warning);
            tvEstadoParticipacion.setText("Sin equipo");
        }
    }

    private void configurarBotonCerrarSesion() {
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionAlumno);
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> {
                // Lógica para cerrar sesión
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar estado del equipo cada vez que la actividad se reanude
        configurarEstadoEquipo();
    }
}