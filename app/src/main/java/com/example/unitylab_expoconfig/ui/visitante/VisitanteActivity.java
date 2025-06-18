package com.example.unitylab_expoconfig.ui.visitante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;

import java.util.ArrayList;
import java.util.List;

public class VisitanteActivity extends AppCompatActivity {

    private RecyclerView recyclerProyectosDestacados;
    private View layoutSinProyectosDestacados;
    private TextView tvTotalProyectosVisitante, tvTotalEquiposVisitante, tvMisEvaluaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_visitante); // Asegúrate que el XML se llame así

        // Inicializar vistas
        initViews();

        // Configurar estadísticas (valores de ejemplo)
        setupStats();

        // Configurar RecyclerView de proyectos destacados
        //setupProyectosDestacados();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        recyclerProyectosDestacados = findViewById(R.id.recyclerProyectosDestacados);
        layoutSinProyectosDestacados = findViewById(R.id.layoutSinProyectosDestacados);
        tvTotalProyectosVisitante = findViewById(R.id.tvTotalProyectosVisitante);
        tvTotalEquiposVisitante = findViewById(R.id.tvTotalEquiposVisitante);
        tvMisEvaluaciones = findViewById(R.id.tvMisEvaluaciones);
    }

    private void setupStats() {
        // Estos valores deberían venir de tu backend/database
        tvTotalProyectosVisitante.setText("24");
        tvTotalEquiposVisitante.setText("15");
        tvMisEvaluaciones.setText("3");
    }

    /*private void setupProyectosDestacados() {
        List<Proyecto> proyectos = getProyectosDestacados(); // Obtener datos

        if (proyectos.isEmpty()) {
            recyclerProyectosDestacados.setVisibility(View.GONE);
            layoutSinProyectosDestacados.setVisibility(View.VISIBLE);
        } else {
            recyclerProyectosDestacados.setVisibility(View.VISIBLE);
            layoutSinProyectosDestacados.setVisibility(View.GONE);

            ProyectoAdapter adapter = new ProyectoAdapter(proyectos, this, true);
            recyclerProyectosDestacados.setLayoutManager(new LinearLayoutManager(this));
            recyclerProyectosDestacados.setAdapter(adapter);
        }
    }*/

    /*private List<Proyecto> getProyectosDestacados() {
        // Esto es solo datos de ejemplo - deberías obtenerlos de tu backend
        List<Proyecto> proyectos = new ArrayList<>();
        proyectos.add(new Proyecto("Sistema de Riego Automático", "4°A Electrónica", 4.5f));
        proyectos.add(new Proyecto("App para Control Parental", "5°B Informática", 4.2f));
        return proyectos;
    }*/

    private void setupListeners() {
        // Card de funciones principales
        CardView cardEvaluarProyectos = findViewById(R.id.cardEvaluarProyectos);
        CardView cardVerAgenda = findViewById(R.id.cardVerAgenda);

        // Items de información útil
        //View btnComoEvaluar = findViewById(R.id.linearComoEvaluar);
        View btnMapaUbicaciones = findViewById(R.id.linearMapaUbicaciones);
        View btnFeedbackEvento = findViewById(R.id.linearFeedbackEvento);

        // Botón ver todos proyectos
        TextView btnVerTodosProyectos = findViewById(R.id.btnVerTodosProyectosVisitante);

        // Botón salir
        Button btnSalir = findViewById(R.id.btnSalir);

        // Listeners
        /*cardEvaluarProyectos.setOnClickListener(v -> {
            Intent intent = new Intent(this, EvaluarProyectosActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });*/

        /*cardVerAgenda.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgendaVisitanteActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });*/

        /*btnComoEvaluar.setOnClickListener(v -> {
            Toast.makeText(this, "Guía de evaluación abierta", Toast.LENGTH_SHORT).show();
            // Abrir PDF/webview con instrucciones
        });

        btnMapaUbicaciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapaActivity.class);
            startActivity(intent);
        });

        btnFeedbackEvento.setOnClickListener(v -> {
            Intent intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);
        });

        btnVerTodosProyectos.setOnClickListener(v -> {
            Intent intent = new Intent(this, TodosProyectosActivity.class);
            startActivity(intent);
        });*/

        btnSalir.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}