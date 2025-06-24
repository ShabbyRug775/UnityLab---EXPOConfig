package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AlumnoBD {
    // Constantes para la tabla ALUMNO
    public static final String TABLE_NAME = "Alumno";
    public static final String COL_BOLETA = "Boleta";
    public static final String COL_NOMBRE = "Nombre";
    public static final String COL_APELLIDO1 = "Apellido1";
    public static final String COL_APELLIDO2 = "Apellido2";
    public static final String COL_CORREO = "Correo";
    public static final String COL_CARRERA = "Carrera";
    public static final String COL_SEMESTRE = "Semestre";
    public static final String COL_GRUPO = "Grupo";
    public static final String COL_TURNO = "Turno";
    public static final String COL_CONTRASEÑA = "Contraseña";
    public static final String COL_ID_EQUIPO = "IdEquipo";
    public static final String COL_ESTADO_REGISTRO = "EstadoRegistro";

    // Sentencia SQL para crear la tabla (sin foreign key por dependencia circular)
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_BOLETA + " INTEGER PRIMARY KEY, " +
                    COL_NOMBRE + " VARCHAR(50) NOT NULL, " +
                    COL_APELLIDO1 + " VARCHAR(50), " +
                    COL_APELLIDO2 + " VARCHAR(50), " +
                    COL_CORREO + " VARCHAR(50), " +
                    COL_CARRERA + " VARCHAR(50), " +
                    COL_SEMESTRE + " INTEGER, " +
                    COL_GRUPO + " VARCHAR(10), " +
                    COL_TURNO + " VARCHAR(10), " +
                    COL_CONTRASEÑA + " VARCHAR(255), " +
                    COL_ID_EQUIPO + " INTEGER NULL, " +
                    COL_ESTADO_REGISTRO + " VARCHAR(20) DEFAULT 'SinEquipo')";

    // Métodos CRUD
    public static long insertarAlumno(SQLiteDatabase db, int boleta, String nombre, String apellido1, String apellido2,
                                      String correo, String carrera, int semestre, String grupo, String turno,
                                      String contraseña, Integer idEquipo, String estadoRegistro) {
        ContentValues values = new ContentValues();
        values.put(COL_BOLETA, boleta);
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDO1, apellido1);
        values.put(COL_APELLIDO2, apellido2);
        values.put(COL_CORREO, correo);
        values.put(COL_CARRERA, carrera);
        values.put(COL_SEMESTRE, semestre);
        values.put(COL_GRUPO, grupo);
        values.put(COL_TURNO, turno);
        values.put(COL_CONTRASEÑA, contraseña);
        if (idEquipo != null) {
            values.put(COL_ID_EQUIPO, idEquipo);
        }
        values.put(COL_ESTADO_REGISTRO, estadoRegistro != null ? estadoRegistro : "SinEquipo");
        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosAlumnos(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerAlumnoPorBoleta(SQLiteDatabase db, int boleta) {
        return db.query(TABLE_NAME,
                null,
                COL_BOLETA + " = ?",
                new String[]{String.valueOf(boleta)},
                null, null, null);
    }

    public static Cursor obtenerAlumnosPorEquipo(SQLiteDatabase db, int idEquipo) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_EQUIPO + " = ?",
                new String[]{String.valueOf(idEquipo)},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerAlumnosPorEstado(SQLiteDatabase db, String estado) {
        return db.query(TABLE_NAME,
                null,
                COL_ESTADO_REGISTRO + " = ?",
                new String[]{estado},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerAlumnosPorCarrera(SQLiteDatabase db, String carrera) {
        return db.query(TABLE_NAME,
                null,
                COL_CARRERA + " = ?",
                new String[]{carrera},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerAlumnosPorGrupo(SQLiteDatabase db, String grupo) {
        return db.query(TABLE_NAME,
                null,
                COL_GRUPO + " = ?",
                new String[]{grupo},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerAlumnosPorTurno(SQLiteDatabase db, String turno) {
        return db.query(TABLE_NAME,
                null,
                COL_TURNO + " = ?",
                new String[]{turno},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarAlumnosPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE + " LIKE ? OR " + COL_APELLIDO1 + " LIKE ? OR " + COL_APELLIDO2 + " LIKE ?",
                new String[]{"%" + nombre + "%", "%" + nombre + "%", "%" + nombre + "%"},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarAlumnoPorCorreo(SQLiteDatabase db, String correo) {
        return db.query(TABLE_NAME,
                null,
                COL_CORREO + " = ?",
                new String[]{correo},
                null, null, null);
    }

    public static int actualizarAlumno(SQLiteDatabase db, int boleta, String nombre, String apellido1, String apellido2,
                                       String correo, String carrera, int semestre, String grupo, String turno,
                                       String contraseña, Integer idEquipo, String estadoRegistro) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDO1, apellido1);
        values.put(COL_APELLIDO2, apellido2);
        values.put(COL_CORREO, correo);
        values.put(COL_CARRERA, carrera);
        values.put(COL_SEMESTRE, semestre);
        values.put(COL_GRUPO, grupo);
        values.put(COL_TURNO, turno);
        values.put(COL_CONTRASEÑA, contraseña);
        if (idEquipo != null) {
            values.put(COL_ID_EQUIPO, idEquipo);
        } else {
            values.putNull(COL_ID_EQUIPO);
        }
        if (estadoRegistro != null) {
            values.put(COL_ESTADO_REGISTRO, estadoRegistro);
        }
        return db.update(TABLE_NAME, values,
                COL_BOLETA + " = ?",
                new String[]{String.valueOf(boleta)});
    }

    public static int asignarAlumnoAEquipo(SQLiteDatabase db, int boleta, int idEquipo) {
        ContentValues values = new ContentValues();
        values.put(COL_ID_EQUIPO, idEquipo);
        values.put(COL_ESTADO_REGISTRO, "ConEquipo");
        return db.update(TABLE_NAME, values,
                COL_BOLETA + " = ?",
                new String[]{String.valueOf(boleta)});
    }

    public static int removerAlumnoDeEquipo(SQLiteDatabase db, int boleta) {
        ContentValues values = new ContentValues();
        values.putNull(COL_ID_EQUIPO);
        values.put(COL_ESTADO_REGISTRO, "SinEquipo");
        return db.update(TABLE_NAME, values,
                COL_BOLETA + " = ?",
                new String[]{String.valueOf(boleta)});
    }

    public static int actualizarEstadoRegistro(SQLiteDatabase db, int boleta, String nuevoEstado) {
        ContentValues values = new ContentValues();
        values.put(COL_ESTADO_REGISTRO, nuevoEstado);
        return db.update(TABLE_NAME, values,
                COL_BOLETA + " = ?",
                new String[]{String.valueOf(boleta)});
    }

    public static int eliminarAlumno(SQLiteDatabase db, int boleta) {
        return db.delete(TABLE_NAME,
                COL_BOLETA + " = ?",
                new String[]{String.valueOf(boleta)});
    }

    /**
     * Verifica las credenciales de un alumno
     */
    public static Cursor verificarCredenciales(SQLiteDatabase db, int boleta, String contraseña) {
        return db.query(TABLE_NAME,
                null,
                COL_BOLETA + " = ? AND " + COL_CONTRASEÑA + " = ?",
                new String[]{String.valueOf(boleta), contraseña},
                null, null, null);
    }

    /**
     * Obtiene alumnos con información de su equipo (JOIN)
     */
    public static Cursor obtenerAlumnosConEquipo(SQLiteDatabase db) {
        String query = "SELECT a." + COL_BOLETA + ", " +
                "a." + COL_NOMBRE + ", " +
                "a." + COL_APELLIDO1 + ", " +
                "a." + COL_APELLIDO2 + ", " +
                "a." + COL_CORREO + ", " +
                "a." + COL_CARRERA + ", " +
                "a." + COL_SEMESTRE + ", " +
                "a." + COL_GRUPO + ", " +
                "a." + COL_TURNO + ", " +
                "a." + COL_ESTADO_REGISTRO + ", " +
                "e." + EquipoBD.COL_NOMBRE + " as nombre_equipo " +
                "FROM " + TABLE_NAME + " a " +
                "LEFT JOIN " + EquipoBD.TABLE_NAME + " e ON a." + COL_ID_EQUIPO + " = e." + EquipoBD.COL_ID_EQUIPO + " " +
                "ORDER BY a." + COL_NOMBRE + " ASC";
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene estadísticas de alumnos
     */
    public static Cursor obtenerEstadisticasAlumnos(SQLiteDatabase db) {
        String query = "SELECT " +
                "COUNT(*) as total_alumnos, " +
                "COUNT(CASE WHEN " + COL_ESTADO_REGISTRO + " = 'SinEquipo' THEN 1 END) as alumnos_sin_equipo, " +
                "COUNT(CASE WHEN " + COL_ESTADO_REGISTRO + " = 'ConEquipo' THEN 1 END) as alumnos_con_equipo, " +
                "COUNT(DISTINCT " + COL_CARRERA + ") as total_carreras, " +
                "COUNT(DISTINCT " + COL_GRUPO + ") as total_grupos " +
                "FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene alumnos disponibles para invitar (sin equipo)
     */
    public static Cursor obtenerAlumnosDisponibles(SQLiteDatabase db) {
        return db.query(TABLE_NAME,
                new String[]{COL_BOLETA, COL_NOMBRE, COL_APELLIDO1, COL_CARRERA, COL_GRUPO, COL_TURNO},
                COL_ESTADO_REGISTRO + " = ?",
                new String[]{"SinEquipo"},
                null, null, COL_NOMBRE + " ASC");
    }

    /**
     * Verifica si existe un alumno con la boleta especificada
     */
    public static boolean existeAlumnoPorBoleta(SQLiteDatabase db, int boleta) {
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COL_BOLETA},
                COL_BOLETA + " = ?",
                new String[]{String.valueOf(boleta)},
                null, null, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }
}