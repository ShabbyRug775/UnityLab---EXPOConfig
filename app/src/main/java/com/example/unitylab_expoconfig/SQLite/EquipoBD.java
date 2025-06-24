package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EquipoBD {
    // Constantes para la tabla EQUIPO
    public static final String TABLE_NAME = "Equipo";
    public static final String COL_ID_EQUIPO = "IdEquipo";
    public static final String COL_NOMBRE = "Nombre";
    public static final String COL_NOMBRE_PROYECTO = "NombreProyecto";
    public static final String COL_NUMERO_ALUMNOS = "Numero_Alumnos";
    public static final String COL_DESCRIPCION = "Descripcion";
    public static final String COL_TURNO = "Turno";
    public static final String COL_LUGAR = "Lugar";
    public static final String COL_CARTEL = "Cartel";
    public static final String COL_CANT_EVAL = "CantEval";
    public static final String COL_PROMEDIO = "Promedio";
    public static final String COL_CANT_VISITAS = "CantVisitas";
    public static final String COL_ID_PROYECTO = "IdProyecto";
    public static final String COL_BOLETA_LIDER = "BoletaLider";
    public static final String COL_ESTADO_EQUIPO = "EstadoEquipo";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_EQUIPO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE + " VARCHAR(100) NOT NULL, " +
                    COL_NOMBRE_PROYECTO + " VARCHAR(50) NOT NULL, " +
                    COL_NUMERO_ALUMNOS + " INTEGER NOT NULL DEFAULT 0, " +
                    COL_DESCRIPCION + " VARCHAR(255) NOT NULL, " +
                    COL_TURNO + " VARCHAR(20), " +
                    COL_LUGAR + " VARCHAR(50), " +
                    COL_CARTEL + " VARCHAR(50), " +
                    COL_CANT_EVAL + " INTEGER DEFAULT 0, " +
                    COL_PROMEDIO + " DECIMAL(3,2) DEFAULT 0.00, " +
                    COL_CANT_VISITAS + " INTEGER DEFAULT 0, " +
                    COL_ID_PROYECTO + " INTEGER, " +
                    COL_BOLETA_LIDER + " INTEGER, " +
                    COL_ESTADO_EQUIPO + " VARCHAR(20) DEFAULT 'EnFormacion', " +
                    "FOREIGN KEY (" + COL_ID_PROYECTO + ") REFERENCES " + ProyectoBD.TABLE_NAME + " (" + ProyectoBD.COL_ID_PROYECTO + "), " +
                    "FOREIGN KEY (" + COL_BOLETA_LIDER + ") REFERENCES " + AlumnoBD.TABLE_NAME + " (" + AlumnoBD.COL_BOLETA + "))";

    // Métodos CRUD
    public static long insertarEquipo(SQLiteDatabase db, String nombre, String nombreProyecto, int numeroAlumnos,
                                      String descripcion, String turno, String lugar, String cartel,
                                      int cantEval, double promedio, int cantVisitas, Integer idProyecto,
                                      Integer boletaLider, String estadoEquipo) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_NOMBRE_PROYECTO, nombreProyecto);
        values.put(COL_NUMERO_ALUMNOS, numeroAlumnos);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_TURNO, turno);
        values.put(COL_LUGAR, lugar);
        values.put(COL_CARTEL, cartel);
        values.put(COL_CANT_EVAL, cantEval);
        values.put(COL_PROMEDIO, promedio);
        values.put(COL_CANT_VISITAS, cantVisitas);
        if (idProyecto != null) {
            values.put(COL_ID_PROYECTO, idProyecto);
        }
        if (boletaLider != null) {
            values.put(COL_BOLETA_LIDER, boletaLider);
        }
        values.put(COL_ESTADO_EQUIPO, estadoEquipo != null ? estadoEquipo : "EnFormacion");
        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosEquipos(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerEquipoPorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static Cursor obtenerEquiposPorProyecto(SQLiteDatabase db, int idProyecto) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_PROYECTO + " = ?",
                new String[]{String.valueOf(idProyecto)},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerEquiposPorLider(SQLiteDatabase db, int boletaLider) {
        return db.query(TABLE_NAME,
                null,
                COL_BOLETA_LIDER + " = ?",
                new String[]{String.valueOf(boletaLider)},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerEquiposPorEstado(SQLiteDatabase db, String estado) {
        return db.query(TABLE_NAME,
                null,
                COL_ESTADO_EQUIPO + " = ?",
                new String[]{estado},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerEquiposPorTurno(SQLiteDatabase db, String turno) {
        return db.query(TABLE_NAME,
                null,
                COL_TURNO + " = ?",
                new String[]{turno},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarEquiposPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE + " LIKE ?",
                new String[]{"%" + nombre + "%"},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarEquiposPorProyecto(SQLiteDatabase db, String nombreProyecto) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE_PROYECTO + " LIKE ?",
                new String[]{"%" + nombreProyecto + "%"},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarEquiposPorLugar(SQLiteDatabase db, String lugar) {
        return db.query(TABLE_NAME,
                null,
                COL_LUGAR + " LIKE ?",
                new String[]{"%" + lugar + "%"},
                null, null, COL_NOMBRE + " ASC");
    }

    public static int actualizarEquipo(SQLiteDatabase db, int id, String nombre, String nombreProyecto, int numeroAlumnos,
                                       String descripcion, String turno, String lugar, String cartel,
                                       int cantEval, double promedio, int cantVisitas, Integer idProyecto,
                                       Integer boletaLider, String estadoEquipo) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_NOMBRE_PROYECTO, nombreProyecto);
        values.put(COL_NUMERO_ALUMNOS, numeroAlumnos);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_TURNO, turno);
        values.put(COL_LUGAR, lugar);
        values.put(COL_CARTEL, cartel);
        values.put(COL_CANT_EVAL, cantEval);
        values.put(COL_PROMEDIO, promedio);
        values.put(COL_CANT_VISITAS, cantVisitas);
        if (idProyecto != null) {
            values.put(COL_ID_PROYECTO, idProyecto);
        }
        if (boletaLider != null) {
            values.put(COL_BOLETA_LIDER, boletaLider);
        }
        if (estadoEquipo != null) {
            values.put(COL_ESTADO_EQUIPO, estadoEquipo);
        }
        return db.update(TABLE_NAME, values,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int actualizarPromedio(SQLiteDatabase db, int id, double nuevoPromedio, int nuevaCantEval) {
        ContentValues values = new ContentValues();
        values.put(COL_PROMEDIO, nuevoPromedio);
        values.put(COL_CANT_EVAL, nuevaCantEval);
        return db.update(TABLE_NAME, values,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int incrementarVisitas(SQLiteDatabase db, int id) {
        String sql = "UPDATE " + TABLE_NAME + " SET " + COL_CANT_VISITAS + " = " + COL_CANT_VISITAS + " + 1 WHERE " + COL_ID_EQUIPO + " = ?";
        db.execSQL(sql, new String[]{String.valueOf(id)});

        // Retornar el número de filas afectadas
        Cursor cursor = db.rawQuery("SELECT changes()", null);
        int changes = 0;
        if (cursor.moveToFirst()) {
            changes = cursor.getInt(0);
        }
        cursor.close();
        return changes;
    }

    public static int actualizarEstadoEquipo(SQLiteDatabase db, int id, String nuevoEstado) {
        ContentValues values = new ContentValues();
        values.put(COL_ESTADO_EQUIPO, nuevoEstado);
        return db.update(TABLE_NAME, values,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int actualizarContadorAlumnos(SQLiteDatabase db, int id, int nuevoContador) {
        ContentValues values = new ContentValues();
        values.put(COL_NUMERO_ALUMNOS, nuevoContador);
        return db.update(TABLE_NAME, values,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarEquipo(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Obtiene equipos ordenados por promedio (ranking)
     */
    public static Cursor obtenerEquiposPorPromedio(SQLiteDatabase db) {
        return db.query(TABLE_NAME,
                null,
                COL_CANT_EVAL + " > 0",
                null,
                null, null,
                COL_PROMEDIO + " DESC, " + COL_CANT_EVAL + " DESC");
    }

    /**
     * Obtiene equipos con información de proyecto y líder (JOIN)
     */
    public static Cursor obtenerEquiposConDetalles(SQLiteDatabase db) {
        String query = "SELECT e." + COL_ID_EQUIPO + ", " +
                "e." + COL_NOMBRE + ", " +
                "e." + COL_NOMBRE_PROYECTO + ", " +
                "e." + COL_NUMERO_ALUMNOS + ", " +
                "e." + COL_DESCRIPCION + ", " +
                "e." + COL_TURNO + ", " +
                "e." + COL_LUGAR + ", " +
                "e." + COL_CANT_EVAL + ", " +
                "e." + COL_PROMEDIO + ", " +
                "e." + COL_CANT_VISITAS + ", " +
                "e." + COL_ESTADO_EQUIPO + ", " +
                "p." + ProyectoBD.COL_NOMBRE + " as proyecto_nombre, " +
                "a." + AlumnoBD.COL_NOMBRE + " || ' ' || a." + AlumnoBD.COL_APELLIDO1 + " as lider_nombre " +
                "FROM " + TABLE_NAME + " e " +
                "LEFT JOIN " + ProyectoBD.TABLE_NAME + " p ON e." + COL_ID_PROYECTO + " = p." + ProyectoBD.COL_ID_PROYECTO + " " +
                "LEFT JOIN " + AlumnoBD.TABLE_NAME + " a ON e." + COL_BOLETA_LIDER + " = a." + AlumnoBD.COL_BOLETA + " " +
                "ORDER BY e." + COL_NOMBRE + " ASC";
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene estadísticas de equipos
     */
    public static Cursor obtenerEstadisticasEquipos(SQLiteDatabase db) {
        String query = "SELECT " +
                "COUNT(*) as total_equipos, " +
                "COUNT(CASE WHEN " + COL_ESTADO_EQUIPO + " = 'EnFormacion' THEN 1 END) as equipos_formacion, " +
                "COUNT(CASE WHEN " + COL_ESTADO_EQUIPO + " = 'Registrado' THEN 1 END) as equipos_registrados, " +
                "SUM(" + COL_NUMERO_ALUMNOS + ") as total_alumnos, " +
                "AVG(" + COL_PROMEDIO + ") as promedio_general, " +
                "SUM(" + COL_CANT_VISITAS + ") as total_visitas " +
                "FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }
}