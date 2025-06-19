package com.example.unitylab_expoconfig.ui.cartel;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ImpresionActivity extends AppCompatActivity implements CartelAdapter.OnCartelClickListener {

    private Toolbar toolbar;
    private TextView tvTotalCarteles, tvCartelesImpresos, tvCartelesPendientes;
    private Spinner spinnerFiltroEstado;
    private Button btnMarcarTodosImpresos;
    private RecyclerView recyclerCarteles;
    private LinearLayout layoutSinCarteles;
    private FloatingActionButton fabActualizar;

    private CartelAdapter cartelAdapter;
    private List<Cartel> listaCarteles = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cola_impresion);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cola de Impresión");
        }

        // Inicializar vistas
        initViews();

        // Configurar spinner de filtros
        setupSpinner();

        // Configurar RecyclerView
        setupRecyclerView();

        // Configurar listeners
        setupListeners();

        // Cargar datos desde Firestore
        cargarCarteles();
    }

    private void initViews() {
        tvTotalCarteles = findViewById(R.id.tvTotalCarteles);
        tvCartelesImpresos = findViewById(R.id.tvCartelesImpresos);
        tvCartelesPendientes = findViewById(R.id.tvCartelesPendientes);
        spinnerFiltroEstado = findViewById(R.id.spinnerFiltroEstado);
        btnMarcarTodosImpresos = findViewById(R.id.btnMarcarTodosImpresos);
        recyclerCarteles = findViewById(R.id.recyclerCarteles);
        layoutSinCarteles = findViewById(R.id.layoutSinCarteles);
        fabActualizar = findViewById(R.id.fabActualizar);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filtros_estado_carteles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroEstado.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        cartelAdapter = new CartelAdapter(this, listaCarteles, this);
        recyclerCarteles.setLayoutManager(new LinearLayoutManager(this));
        recyclerCarteles.setAdapter(cartelAdapter);
    }

    private void setupListeners() {
        btnMarcarTodosImpresos.setOnClickListener(v -> marcarTodosComoImpresos());
        fabActualizar.setOnClickListener(v -> cargarCarteles());

        spinnerFiltroEstado.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filtrarCarteles(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    @Override
    public void onVistaPreviaClick(Cartel cartel) {
        // Abrir actividad de vista previa del cartel
        // Intent intent = new Intent(this, VistaPreviaCartelActivity.class);
        // intent.putExtra("url_archivo", cartel.getUrlArchivo());
        // startActivity(intent);
    }

    @Override
    public void onEstadoChanged(Cartel cartel, boolean isImpreso) {
        String nuevoEstado = isImpreso ? "impreso" : "pendiente";
        actualizarEstadoCartel(cartel, nuevoEstado);
    }

    private void cargarCarteles() {
        db.collection("carteles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaCarteles.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Cartel cartel = document.toObject(Cartel.class);
                            cartel.setId(document.getId());
                            listaCarteles.add(cartel);
                        }
                        actualizarUI();
                    }
                });
    }

    private void filtrarCarteles(String filtro) {
        List<Cartel> listaFiltrada = new ArrayList<>();

        if (filtro.equals("Todos")) {
            listaFiltrada.addAll(listaCarteles);
        } else {
            for (Cartel cartel : listaCarteles) {
                if (cartel.getEstado().equalsIgnoreCase(filtro)) {
                    listaFiltrada.add(cartel);
                }
            }
        }

        cartelAdapter.updateData(listaFiltrada);
        actualizarEstadisticas(listaFiltrada);
    }

    private void marcarTodosComoImpresos() {
        for (Cartel cartel : listaCarteles) {
            if (!cartel.getEstado().equals("impreso")) {
                actualizarEstadoCartel(cartel, "impreso");
            }
        }
    }

    private void actualizarEstadoCartel(Cartel cartel, String nuevoEstado) {
        db.collection("carteles").document(cartel.getId())
                .update("estado", nuevoEstado)
                .addOnSuccessListener(aVoid -> cargarCarteles());
    }

    private void actualizarUI() {
        actualizarEstadisticas(listaCarteles);
        cartelAdapter.updateData(listaCarteles);

        if (listaCarteles.isEmpty()) {
            recyclerCarteles.setVisibility(View.GONE);
            layoutSinCarteles.setVisibility(View.VISIBLE);
        } else {
            recyclerCarteles.setVisibility(View.VISIBLE);
            layoutSinCarteles.setVisibility(View.GONE);
        }
    }

    private void actualizarEstadisticas(List<Cartel> carteles) {
        int total = carteles.size();
        int impresos = (int) carteles.stream()
                .filter(c -> c.getEstado().equals("impreso"))
                .count();

        tvTotalCarteles.setText(String.valueOf(total));
        tvCartelesImpresos.setText(String.valueOf(impresos));
        tvCartelesPendientes.setText(String.valueOf(total - impresos));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}