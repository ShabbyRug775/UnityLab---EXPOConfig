package com.example.unitylab_expoconfig.ui.inicio;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AgendaActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CardView cardViewHeader;
    private TextView tvFechasEvento;
    private TextView tvDias, tvHoras, tvMinutos;
    private Spinner spinnerDia, spinnerTipo;
    private RecyclerView recyclerEventos;
    private FloatingActionButton fabVistaCalendario, fabActualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_exposicion); // Asume que el XML se llama activity_agenda.xml

        // Configurar toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar vistas
        //coordinatorLayout = findViewById(R.id.coordinatorLayout);
        //appBarLayout = findViewById(R.id.appBarLayout);
        //cardViewHeader = findViewById(R.id.cardViewHeader);
        tvFechasEvento = findViewById(R.id.tvFechasEvento);
        tvDias = findViewById(R.id.tvDias);
        tvHoras = findViewById(R.id.tvHoras);
        tvMinutos = findViewById(R.id.tvMinutos);
        spinnerDia = findViewById(R.id.spinnerDia);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        recyclerEventos = findViewById(R.id.recyclerEventos);
        fabVistaCalendario = findViewById(R.id.fabVistaCalendario);
        fabActualizar = findViewById(R.id.fabActualizar);

        // Configurar eventos y lógica adicional aquí
        setupUI();
    }

    private void setupUI() {
        // Configurar los spinners, RecyclerView, etc.
        // Por ejemplo:
        // setupSpinners();
        // setupRecyclerView();
        // setupCountdownTimer();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}