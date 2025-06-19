package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EstudianteBD {
    // Constantes para la tabla ESTUDIANTE
    public static final String TABLE_NAME = "ESTUDIANTE";
    public static final String COL_ID = "id_estudiante";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_APELLIDOS = "apellidos";
    public static final String COL_CORREO = "correo";
    public static final String COL_BOLETA = "boleta";
    public static final String COL_GRUPO = "grupo";
    public static final String COL_SEMESTRE = "semestre";
    public static final String COL_CARRERA = "carrera";
    public static final String COL_TURNO = "turno";
    public static final String COL_PASSWORD = "password";
    public static final String COL_ID_EQUIPO = "id_equipo";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE + " TEXT NOT NULL, " +
                    COL_APELLIDOS + " TEXT NOT NULL, " +
                    COL_CORREO + " TEXT UNIQUE, " +
                    COL_BOLETA + " TEXT UNIQUE, " +
                    COL_GRUPO + " TEXT, " +
                    COL_SEMESTRE + " TEXT, " +
                    COL_CARRERA + " TEXT, " +
                    COL_TURNO + " TEXT, " +
                    COL_PASSWORD + " TEXT," +
                    COL_ID_EQUIPO + " INTEGER," +
                    "FOREIGN KEY(" + COL_ID_EQUIPO + ") REFERENCES " + EquipoDB.TABLE_NAME + "(" + EquipoDB.COL_ID_EQUIPO + "))";

    // Métodos CRUD
    public static long insertarEstudiante(SQLiteDatabase db, String nombre, String apellidos,
                                          String correo, String boleta, String grupo,
                                          String semestre, String carrera, String turno, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDOS, apellidos);
        values.put(COL_CORREO, correo);
        values.put(COL_BOLETA, boleta);
        values.put(COL_GRUPO, grupo);
        values.put(COL_SEMESTRE, semestre);
        values.put(COL_CARRERA, carrera);
        values.put(COL_TURNO, turno);
        values.put(COL_PASSWORD, password);

        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosEstudiantes(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public static Cursor obtenerEstudiantePorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static int actualizarEstudiante(SQLiteDatabase db, int id, String nombre, String apellidos,
                                           String correo, String boleta, String grupo, String semestre, String carrera, String turno, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDOS, apellidos);
        values.put(COL_CORREO, correo);
        values.put(COL_BOLETA, boleta);
        values.put(COL_GRUPO, grupo);
        values.put(COL_SEMESTRE, semestre);
        values.put(COL_CARRERA, carrera);
        values.put(COL_TURNO, turno);
        values.put(COL_PASSWORD, password);

        return db.update(TABLE_NAME, values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarEstudiante(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Metodo adicional para búsqueda
    public static Cursor buscarEstudiantesPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE + " LIKE ?",
                new String[]{"%" + nombre + "%"},
                null, null, null);
    }

    public static Cursor buscarEstudiantePorBoleta(SQLiteDatabase db, String boleta) {
        return db.query(TABLE_NAME,
                null,
                COL_BOLETA + " = ?",
                new String[]{boleta},
                null, null, null);
    }

    // Metodo para actualizar el equipo del estudiante
    public static int actualizarEquipoEstudiante(SQLiteDatabase db, int idEstudiante, int idEquipo) {
        ContentValues values = new ContentValues();
        values.put(COL_ID_EQUIPO, idEquipo);

        return db.update(TABLE_NAME, values,
                COL_ID + " = ?",
                new String[]{String.valueOf(idEstudiante)});
    }

    // Metodo para verificar si el estudiante ya está en un equipo
    public static boolean estaEnEquipo(SQLiteDatabase db, int idEstudiante) {
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COL_ID_EQUIPO},
                COL_ID + " = ?",
                new String[]{String.valueOf(idEstudiante)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int equipoIndex = cursor.getColumnIndex(COL_ID_EQUIPO);
            boolean enEquipo = !cursor.isNull(equipoIndex);
            cursor.close();
            return enEquipo;
        }

        if (cursor != null) {
            cursor.close();
        }
        return false;
    }
}