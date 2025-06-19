package com.example.unitylab_expoconfig.ui.equipos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EquipoDB;
import com.example.unitylab_expoconfig.SQLite.EstudianteBD;

public class UnirseAEquipoActivity extends AppCompatActivity {

    private EditText editCodigoAcceso;
    private Button btnValidarCodigo;
    private DbmsSQLiteHelper dbHelper;
    private int idEstudiante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_equipo_proyecto);

        // Obtener ID del estudiante del intent
        idEstudiante = getIntent().getIntExtra("idUsuario", -1);
        if (idEstudiante == -1) {
            Toast.makeText(this, "Error: No se identificó al estudiante", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DbmsSQLiteHelper(this);

        editCodigoAcceso = findViewById(R.id.editCodigoAcceso);
        btnValidarCodigo = findViewById(R.id.btnValidarCodigo);

        btnValidarCodigo.setOnClickListener(v -> unirseAEquipo());
    }

    private void unirseAEquipo() {
        String codigo = editCodigoAcceso.getText().toString().trim().toUpperCase();

        if (codigo.isEmpty()) {
            Toast.makeText(this, "Ingresa el código de acceso", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Verificar si el estudiante ya está en un equipo
            if (EstudianteBD.estaEnEquipo(db, idEstudiante)) {
                Toast.makeText(this, "Ya perteneces a un equipo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Buscar equipo por código
            Cursor cursor = db.query(EquipoDB.TABLE_NAME,
                    null,
                    EquipoDB.COL_CLAVE_ACCESO + " = ?",
                    new String[]{codigo},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idEquipo = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_ID_EQUIPO));
                int numAlumnos = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_NUM_ALUMNOS));

                // Actualizar equipo del estudiante
                int filasActualizadas = EstudianteBD.actualizarEquipoEstudiante(db, idEstudiante, idEquipo);

                if (filasActualizadas > 0) {
                    // Incrementar el número de alumnos en el equipo
                    ContentValues values = new ContentValues();
                    values.put(EquipoDB.COL_NUM_ALUMNOS, numAlumnos + 1);
                    db.update(EquipoDB.TABLE_NAME, values,
                            EquipoDB.COL_ID_EQUIPO + " = ?",
                            new String[]{String.valueOf(idEquipo)});

                    Toast.makeText(this, "¡Te has unido al equipo exitosamente!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al unirse al equipo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Código de equipo inválido", Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }
}