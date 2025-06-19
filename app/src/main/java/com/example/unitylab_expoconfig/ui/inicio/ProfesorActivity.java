package com.example.unitylab_expoconfig.ui.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.ui.proyectos.CrearProyectoActivity;
import com.example.unitylab_expoconfig.ui.proyectos.ListaProyectosActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfesorActivity extends AppCompatActivity {
    private static final String TAG = "ProfesorActivity";

    private int idUsuarioActual;
    private String tipoUsuario;
    private String nombreUsuario;
    private FloatingActionButton fabAgregarProyecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_profesor);

        // Obtener datos del intent
        Intent intent = getIntent();
        if (intent != null) {
            idUsuarioActual = intent.getIntExtra("idUsuario", -1);
            tipoUsuario = intent.getStringExtra("tipoUsuario");
            nombreUsuario = intent.getStringExtra("nombreUsuario");
        }

        // Configurar elementos de la UI
        configurarBienvenida();
        configurarCards();
        configurarAccionesAdicionales();
        configurarProyectosRecientes();
        configurarBotonCerrarSesion();
        configurarFAB();
    }

    private void configurarBienvenida() {
        TextView tvBienvenida = findViewById(R.id.tvBienvenidaProfesor);
        if (tvBienvenida != null && nombreUsuario != null) {
            String mensajeBienvenida = "¡Bienvenido, " + nombreUsuario + "!";
            tvBienvenida.setText(mensajeBienvenida);

            // Opcional: También puedes mostrar el tipo de usuario si lo deseas
            TextView tvTipoUsuario = findViewById(R.id.textViewTipoUsuario); // Asegúrate de tener este TextView en tu layout
            if (tvTipoUsuario != null) {
                String tipoMensaje = tipoUsuario.equals("estudiante") ? "Estudiante" : "Profesor";
                tvTipoUsuario.setText(tipoMensaje);
            }
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
            });
        }

        if (cardMisProyectos != null) {
            cardMisProyectos.setOnClickListener(v -> {
                // Lógica para Mis Proyectos
                Intent intent = new Intent(this, ListaProyectosActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                startActivity(intent);
            });
        }

        if (cardEvaluarEquipos != null) {
            cardEvaluarEquipos.setOnClickListener(v -> {
                // Lógica para Evaluar Equipos
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        }

        if (cardAgenda != null) {
            cardAgenda.setOnClickListener(v -> {
                // Lógica para Agenda
                Intent intent = new Intent(this, AgendaActivity.class);
                startActivity(intent);
            });
        }
    }

    private void configurarAccionesAdicionales() {
        View linearGenerarClaves = findViewById(R.id.linearGenerarClaves);
        View linearGestionarEquipos = findViewById(R.id.linearGestionarEquipos);
        View linearCambiarPassword = findViewById(R.id.linearCambiarPassword);

        if (linearGenerarClaves != null) {
            linearGenerarClaves.setOnClickListener(v -> {
                // Lógica para Generar Claves
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        }

        if (linearGestionarEquipos != null) {
            linearGestionarEquipos.setOnClickListener(v -> {
                // Lógica para Gestionar Equipos
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        }

        if (linearCambiarPassword != null) {
            linearCambiarPassword.setOnClickListener(v -> {
                // Lógica para Cambiar Contraseña
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        }
    }

    private void configurarProyectosRecientes() {
        RecyclerView recyclerProyectos = findViewById(R.id.recyclerProyectosRecientes);
        TextView btnVerTodos = findViewById(R.id.btnVerTodosProyectos);

        // Aquí deberías configurar el RecyclerView con un adaptador
        // recyclerProyectos.setAdapter(...);

        if (btnVerTodos != null) {
            btnVerTodos.setOnClickListener(v -> {
                Intent intent = new Intent(this, ListaProyectosActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                startActivity(intent);
            });
        }
    }

    private void configurarBotonCerrarSesion() {
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionProfesor);
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

    private void configurarFAB() {
        fabAgregarProyecto = findViewById(R.id.fabAgregarProyecto);
        Log.d(TAG, "Configurando FAB...");
        try {
            if (fabAgregarProyecto != null) {
                // Ocultar el FAB si no hay sesión activa (idUsuarioActual == -1) O si el usuario es estudiante
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
                        startActivity(intent);
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
}