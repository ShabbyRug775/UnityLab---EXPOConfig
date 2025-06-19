package com.example.unitylab_expoconfig.ui.proyectos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.ProyectoBD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListaProyectosActivity extends AppCompatActivity {
    private static final String TAG = "ListaProyectos";

    private ListView listViewProyectos;
    private FloatingActionButton fabAgregarProyecto;
    private LinearLayout tvSinProyectos;
    private DbmsSQLiteHelper dbHelper;
    private SQLiteDatabase db;
    private ProyectosAdapter adapter;
    private List<Proyecto> listaProyectos;
    private int idUsuarioActual;
    private String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_proyectos);

        // Inicializar la lista de proyectos ANTES de usarla
        listaProyectos = new ArrayList<>(); // <-- Esta línea es crucial

        // Obtener datos del usuario
        Intent intent = getIntent();
        idUsuarioActual = intent.getIntExtra("idUsuario", -1);
        tipoUsuario = intent.getStringExtra("tipoUsuario");

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mis Proyectos");
        }

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);
        db = dbHelper.getReadableDatabase();

        // Inicializar vistas
        listViewProyectos = findViewById(R.id.listViewProyectos);
        fabAgregarProyecto = findViewById(R.id.fabAgregarProyecto);
        tvSinProyectos = findViewById(R.id.tvSinProyectos);

        // Configurar adaptador con la lista ya inicializada
        adapter = new ProyectosAdapter(listaProyectos, this, idUsuarioActual, tipoUsuario);
        listViewProyectos.setAdapter(adapter);

        // Configurar click listener para ListView
        listViewProyectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Proyecto proyecto = listaProyectos.get(position);
                verDetallesProyecto(proyecto);
            }
        });

        // Configurar FAB
        configurarFAB();

        // Cargar proyectos
        cargarProyectos();
    }

    private void configurarFAB() {
        // Mostrar FAB solo para profesores
        if ("profesor".equals(tipoUsuario)) {
            fabAgregarProyecto.setVisibility(View.VISIBLE);
            fabAgregarProyecto.setOnClickListener(v -> {
                Intent intent = new Intent(this, CrearProyectoActivity.class);
                intent.putExtra("idUsuario", idUsuarioActual);
                intent.putExtra("tipoUsuario", tipoUsuario);
                startActivity(intent);
            });
        } else {
            fabAgregarProyecto.setVisibility(View.GONE);
        }
    }

    private void cargarProyectos() {
        Cursor cursor = null;
        try {
            // Obtener proyectos según el tipo de usuario
            if ("profesor".equals(tipoUsuario)) {
                cursor = ProyectoBD.obtenerProyectosPorProfesor(db, idUsuarioActual);
            } else if ("estudiante".equals(tipoUsuario)) {
                cursor = ProyectoBD.obtenerTodosProyectos(db);
            } else {
                cursor = ProyectoBD.obtenerTodosProyectos(db);
            }

            // Limpiar la lista existente
            listaProyectos.clear();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Proyecto proyecto = new Proyecto();
                    proyecto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ID)));
                    proyecto.setNombreProyecto(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_NOMBRE_PROYECTO)));
                    proyecto.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_DESCRIPCION)));
                    proyecto.setFechaCreacion(cursor.getString(cursor.getColumnIndexOrThrow(ProyectoBD.COL_FECHA_CREACION)));
                    proyecto.setIdEquipo(cursor.getInt(cursor.getColumnIndexOrThrow(ProyectoBD.COL_ID_EQUIPO)));

                    listaProyectos.add(proyecto);
                } while (cursor.moveToNext());
            }

            // Notificar al adaptador que los datos cambiaron
            adapter.notifyDataSetChanged();

            // Mostrar u ocultar mensaje de "sin proyectos"
            if (listaProyectos.isEmpty()) {
                tvSinProyectos.setVisibility(View.VISIBLE);
                listViewProyectos.setVisibility(View.GONE);
            } else {
                tvSinProyectos.setVisibility(View.GONE);
                listViewProyectos.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar proyectos", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void verDetallesProyecto(Proyecto proyecto) {
        Intent intent = new Intent(this, DetalleProyectoActivity.class);
        intent.putExtra("idProyecto", proyecto.getId());
        intent.putExtra("idUsuario", idUsuarioActual);
        intent.putExtra("tipoUsuario", tipoUsuario);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProyectos();
    }

    @Override
    protected void onDestroy() {
        if (db != null) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}