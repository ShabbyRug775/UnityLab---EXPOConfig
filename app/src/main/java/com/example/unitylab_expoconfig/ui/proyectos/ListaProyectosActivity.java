package com.example.unitylab_expoconfig.ui.proyectos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.ProyectoBD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ListaProyectosActivity extends AppCompatActivity {
    private static final String TAG = "ListaProyectos";

    private ListView listViewProyectos;
    private FloatingActionButton fabAgregarProyecto;
    private Spinner spinnerFiltro;
    private LinearLayout tvSinProyectos;  // CAMBIADO A LinearLayout
    private DbmsSQLiteHelper dbHelper;
    private ProyectosAdapter adapter;
    private List<Proyecto> listaProyectos;
    private int idUsuarioActual;
    private String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "=== INICIANDO ListaProyectosActivity ===");

        try {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "super.onCreate() completado");

            // VERIFICAR SI EL LAYOUT EXISTE
            setContentView(R.layout.activity_lista_proyectos);
            Log.d(TAG, "setContentView() completado");

            // Obtener datos del intent
            Intent intent = getIntent();
            idUsuarioActual = intent.getIntExtra("idUsuario", -1);
            tipoUsuario = intent.getStringExtra("tipoUsuario");

            Log.d(TAG, "Datos recibidos: ID=" + idUsuarioActual + ", Tipo=" + tipoUsuario);

            // Verificar toolbar OPCIONAL (comentar si no existe en el layout)
            try {
                Toolbar toolbar = findViewById(R.id.toolbar);
                if (toolbar != null) {
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setTitle("Proyectos");
                    Log.d(TAG, "Toolbar configurado");
                } else {
                    Log.w(TAG, "Toolbar no encontrado en el layout");
                }
            } catch (Exception e) {
                Log.w(TAG, "Error configurando toolbar: " + e.getMessage());
            }

            dbHelper = new DbmsSQLiteHelper(this);
            listaProyectos = new ArrayList<>();
            Log.d(TAG, "Variables inicializadas");

            inicializarVistas();
            Log.d(TAG, "Vistas inicializadas");

            configurarListView();
            Log.d(TAG, "ListView configurado");

            configurarFAB();
            Log.d(TAG, "FAB configurado");

            cargarProyectos();
            Log.d(TAG, "Proyectos cargados");

            Log.d(TAG, "=== ListaProyectosActivity CREADA EXITOSAMENTE ===");

        } catch (Exception e) {
            Log.e(TAG, "ERROR EN onCreate(): " + e.getMessage());
            e.printStackTrace();

            // Mostrar error al usuario
            Toast.makeText(this, "Error al cargar la pantalla: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish(); // Cerrar la actividad si hay error crítico
        }
    }

    private void inicializarVistas() {
        Log.d(TAG, "Inicializando vistas...");
        try {
            // BUSCAR VISTAS BÁSICAS PRIMERO
            listViewProyectos = findViewById(R.id.listViewProyectos);
            if (listViewProyectos == null) {
                throw new RuntimeException("listViewProyectos no encontrado en el layout");
            }
            Log.d(TAG, "ListView encontrado");

            // VISTAS OPCIONALES (comentar si no existen)
            try {
                fabAgregarProyecto = findViewById(R.id.fabAgregarProyecto);
                Log.d(TAG, "FAB " + (fabAgregarProyecto != null ? "encontrado" : "no encontrado"));
            } catch (Exception e) {
                Log.w(TAG, "FAB no encontrado: " + e.getMessage());
            }

            try {
                spinnerFiltro = findViewById(R.id.spinnerFiltro);
                Log.d(TAG, "Spinner " + (spinnerFiltro != null ? "encontrado" : "no encontrado"));
            } catch (Exception e) {
                Log.w(TAG, "Spinner no encontrado: " + e.getMessage());
            }

            try {
                tvSinProyectos = findViewById(R.id.tvSinProyectos);  // Ahora es LinearLayout
                Log.d(TAG, "LinearLayout sin proyectos " + (tvSinProyectos != null ? "encontrado" : "no encontrado"));
            } catch (Exception e) {
                Log.w(TAG, "LinearLayout sin proyectos no encontrado: " + e.getMessage());
            }

            Log.d(TAG, "Vistas inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar vistas: " + e.getMessage());
            throw e; // Re-lanzar para que se maneje en onCreate
        }
    }

    private void configurarListView() {
        Log.d(TAG, "Configurando ListView...");
        try {
            adapter = new ProyectosAdapter(this, listaProyectos);
            listViewProyectos.setAdapter(adapter);

            listViewProyectos.setOnItemClickListener((parent, view, position, id) -> {
                Log.d(TAG, "Item clickeado en posición: " + position);
                try {
                    Proyecto proyecto = listaProyectos.get(position);
                    verDetallesProyecto(proyecto);
                } catch (Exception e) {
                    Log.e(TAG, "Error al hacer click en item: " + e.getMessage());
                    Toast.makeText(this, "Error al abrir proyecto", Toast.LENGTH_SHORT).show();
                }
            });

            Log.d(TAG, "ListView configurado exitosamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar ListView: " + e.getMessage());
            // No re-lanzar, continuar sin ListView clickeable
        }
    }

    private void configurarFAB() {
        Log.d(TAG, "Configurando FAB...");
        try {
            if (fabAgregarProyecto != null) {
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

    private void cargarProyectos() {
        Log.d(TAG, "Cargando proyectos...");
        try {
            Cursor cursor = dbHelper.obtenerTodosProyectos();
            actualizarListaProyectos(cursor);
            if (cursor != null) {
                cursor.close();
            }
            Log.d(TAG, "Proyectos cargados exitosamente. Total: " + listaProyectos.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar proyectos: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar proyectos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarListaProyectos(Cursor cursor) {
        Log.d(TAG, "Actualizando lista de proyectos...");
        try {
            listaProyectos.clear();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Proyecto proyecto = new Proyecto();
                    proyecto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ID)));
                    proyecto.setNombreProyecto(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_NOMBRE_PROYECTO)));
                    proyecto.setNombreEquipo(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_NOMBRE_EQUIPO)));
                    proyecto.setMateria(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_MATERIA)));
                    proyecto.setGrupo(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_GRUPO)));
                    proyecto.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ESTADO)));

                    listaProyectos.add(proyecto);
                    Log.d(TAG, "Proyecto agregado: " + proyecto.getNombreProyecto());
                } while (cursor.moveToNext());
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            // Mostrar/ocultar LinearLayout de sin proyectos
            if (tvSinProyectos != null) {
                if (listaProyectos.isEmpty()) {
                    tvSinProyectos.setVisibility(View.VISIBLE);
                    listViewProyectos.setVisibility(View.GONE);
                    Log.d(TAG, "Mostrando mensaje 'sin proyectos'");
                } else {
                    tvSinProyectos.setVisibility(View.GONE);
                    listViewProyectos.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Ocultando mensaje 'sin proyectos'");
                }
            }

            Log.d(TAG, "Lista actualizada con " + listaProyectos.size() + " proyectos");
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar lista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verDetallesProyecto(Proyecto proyecto) {
        Log.d(TAG, "Abriendo detalles del proyecto: " + proyecto.getNombreProyecto());
        try {
            Intent intent = new Intent(this, DetalleProyectoActivity.class);
            intent.putExtra("idProyecto", proyecto.getId());
            intent.putExtra("idUsuario", idUsuarioActual);
            intent.putExtra("tipoUsuario", tipoUsuario);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error al abrir detalles: " + e.getMessage());
            Toast.makeText(this, "Error al abrir detalles del proyecto", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Simplemente cerrar la actividad actual
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() - Recargando proyectos");
        cargarProyectos();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }
}