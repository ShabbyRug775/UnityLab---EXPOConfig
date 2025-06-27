package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VisitanteBD {
    // Constantes para la tabla VISITANTE
    public static final String TABLE_NAME = "Visitante";
    public static final String COL_ID_VISITANTE = "IdVisitante";
    public static final String COL_NOMBRE = "Nombre";
    public static final String COL_APELLIDO1 = "Apellido1";
    public static final String COL_APELLIDO2 = "Apellido2";
    public static final String COL_ID_EVENTO = "IdEvento";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_VISITANTE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE + " VARCHAR(50), " +
                    COL_APELLIDO1 + " VARCHAR(50), " +
                    COL_APELLIDO2 + " VARCHAR(50), " +
                    COL_ID_EVENTO + " INTEGER, " +
                    "FOREIGN KEY (" + COL_ID_EVENTO + ") REFERENCES " + EventoBD.TABLE_NAME + " (" + EventoBD.COL_ID_EVENTO + "))";

    // Métodos CRUD
    public static long insertarVisitante(SQLiteDatabase db, String nombre, String apellido1, String apellido2, int idEvento) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDO1, apellido1);
        values.put(COL_APELLIDO2, apellido2);
        values.put(COL_ID_EVENTO, idEvento);
        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosVisitantes(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerVisitantePorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_VISITANTE + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static Cursor obtenerVisitantesPorEvento(SQLiteDatabase db, int idEvento) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_EVENTO + " = ?",
                new String[]{String.valueOf(idEvento)},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarVisitantesPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE + " LIKE ? OR " + COL_APELLIDO1 + " LIKE ? OR " + COL_APELLIDO2 + " LIKE ?",
                new String[]{"%" + nombre + "%", "%" + nombre + "%", "%" + nombre + "%"},
                null, null, COL_NOMBRE + " ASC");
    }

    public static int actualizarVisitante(SQLiteDatabase db, int id, String nombre, String apellido1, String apellido2, int idEvento) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDO1, apellido1);
        values.put(COL_APELLIDO2, apellido2);
        values.put(COL_ID_EVENTO, idEvento);
        return db.update(TABLE_NAME, values,
                COL_ID_VISITANTE + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarVisitante(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID_VISITANTE + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Obtiene visitantes con información del evento (JOIN)
     */
    public static Cursor obtenerVisitantesConEvento(SQLiteDatabase db) {
        String query = "SELECT v." + COL_ID_VISITANTE + ", " +
                "v." + COL_NOMBRE + ", " +
                "v." + COL_APELLIDO1 + ", " +
                "v." + COL_APELLIDO2 + ", " +
                "e." + EventoBD.COL_NOMBRE + " as nombre_evento, " +
                "e." + EventoBD.COL_FECHA_INICIO + " as fecha_evento " +
                "FROM " + TABLE_NAME + " v " +
                "LEFT JOIN " + EventoBD.TABLE_NAME + " e ON v." + COL_ID_EVENTO + " = e." + EventoBD.COL_ID_EVENTO + " " +
                "ORDER BY v." + COL_NOMBRE + " ASC";
        return db.rawQuery(query, null);
    }

    /**
     * Cuenta el número de visitantes por evento
     */
    public static Cursor contarVisitantesPorEvento(SQLiteDatabase db) {
        String query = "SELECT " +
                "e." + EventoBD.COL_NOMBRE + " as nombre_evento, " +
                "COUNT(v." + COL_ID_VISITANTE + ") as num_visitantes " +
                "FROM " + EventoBD.TABLE_NAME + " e " +
                "LEFT JOIN " + TABLE_NAME + " v ON e." + EventoBD.COL_ID_EVENTO + " = v." + COL_ID_EVENTO + " " +
                "GROUP BY e." + EventoBD.COL_ID_EVENTO + ", e." + EventoBD.COL_NOMBRE + " " +
                "ORDER BY num_visitantes DESC";
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene estadísticas de visitantes
     */
    public static Cursor obtenerEstadisticasVisitantes(SQLiteDatabase db) {
        String query = "SELECT " +
                "COUNT(*) as total_visitantes, " +
                "COUNT(DISTINCT " + COL_ID_EVENTO + ") as eventos_con_visitantes " +
                "FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene los visitantes más recientes
     */
    public static Cursor obtenerVisitantesRecientes(SQLiteDatabase db, int limite) {
        return db.query(TABLE_NAME,
                null,
                null,
                null,
                null, null,
                COL_ID_VISITANTE + " DESC",
                String.valueOf(limite));
    }
}