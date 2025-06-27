package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EventoBD {
    // Constantes para la tabla EVENTO
    public static final String TABLE_NAME = "Evento";
    public static final String COL_ID_EVENTO = "IdEvento";
    public static final String COL_NOMBRE = "Nombre";
    public static final String COL_DESCRIPCION = "Descripcion";
    public static final String COL_UBICACION = "Ubicacion";
    public static final String COL_FECHA_INICIO = "FechaInicio";
    public static final String COL_FECHA_FIN = "FechaFin";
    public static final String COL_DIAS_PRESENTACION = "Diaspresentacion";
    public static final String COL_HORA_INICIO = "HoraInicio";
    public static final String COL_HORA_FIN = "HoraFin";
    public static final String COL_ASISTENCIA = "Asistencia";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_EVENTO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE + " VARCHAR(50), " +
                    COL_DESCRIPCION + " VARCHAR(100), " +
                    COL_UBICACION + " VARCHAR(50), " +
                    COL_FECHA_INICIO + " DATE, " +
                    COL_FECHA_FIN + " DATE, " +
                    COL_DIAS_PRESENTACION + " VARCHAR(50), " +
                    COL_HORA_INICIO + " TIME, " +
                    COL_HORA_FIN + " TIME, " +
                    COL_ASISTENCIA + " INTEGER DEFAULT 0)";

    // Métodos CRUD
    public static long insertarEvento(SQLiteDatabase db, String nombre, String descripcion, String ubicacion,
                                      String fechaInicio, String fechaFin, String diasPresentacion,
                                      String horaInicio, String horaFin, int asistencia) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_UBICACION, ubicacion);
        values.put(COL_FECHA_INICIO, fechaInicio);
        values.put(COL_FECHA_FIN, fechaFin);
        values.put(COL_DIAS_PRESENTACION, diasPresentacion);
        values.put(COL_HORA_INICIO, horaInicio);
        values.put(COL_HORA_FIN, horaFin);
        values.put(COL_ASISTENCIA, asistencia);
        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosEventos(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null, COL_FECHA_INICIO + " DESC");
    }

    public static Cursor obtenerEventoPorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_EVENTO + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static Cursor obtenerEventosActivos(SQLiteDatabase db) {
        // Obtener eventos cuya fecha de fin es mayor o igual a la fecha actual
        return db.query(TABLE_NAME,
                null,
                COL_FECHA_FIN + " >= date('now')",
                null,
                null, null, COL_FECHA_INICIO + " ASC");
    }

    public static Cursor obtenerEventosPorFecha(SQLiteDatabase db, String fechaInicio, String fechaFin) {
        return db.query(TABLE_NAME,
                null,
                COL_FECHA_INICIO + " >= ? AND " + COL_FECHA_FIN + " <= ?",
                new String[]{fechaInicio, fechaFin},
                null, null, COL_FECHA_INICIO + " ASC");
    }

    public static Cursor buscarEventosPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE + " LIKE ?",
                new String[]{"%" + nombre + "%"},
                null, null, COL_FECHA_INICIO + " DESC");
    }

    public static Cursor buscarEventosPorUbicacion(SQLiteDatabase db, String ubicacion) {
        return db.query(TABLE_NAME,
                null,
                COL_UBICACION + " LIKE ?",
                new String[]{"%" + ubicacion + "%"},
                null, null, COL_FECHA_INICIO + " DESC");
    }

    public static int actualizarEvento(SQLiteDatabase db, int id, String nombre, String descripcion, String ubicacion,
                                       String fechaInicio, String fechaFin, String diasPresentacion,
                                       String horaInicio, String horaFin, int asistencia) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_UBICACION, ubicacion);
        values.put(COL_FECHA_INICIO, fechaInicio);
        values.put(COL_FECHA_FIN, fechaFin);
        values.put(COL_DIAS_PRESENTACION, diasPresentacion);
        values.put(COL_HORA_INICIO, horaInicio);
        values.put(COL_HORA_FIN, horaFin);
        values.put(COL_ASISTENCIA, asistencia);
        return db.update(TABLE_NAME, values,
                COL_ID_EVENTO + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int incrementarAsistencia(SQLiteDatabase db, int id) {
        String sql = "UPDATE " + TABLE_NAME + " SET " + COL_ASISTENCIA + " = " + COL_ASISTENCIA + " + 1 WHERE " + COL_ID_EVENTO + " = ?";
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

    public static int actualizarAsistencia(SQLiteDatabase db, int id, int nuevaAsistencia) {
        ContentValues values = new ContentValues();
        values.put(COL_ASISTENCIA, nuevaAsistencia);
        return db.update(TABLE_NAME, values,
                COL_ID_EVENTO + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarEvento(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID_EVENTO + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Obtiene eventos próximos (dentro de los próximos 30 días)
     */
    public static Cursor obtenerEventosProximos(SQLiteDatabase db) {
        return db.query(TABLE_NAME,
                null,
                COL_FECHA_INICIO + " >= date('now') AND " + COL_FECHA_INICIO + " <= date('now', '+30 days')",
                null,
                null, null, COL_FECHA_INICIO + " ASC");
    }

    /**
     * Obtiene estadísticas básicas de eventos
     */
    public static Cursor obtenerEstadisticasEventos(SQLiteDatabase db) {
        String query = "SELECT " +
                "COUNT(*) as total_eventos, " +
                "SUM(" + COL_ASISTENCIA + ") as total_asistencia, " +
                "AVG(" + COL_ASISTENCIA + ") as promedio_asistencia, " +
                "MAX(" + COL_ASISTENCIA + ") as max_asistencia " +
                "FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }
}