package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProyectoBD {
    // Constantes para la tabla PROYECTO
    public static final String TABLE_NAME = "Proyecto";
    public static final String COL_ID_PROYECTO = "IdProyecto";
    public static final String COL_NOMBRE = "Nombre";
    public static final String COL_DESCRIPCION = "Descripcion";
    public static final String COL_CLAVE = "Clave";
    public static final String COL_NUMERO_EMPLEADO_PROFESOR = "NumeroEmpleadoProfesor";
    public static final String COL_ID_EVENTO = "IdEvento";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_PROYECTO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE + " VARCHAR(100) NOT NULL, " +
                    COL_DESCRIPCION + " VARCHAR(255) NOT NULL, " +
                    COL_CLAVE + " VARCHAR(50) NOT NULL, " +
                    COL_NUMERO_EMPLEADO_PROFESOR + " INTEGER, " +
                    COL_ID_EVENTO + " INTEGER, " +
                    "FOREIGN KEY (" + COL_NUMERO_EMPLEADO_PROFESOR + ") REFERENCES " + ProfesorBD.TABLE_NAME + " (" + ProfesorBD.COL_NUMERO_EMPLEADO + "), " +
                    "FOREIGN KEY (" + COL_ID_EVENTO + ") REFERENCES " + EventoBD.TABLE_NAME + " (" + EventoBD.COL_ID_EVENTO + "))";

    // Métodos CRUD
    public static long insertarProyecto(SQLiteDatabase db, String nombre, String descripcion, String clave,
                                        Integer numeroEmpleadoProfesor, Integer idEvento) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_CLAVE, clave);
        if (numeroEmpleadoProfesor != null) {
            values.put(COL_NUMERO_EMPLEADO_PROFESOR, numeroEmpleadoProfesor);
        }
        if (idEvento != null) {
            values.put(COL_ID_EVENTO, idEvento);
        }
        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosProyectos(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerProyectoPorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_PROYECTO + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static Cursor obtenerProyectosPorProfesor(SQLiteDatabase db, int numeroEmpleadoProfesor) {
        return db.query(TABLE_NAME,
                null,
                COL_NUMERO_EMPLEADO_PROFESOR + " = ?",
                new String[]{String.valueOf(numeroEmpleadoProfesor)},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerProyectosPorEvento(SQLiteDatabase db, int idEvento) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_EVENTO + " = ?",
                new String[]{String.valueOf(idEvento)},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarProyectosPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE + " LIKE ?",
                new String[]{"%" + nombre + "%"},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarProyectosPorClave(SQLiteDatabase db, String clave) {
        return db.query(TABLE_NAME,
                null,
                COL_CLAVE + " LIKE ?",
                new String[]{"%" + clave + "%"},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarProyectosPorDescripcion(SQLiteDatabase db, String descripcion) {
        return db.query(TABLE_NAME,
                null,
                COL_DESCRIPCION + " LIKE ?",
                new String[]{"%" + descripcion + "%"},
                null, null, COL_NOMBRE + " ASC");
    }

    public static int actualizarProyecto(SQLiteDatabase db, int id, String nombre, String descripcion, String clave,
                                         Integer numeroEmpleadoProfesor, Integer idEvento) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_CLAVE, clave);
        if (numeroEmpleadoProfesor != null) {
            values.put(COL_NUMERO_EMPLEADO_PROFESOR, numeroEmpleadoProfesor);
        }
        if (idEvento != null) {
            values.put(COL_ID_EVENTO, idEvento);
        }
        return db.update(TABLE_NAME, values,
                COL_ID_PROYECTO + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarProyecto(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID_PROYECTO + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Obtiene proyectos con información de profesor y evento (JOIN)
     */
    public static Cursor obtenerProyectosConDetalles(SQLiteDatabase db) {
        String query = "SELECT p." + COL_ID_PROYECTO + ", " +
                "p." + COL_NOMBRE + ", " +
                "p." + COL_DESCRIPCION + ", " +
                "p." + COL_CLAVE + ", " +
                "pr." + ProfesorBD.COL_NOMBRE + " || ' ' || pr." + ProfesorBD.COL_APELLIDO1 + " as nombre_profesor, " +
                "e." + EventoBD.COL_NOMBRE + " as nombre_evento " +
                "FROM " + TABLE_NAME + " p " +
                "LEFT JOIN " + ProfesorBD.TABLE_NAME + " pr ON p." + COL_NUMERO_EMPLEADO_PROFESOR + " = pr." + ProfesorBD.COL_NUMERO_EMPLEADO + " " +
                "LEFT JOIN " + EventoBD.TABLE_NAME + " e ON p." + COL_ID_EVENTO + " = e." + EventoBD.COL_ID_EVENTO + " " +
                "ORDER BY p." + COL_NOMBRE + " ASC";
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene el número de equipos registrados por proyecto
     */
    public static Cursor obtenerProyectosConConteoEquipos(SQLiteDatabase db) {
        String query = "SELECT p." + COL_ID_PROYECTO + ", " +
                "p." + COL_NOMBRE + ", " +
                "p." + COL_DESCRIPCION + ", " +
                "p." + COL_CLAVE + ", " +
                "COUNT(eq." + EquipoBD.COL_ID_EQUIPO + ") as numero_equipos " +
                "FROM " + TABLE_NAME + " p " +
                "LEFT JOIN " + EquipoBD.TABLE_NAME + " eq ON p." + COL_ID_PROYECTO + " = eq." + EquipoBD.COL_ID_PROYECTO + " " +
                "GROUP BY p." + COL_ID_PROYECTO + ", p." + COL_NOMBRE + ", p." + COL_DESCRIPCION + ", p." + COL_CLAVE + " " +
                "ORDER BY p." + COL_NOMBRE + " ASC";
        return db.rawQuery(query, null);
    }

    /**
     * Verifica si existe un proyecto con la clave especificadau
     */
    public static boolean existeProyectoPorClave(SQLiteDatabase db, String clave) {
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COL_ID_PROYECTO},
                COL_CLAVE + " = ?",
                new String[]{clave},
                null, null, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }


}