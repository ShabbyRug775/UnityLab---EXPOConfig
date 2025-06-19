package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EquipoDB {
    // Constantes para la tabla EQUIPO
    public static final String TABLE_NAME = "EQUIPO";
    public static final String COL_ID_EQUIPO = "IDEquipo";
    public static final String COL_NOMBRE = "Nombre";
    public static final String COL_NOMBRE_PROYECTO = "NombreProyecto";
    public static final String COL_NUM_ALUMNOS = "Numero_Alumnos";
    public static final String COL_DESCRIPCION = "Descripcion";
    public static final String COL_LUGAR = "Lugar";
    public static final String COL_CARTEL = "Cartel";
    public static final String COL_CANT_EVAL = "CantEval";
    public static final String COL_PROMEDIO = "Promedio";
    public static final String COL_CANT_VISITAS = "CantVisitas";
    public static final String COL_CLAVE_ACCESO = "ClaveAcceso"; // Clave unica para unirse a un equipo

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_EQUIPO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE + " VARCHAR(100) NOT NULL, " +
                    COL_NOMBRE_PROYECTO + " VARCHAR(50), " +
                    COL_NUM_ALUMNOS + " INTEGER, " +
                    COL_DESCRIPCION + " VARCHAR(100), " +
                    COL_LUGAR + " INTEGER, " +
                    COL_CARTEL + " VARCHAR(100), " +
                    COL_CANT_EVAL + " INTEGER DEFAULT 0, " +
                    COL_PROMEDIO + " FLOAT DEFAULT 0, " +
                    COL_CANT_VISITAS + " INTEGER DEFAULT 0," +
                    COL_CLAVE_ACCESO + " VARCHAR(6) UNIQUE)"; // Nueva columna

    // Métodos CRUD
    public static long insertarEquipo(SQLiteDatabase db, String nombre, String nombreProyecto,
                                      int numAlumnos, String descripcion, int lugar,
                                      String cartel, int cantEval, float promedio, int cantVisitas, String claveAcceso) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_NOMBRE_PROYECTO, nombreProyecto);
        values.put(COL_NUM_ALUMNOS, numAlumnos);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_LUGAR, lugar);
        values.put(COL_CARTEL, cartel);
        values.put(COL_CANT_EVAL, cantEval);
        values.put(COL_PROMEDIO, promedio);
        values.put(COL_CANT_VISITAS, cantVisitas);
        values.put(COL_CLAVE_ACCESO, claveAcceso);

        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosEquipos(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public static Cursor obtenerEquipoPorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static Cursor buscarEquiposPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE + " LIKE ?",
                new String[]{"%" + nombre + "%"},
                null, null, null);
    }

    public static Cursor buscarEquiposPorProyecto(SQLiteDatabase db, String proyecto) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE_PROYECTO + " LIKE ?",
                new String[]{"%" + proyecto + "%"},
                null, null, null);
    }

    public static int actualizarEquipo(SQLiteDatabase db, int id, String nombre, String nombreProyecto,
                                       int numAlumnos, String descripcion, int lugar,
                                       String cartel, int cantEval, float promedio, int cantVisitas) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_NOMBRE_PROYECTO, nombreProyecto);
        values.put(COL_NUM_ALUMNOS, numAlumnos);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_LUGAR, lugar);
        values.put(COL_CARTEL, cartel);
        values.put(COL_CANT_EVAL, cantEval);
        values.put(COL_PROMEDIO, promedio);
        values.put(COL_CANT_VISITAS, cantVisitas);

        return db.update(TABLE_NAME, values,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int actualizarPromedio(SQLiteDatabase db, int id, float nuevoPromedio, int nuevaEvaluacion) {
        ContentValues values = new ContentValues();
        values.put(COL_PROMEDIO, nuevoPromedio);
        values.put(COL_CANT_EVAL, nuevaEvaluacion);

        return db.update(TABLE_NAME, values,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int incrementarVisitas(SQLiteDatabase db, int id) {
        ContentValues values = new ContentValues();
        values.put(COL_CANT_VISITAS, COL_CANT_VISITAS + " + 1");

        return db.update(TABLE_NAME, values,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarEquipo(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Método para obtener equipos ordenados por promedio (para ranking)
    public static Cursor obtenerEquiposPorPromedio(SQLiteDatabase db) {
        return db.query(TABLE_NAME,
                null,
                COL_CANT_EVAL + " > 0",
                null,
                null, null,
                COL_PROMEDIO + " DESC");
    }

    // Método para obtener equipos por lugar
    public static Cursor obtenerEquiposPorLugar(SQLiteDatabase db, int lugar) {
        return db.query(TABLE_NAME,
                null,
                COL_LUGAR + " = ?",
                new String[]{String.valueOf(lugar)},
                null, null, null);
    }
}