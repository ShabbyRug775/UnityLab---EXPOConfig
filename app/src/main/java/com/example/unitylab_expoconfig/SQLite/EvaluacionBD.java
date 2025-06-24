package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EvaluacionBD {
    // Constantes para la tabla EVALUACION
    public static final String TABLE_NAME = "Evaluacion";
    public static final String COL_ID_EVALUACION = "IdEvaluacion";
    public static final String COL_CALIFICACION = "Calificacion";
    public static final String COL_COMENTARIOS = "Comentarios";
    public static final String COL_ID_EQUIPO = "IdEquipo";
    public static final String COL_NUMERO_EMPLEADO_EVALUADOR = "NumeroEmpleadoEvaluador";
    public static final String COL_BOLETA_EVALUADOR = "BoletaEvaluador";
    public static final String COL_ID_VISITANTE_EVALUADOR = "IdVisitanteEvaluador";
    public static final String COL_FECHA_EVALUACION = "FechaEvaluacion";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_EVALUACION + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CALIFICACION + " INTEGER, " +
                    COL_COMENTARIOS + " VARCHAR(255), " +
                    COL_ID_EQUIPO + " INTEGER, " +
                    COL_NUMERO_EMPLEADO_EVALUADOR + " INTEGER, " +
                    COL_BOLETA_EVALUADOR + " INTEGER, " +
                    COL_ID_VISITANTE_EVALUADOR + " INTEGER, " +
                    COL_FECHA_EVALUACION + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (" + COL_ID_EQUIPO + ") REFERENCES " + EquipoBD.TABLE_NAME + " (" + EquipoBD.COL_ID_EQUIPO + "), " +
                    "FOREIGN KEY (" + COL_NUMERO_EMPLEADO_EVALUADOR + ") REFERENCES " + ProfesorBD.TABLE_NAME + " (" + ProfesorBD.COL_NUMERO_EMPLEADO + "), " +
                    "FOREIGN KEY (" + COL_BOLETA_EVALUADOR + ") REFERENCES " + AlumnoBD.TABLE_NAME + " (" + AlumnoBD.COL_BOLETA + "), " +
                    "FOREIGN KEY (" + COL_ID_VISITANTE_EVALUADOR + ") REFERENCES " + VisitanteBD.TABLE_NAME + " (" + VisitanteBD.COL_ID_VISITANTE + "))";

    // Métodos CRUD

    /**
     * Inserta una evaluación realizada por un profesor
     */
    public static long insertarEvaluacionProfesor(SQLiteDatabase db, int calificacion, String comentarios,
                                                  int idEquipo, int numeroEmpleadoEvaluador) {
        ContentValues values = new ContentValues();
        values.put(COL_CALIFICACION, calificacion);
        values.put(COL_COMENTARIOS, comentarios);
        values.put(COL_ID_EQUIPO, idEquipo);
        values.put(COL_NUMERO_EMPLEADO_EVALUADOR, numeroEmpleadoEvaluador);
        values.putNull(COL_BOLETA_EVALUADOR);
        values.putNull(COL_ID_VISITANTE_EVALUADOR);
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Inserta una evaluación realizada por un alumno
     */
    public static long insertarEvaluacionAlumno(SQLiteDatabase db, int calificacion, String comentarios,
                                                int idEquipo, int boletaEvaluador) {
        ContentValues values = new ContentValues();
        values.put(COL_CALIFICACION, calificacion);
        values.put(COL_COMENTARIOS, comentarios);
        values.put(COL_ID_EQUIPO, idEquipo);
        values.putNull(COL_NUMERO_EMPLEADO_EVALUADOR);
        values.put(COL_BOLETA_EVALUADOR, boletaEvaluador);
        values.putNull(COL_ID_VISITANTE_EVALUADOR);
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Inserta una evaluación realizada por un visitante
     */
    public static long insertarEvaluacionVisitante(SQLiteDatabase db, int calificacion, String comentarios,
                                                   int idEquipo, int idVisitanteEvaluador) {
        ContentValues values = new ContentValues();
        values.put(COL_CALIFICACION, calificacion);
        values.put(COL_COMENTARIOS, comentarios);
        values.put(COL_ID_EQUIPO, idEquipo);
        values.putNull(COL_NUMERO_EMPLEADO_EVALUADOR);
        values.putNull(COL_BOLETA_EVALUADOR);
        values.put(COL_ID_VISITANTE_EVALUADOR, idVisitanteEvaluador);
        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodasEvaluaciones(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null, COL_FECHA_EVALUACION + " DESC");
    }

    public static Cursor obtenerEvaluacionPorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_EVALUACION + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static Cursor obtenerEvaluacionesPorEquipo(SQLiteDatabase db, int idEquipo) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(idEquipo)},
                null, null, COL_FECHA_EVALUACION + " DESC");
    }

    public static Cursor obtenerEvaluacionesPorProfesor(SQLiteDatabase db, int numeroEmpleadoProfesor) {
        return db.query(TABLE_NAME,
                null,
                COL_NUMERO_EMPLEADO_EVALUADOR + " = ?",
                new String[]{String.valueOf(numeroEmpleadoProfesor)},
                null, null, COL_FECHA_EVALUACION + " DESC");
    }

    public static Cursor obtenerEvaluacionesPorAlumno(SQLiteDatabase db, int boletaAlumno) {
        return db.query(TABLE_NAME,
                null,
                COL_BOLETA_EVALUADOR + " = ?",
                new String[]{String.valueOf(boletaAlumno)},
                null, null, COL_FECHA_EVALUACION + " DESC");
    }

    public static Cursor obtenerEvaluacionesPorVisitante(SQLiteDatabase db, int idVisitante) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_VISITANTE_EVALUADOR + " = ?",
                new String[]{String.valueOf(idVisitante)},
                null, null, COL_FECHA_EVALUACION + " DESC");
    }

    public static Cursor obtenerEvaluacionesPorCalificacion(SQLiteDatabase db, int calificacionMinima, int calificacionMaxima) {
        return db.query(TABLE_NAME,
                null,
                COL_CALIFICACION + " >= ? AND " + COL_CALIFICACION + " <= ?",
                new String[]{String.valueOf(calificacionMinima), String.valueOf(calificacionMaxima)},
                null, null, COL_FECHA_EVALUACION + " DESC");
    }

    public static Cursor obtenerEvaluacionesPorFecha(SQLiteDatabase db, String fechaInicio, String fechaFin) {
        return db.query(TABLE_NAME,
                null,
                COL_FECHA_EVALUACION + " >= ? AND " + COL_FECHA_EVALUACION + " <= ?",
                new String[]{fechaInicio, fechaFin},
                null, null, COL_FECHA_EVALUACION + " DESC");
    }

    public static int actualizarEvaluacion(SQLiteDatabase db, int id, int calificacion, String comentarios) {
        ContentValues values = new ContentValues();
        values.put(COL_CALIFICACION, calificacion);
        values.put(COL_COMENTARIOS, comentarios);
        return db.update(TABLE_NAME, values,
                COL_ID_EVALUACION + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarEvaluacion(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID_EVALUACION + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Obtiene evaluaciones con información detallada (JOIN con todas las tablas relacionadas)
     */
    public static Cursor obtenerEvaluacionesConDetalles(SQLiteDatabase db) {
        String query = "SELECT ev." + COL_ID_EVALUACION + ", " +
                "ev." + COL_CALIFICACION + ", " +
                "ev." + COL_COMENTARIOS + ", " +
                "ev." + COL_FECHA_EVALUACION + ", " +
                "eq." + EquipoBD.COL_NOMBRE + " as nombre_equipo, " +
                "CASE " +
                "  WHEN ev." + COL_NUMERO_EMPLEADO_EVALUADOR + " IS NOT NULL THEN pr." + ProfesorBD.COL_NOMBRE + " || ' ' || pr." + ProfesorBD.COL_APELLIDO1 + " " +
                "  WHEN ev." + COL_BOLETA_EVALUADOR + " IS NOT NULL THEN al." + AlumnoBD.COL_NOMBRE + " || ' ' || al." + AlumnoBD.COL_APELLIDO1 + " " +
                "  WHEN ev." + COL_ID_VISITANTE_EVALUADOR + " IS NOT NULL THEN vi." + VisitanteBD.COL_NOMBRE + " || ' ' || vi." + VisitanteBD.COL_APELLIDO1 + " " +
                "  ELSE 'Evaluador desconocido' " +
                "END as nombre_evaluador, " +
                "CASE " +
                "  WHEN ev." + COL_NUMERO_EMPLEADO_EVALUADOR + " IS NOT NULL THEN 'Profesor' " +
                "  WHEN ev." + COL_BOLETA_EVALUADOR + " IS NOT NULL THEN 'Alumno' " +
                "  WHEN ev." + COL_ID_VISITANTE_EVALUADOR + " IS NOT NULL THEN 'Visitante' " +
                "  ELSE 'Desconocido' " +
                "END as tipo_evaluador " +
                "FROM " + TABLE_NAME + " ev " +
                "LEFT JOIN " + EquipoBD.TABLE_NAME + " eq ON ev." + COL_ID_EQUIPO + " = eq." + EquipoBD.COL_ID_EQUIPO + " " +
                "LEFT JOIN " + ProfesorBD.TABLE_NAME + " pr ON ev." + COL_NUMERO_EMPLEADO_EVALUADOR + " = pr." + ProfesorBD.COL_NUMERO_EMPLEADO + " " +
                "LEFT JOIN " + AlumnoBD.TABLE_NAME + " al ON ev." + COL_BOLETA_EVALUADOR + " = al." + AlumnoBD.COL_BOLETA + " " +
                "LEFT JOIN " + VisitanteBD.TABLE_NAME + " vi ON ev." + COL_ID_VISITANTE_EVALUADOR + " = vi." + VisitanteBD.COL_ID_VISITANTE + " " +
                "ORDER BY ev." + COL_FECHA_EVALUACION + " DESC";
        return db.rawQuery(query, null);
    }

    /**
     * Calcula el promedio de calificaciones por equipo
     */
    public static Cursor obtenerPromediosPorEquipo(SQLiteDatabase db) {
        String query = "SELECT " +
                "eq." + EquipoBD.COL_ID_EQUIPO + ", " +
                "eq." + EquipoBD.COL_NOMBRE + " as nombre_equipo, " +
                "COUNT(ev." + COL_ID_EVALUACION + ") as total_evaluaciones, " +
                "AVG(CAST(ev." + COL_CALIFICACION + " AS FLOAT)) as promedio_calificacion, " +
                "MIN(ev." + COL_CALIFICACION + ") as calificacion_minima, " +
                "MAX(ev." + COL_CALIFICACION + ") as calificacion_maxima " +
                "FROM " + EquipoBD.TABLE_NAME + " eq " +
                "LEFT JOIN " + TABLE_NAME + " ev ON eq." + EquipoBD.COL_ID_EQUIPO + " = ev." + COL_ID_EQUIPO + " " +
                "GROUP BY eq." + EquipoBD.COL_ID_EQUIPO + ", eq." + EquipoBD.COL_NOMBRE + " " +
                "ORDER BY promedio_calificacion DESC";
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene estadísticas generales de evaluaciones
     */
    public static Cursor obtenerEstadisticasEvaluaciones(SQLiteDatabase db) {
        String query = "SELECT " +
                "COUNT(*) as total_evaluaciones, " +
                "AVG(CAST(" + COL_CALIFICACION + " AS FLOAT)) as promedio_general, " +
                "MIN(" + COL_CALIFICACION + ") as calificacion_minima, " +
                "MAX(" + COL_CALIFICACION + ") as calificacion_maxima, " +
                "COUNT(DISTINCT " + COL_ID_EQUIPO + ") as equipos_evaluados, " +
                "COUNT(CASE WHEN " + COL_NUMERO_EMPLEADO_EVALUADOR + " IS NOT NULL THEN 1 END) as evaluaciones_profesores, " +
                "COUNT(CASE WHEN " + COL_BOLETA_EVALUADOR + " IS NOT NULL THEN 1 END) as evaluaciones_alumnos, " +
                "COUNT(CASE WHEN " + COL_ID_VISITANTE_EVALUADOR + " IS NOT NULL THEN 1 END) as evaluaciones_visitantes " +
                "FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene las evaluaciones más recientes
     */
    public static Cursor obtenerEvaluacionesRecientes(SQLiteDatabase db, int limite) {
        return db.query(TABLE_NAME,
                null,
                null,
                null,
                null, null,
                COL_FECHA_EVALUACION + " DESC",
                String.valueOf(limite));
    }

    /**
     * Verifica si un evaluador ya evaluó un equipo específico
     */
    public static boolean yaEvaluoEquipo(SQLiteDatabase db, int idEquipo, Integer numeroEmpleadoEvaluador,
                                         Integer boletaEvaluador, Integer idVisitanteEvaluador) {
        String selection = COL_ID_EQUIPO + " = ?";
        String[] selectionArgs;

        if (numeroEmpleadoEvaluador != null) {
            selection += " AND " + COL_NUMERO_EMPLEADO_EVALUADOR + " = ?";
            selectionArgs = new String[]{String.valueOf(idEquipo), String.valueOf(numeroEmpleadoEvaluador)};
        } else if (boletaEvaluador != null) {
            selection += " AND " + COL_BOLETA_EVALUADOR + " = ?";
            selectionArgs = new String[]{String.valueOf(idEquipo), String.valueOf(boletaEvaluador)};
        } else if (idVisitanteEvaluador != null) {
            selection += " AND " + COL_ID_VISITANTE_EVALUADOR + " = ?";
            selectionArgs = new String[]{String.valueOf(idEquipo), String.valueOf(idVisitanteEvaluador)};
        } else {
            return false; // No se especificó ningún evaluador válido
        }

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COL_ID_EVALUACION},
                selection,
                selectionArgs,
                null, null, null);

        boolean yaEvaluo = cursor.getCount() > 0;
        cursor.close();
        return yaEvaluo;
    }
}