package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProyectoBD {
    // Constantes para la tabla PROYECTO
    public static final String TABLE_NAME = "PROYECTO";
    public static final String COL_ID = "id_proyecto";
    public static final String COL_NOMBRE_PROYECTO = "nombre_proyecto";
    public static final String COL_DESCRIPCION = "descripcion";
    public static final String COL_ID_PROFESOR = "id_profesor";
    public static final String COL_ID_EQUIPO = "id_equipo";
    public static final String COL_FECHA_CREACION = "fecha_creacion";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE_PROYECTO + " TEXT NOT NULL, " +
                    COL_DESCRIPCION + " TEXT, " +
                    COL_ID_PROFESOR + " INTEGER, " +
                    COL_ID_EQUIPO + " INTEGER, " +
                    COL_FECHA_CREACION + " TEXT, " +
                    "FOREIGN KEY(" + COL_ID_PROFESOR + ") REFERENCES " + ProfesorBD.TABLE_NAME + "(" + ProfesorBD.COL_ID + "), " +
                    "FOREIGN KEY(" + COL_ID_EQUIPO + ") REFERENCES " + EquipoDB.TABLE_NAME + "(" + EquipoDB.COL_ID_EQUIPO + "))";

    // Metodos CRUD
    public static long insertarProyecto(SQLiteDatabase db, String nombreProyecto, String descripcion, int idProfesor, int idEquipo, String fechaCreacion) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE_PROYECTO, nombreProyecto);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_ID_PROFESOR, idProfesor);
        values.put(COL_ID_EQUIPO, idEquipo);
        values.put(COL_FECHA_CREACION, fechaCreacion);

        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosProyectos(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null,
                COL_FECHA_CREACION + " DESC");
    }

    public static Cursor obtenerProyectoPorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static Cursor obtenerProyectosPorEquipo(SQLiteDatabase db, int idEstudiante) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(idEstudiante)},
                null, null, COL_FECHA_CREACION + " DESC");
    }

    public static Cursor obtenerProyectosPorProfesor(SQLiteDatabase db, int idProfesor) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_PROFESOR + " = ?",
                new String[]{String.valueOf(idProfesor)},
                null, null, COL_FECHA_CREACION + " DESC");
    }

    public static Cursor buscarProyectosPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE_PROYECTO + " LIKE ?",
                new String[]{"%" + nombre + "%"},
                null, null, COL_FECHA_CREACION + " DESC");
    }

    public static int actualizarProyecto(SQLiteDatabase db, int id, String nombreProyecto, String descripcion, int idProfesor, int idEquipo) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE_PROYECTO, nombreProyecto);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_ID_PROFESOR, idProfesor);
        values.put(COL_ID_EQUIPO, idEquipo);

        return db.update(TABLE_NAME, values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarProyecto(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

}