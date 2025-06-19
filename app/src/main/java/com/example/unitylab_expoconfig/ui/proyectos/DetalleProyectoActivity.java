package com.example.unitylab_expoconfig.ui.proyectos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EquipoDB;
import com.example.unitylab_expoconfig.SQLite.ProyectoBD;

public class DetalleProyectoActivity extends AppCompatActivity {

    private DbmsSQLiteHelper dbHelper;
    private SQLiteDatabase db;
    private int idProyecto;
    private int idUsuarioActual;
    private String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_proyecto);

        // Obtener datos del intent
        idProyecto = getIntent().getIntExtra("idProyecto", -1);
        idUsuarioActual = getIntent().getIntExtra("idUsuario", -1);
        tipoUsuario = getIntent().getStringExtra("tipoUsuario");

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);
        db = dbHelper.getReadableDatabase();

        // Configurar botones
        configurarBotones();

        // Cargar datos del proyecto
        cargarDatosProyecto();
    }

    private void configurarBotones() {
        Button btnEditar = findViewById(R.id.btnEditarProyecto);
        Button btnCambiarEstado = findViewById(R.id.btnCambiarEstado);
        Button btnVerCartel = findViewById(R.id.btnVerCartel);

        // Mostrar opciones según el tipo de usuario
        if ("profesor".equals(tipoUsuario)) {
            btnEditar.setVisibility(View.VISIBLE);
            btnCambiarEstado.setVisibility(View.VISIBLE);
        } else {
            btnEditar.setVisibility(View.GONE);
            btnCambiarEstado.setVisibility(View.GONE);
        }

        btnEditar.setOnClickListener(v -> editarProyecto());
        btnCambiarEstado.setOnClickListener(v -> cambiarEstadoProyecto());
        btnVerCartel.setOnClickListener(v -> verCartelEquipo());
    }

    private void cargarDatosProyecto() {
        if (idProyecto == -1) {
            Toast.makeText(this, "Error: Proyecto no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obtener datos del proyecto
        Cursor cursorProyecto = ProyectoBD.obtenerProyectoPorId(db, idProyecto);
        if (cursorProyecto == null || !cursorProyecto.moveToFirst()) {
            Toast.makeText(this, "Error: Proyecto no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obtener datos del equipo asociado
        int idEquipo = cursorProyecto.getInt(cursorProyecto.getColumnIndexOrThrow(ProyectoBD.COL_ID_EQUIPO));
        Cursor cursorEquipo = EquipoDB.obtenerEquipoPorId(db, idEquipo);

        // Mostrar datos del proyecto
        TextView tvNombre = findViewById(R.id.tvNombreProyecto);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        TextView tvEstado = findViewById(R.id.tvEstado);
        TextView tvFechaCreacion = findViewById(R.id.tvFechaCreacion);
        TextView tvNombreEquipo = findViewById(R.id.tvNombreEquipo);
        TextView tvCodigoAcceso = findViewById(R.id.tvCodigoAcceso);

        // Proyecto
        tvNombre.setText(cursorProyecto.getString(cursorProyecto.getColumnIndexOrThrow(ProyectoBD.COL_NOMBRE_PROYECTO)));
        tvDescripcion.setText(cursorProyecto.getString(cursorProyecto.getColumnIndexOrThrow(ProyectoBD.COL_DESCRIPCION)));
        tvFechaCreacion.setText("Creado: " + cursorProyecto.getString(cursorProyecto.getColumnIndexOrThrow(ProyectoBD.COL_FECHA_CREACION)));

        // Estado (asumiendo que hay un campo estado, si no, puedes eliminarlo)
        tvEstado.setText("ACTIVO"); // Puedes ajustar esto según tu lógica

        // Equipo (si existe)
        if (cursorEquipo != null && cursorEquipo.moveToFirst()) {
            tvNombreEquipo.setText("Equipo: " + cursorEquipo.getString(cursorEquipo.getColumnIndexOrThrow(EquipoDB.COL_NOMBRE)));
            tvCodigoAcceso.setText("Clave de acceso: " + cursorEquipo.getString(cursorEquipo.getColumnIndexOrThrow(EquipoDB.COL_CLAVE_ACCESO)));

            // Mostrar más datos del equipo si es necesario
            TextView tvNumAlumnos = findViewById(R.id.tvNumAlumnos);
            TextView tvLugar = findViewById(R.id.tvLugar);

            tvNumAlumnos.setText("Miembros: " + cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoDB.COL_NUM_ALUMNOS)));
            tvLugar.setText("Lugar: Stand " + cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoDB.COL_LUGAR)));

            cursorEquipo.close();
        }

        cursorProyecto.close();
    }

    private void editarProyecto() {
        // Implementar lógica para editar proyecto
        Toast.makeText(this, "Editar proyecto", Toast.LENGTH_SHORT).show();
    }

    private void cambiarEstadoProyecto() {
        // Implementar lógica para cambiar estado
        Toast.makeText(this, "Cambiar estado", Toast.LENGTH_SHORT).show();
    }

    private void verCartelEquipo() {
        // Implementar lógica para ver cartel
        Toast.makeText(this, "Ver cartel del equipo", Toast.LENGTH_SHORT).show();
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