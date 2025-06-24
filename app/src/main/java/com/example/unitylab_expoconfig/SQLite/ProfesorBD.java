package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfesorBD {
    // Constantes para la tabla PROFESOR
    public static final String TABLE_NAME = "Profesor";
    public static final String COL_NUMERO_EMPLEADO = "NumeroEmpleado";
    public static final String COL_NOMBRE = "Nombre";
    public static final String COL_APELLIDO1 = "Apellido1";
    public static final String COL_APELLIDO2 = "Apellido2";
    public static final String COL_CONTRASEÑA = "Contraseña";
    public static final String COL_CORREO = "Correo";
    public static final String COL_TELEFONO = "Telefono";
    public static final String COL_ESTADO = "Estado";
    public static final String COL_ID_ACADEMIA = "IdAcademia";
    public static final String COL_NUMERO_EMPLEADO_ADMINISTRADOR = "NumeroEmpleadoAdministrador";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_NUMERO_EMPLEADO + " INTEGER PRIMARY KEY, " +
                    COL_NOMBRE + " VARCHAR(50) NOT NULL, " +
                    COL_APELLIDO1 + " VARCHAR(50), " +
                    COL_APELLIDO2 + " VARCHAR(50), " +
                    COL_CONTRASEÑA + " VARCHAR(255) NOT NULL, " +
                    COL_CORREO + " VARCHAR(50), " +
                    COL_TELEFONO + " VARCHAR(15), " +
                    COL_ESTADO + " VARCHAR(10), " +
                    COL_ID_ACADEMIA + " INTEGER, " +
                    COL_NUMERO_EMPLEADO_ADMINISTRADOR + " INTEGER, " +
                    "FOREIGN KEY (" + COL_ID_ACADEMIA + ") REFERENCES " + AcademiasBD.TABLE_NAME + " (" + AcademiasBD.COL_ID_ACADEMIA + "), " +
                    "FOREIGN KEY (" + COL_NUMERO_EMPLEADO_ADMINISTRADOR + ") REFERENCES " + AdministradorBD.TABLE_NAME + " (" + AdministradorBD.COL_NUMERO_EMPLEADO + "))";

    // Métodos CRUD
    public static long insertarProfesor(SQLiteDatabase db, int numeroEmpleado, String nombre, String apellido1, String apellido2,
                                        String contraseña, String correo, String telefono, String estado,
                                        Integer idAcademia, Integer numeroEmpleadoAdministrador) {
        ContentValues values = new ContentValues();
        values.put(COL_NUMERO_EMPLEADO, numeroEmpleado);
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDO1, apellido1);
        values.put(COL_APELLIDO2, apellido2);
        values.put(COL_CONTRASEÑA, contraseña);
        values.put(COL_CORREO, correo);
        values.put(COL_TELEFONO, telefono);
        values.put(COL_ESTADO, estado);
        if (idAcademia != null) {
            values.put(COL_ID_ACADEMIA, idAcademia);
        }
        if (numeroEmpleadoAdministrador != null) {
            values.put(COL_NUMERO_EMPLEADO_ADMINISTRADOR, numeroEmpleadoAdministrador);
        }
        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosProfesores(SQLiteDatabase db) {
        return db.query(TABLE_NAME,
                null, null, null, null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerProfesorPorNumeroEmpleado(SQLiteDatabase db, int numeroEmpleado) {
        return db.query(TABLE_NAME,
                null,
                COL_NUMERO_EMPLEADO + " = ?",
                new String[]{String.valueOf(numeroEmpleado)},
                null, null, null);
    }

    public static Cursor obtenerProfesoresPorAcademia(SQLiteDatabase db, int idAcademia) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_ACADEMIA + " = ?",
                new String[]{String.valueOf(idAcademia)},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerProfesoresPorEstado(SQLiteDatabase db, String estado) {
        return db.query(TABLE_NAME,
                null,
                COL_ESTADO + " = ?",
                new String[]{estado},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarProfesoresPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE + " LIKE ? OR " + COL_APELLIDO1 + " LIKE ? OR " + COL_APELLIDO2 + " LIKE ?",
                new String[]{"%" + nombre + "%", "%" + nombre + "%", "%" + nombre + "%"},
                null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor buscarProfesorPorCorreo(SQLiteDatabase db, String correo) {
        return db.query(TABLE_NAME,
                null,
                COL_CORREO + " = ?",
                new String[]{correo},
                null, null, null);
    }

    public static Cursor obtenerNombreCompletoProfesor(SQLiteDatabase db, int numeroEmpleado) {
        return db.query(TABLE_NAME,
                new String[]{COL_NOMBRE, COL_APELLIDO1, COL_APELLIDO2},
                COL_NUMERO_EMPLEADO + " = ?",
                new String[]{String.valueOf(numeroEmpleado)},
                null, null, null);
    }


    public static int actualizarProfesor(SQLiteDatabase db, int numeroEmpleado, String nombre, String apellido1, String apellido2,
                                         String contraseña, String correo, String telefono, String estado,
                                         Integer idAcademia, Integer numeroEmpleadoAdministrador) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDO1, apellido1);
        values.put(COL_APELLIDO2, apellido2);
        values.put(COL_CONTRASEÑA, contraseña);
        values.put(COL_CORREO, correo);
        values.put(COL_TELEFONO, telefono);
        values.put(COL_ESTADO, estado);
        if (idAcademia != null) {
            values.put(COL_ID_ACADEMIA, idAcademia);
        }
        if (numeroEmpleadoAdministrador != null) {
            values.put(COL_NUMERO_EMPLEADO_ADMINISTRADOR, numeroEmpleadoAdministrador);
        }
        return db.update(TABLE_NAME, values,
                COL_NUMERO_EMPLEADO + " = ?",
                new String[]{String.valueOf(numeroEmpleado)});
    }

    public static int cambiarEstadoProfesor(SQLiteDatabase db, int numeroEmpleado, String nuevoEstado) {
        ContentValues values = new ContentValues();
        values.put(COL_ESTADO, nuevoEstado);
        return db.update(TABLE_NAME, values,
                COL_NUMERO_EMPLEADO + " = ?",
                new String[]{String.valueOf(numeroEmpleado)});
    }

    public static int eliminarProfesor(SQLiteDatabase db, int numeroEmpleado) {
        return db.delete(TABLE_NAME,
                COL_NUMERO_EMPLEADO + " = ?",
                new String[]{String.valueOf(numeroEmpleado)});
    }

    /**
     * Verifica las credenciales de un profesor
     */
    public static Cursor verificarCredenciales(SQLiteDatabase db, int numeroEmpleado, String contraseña) {
        return db.query(TABLE_NAME,
                null,
                COL_NUMERO_EMPLEADO + " = ? AND " + COL_CONTRASEÑA + " = ?",
                new String[]{String.valueOf(numeroEmpleado), contraseña},
                null, null, null);
    }

    /**
     * Obtiene profesores con información de su academia (JOIN)
     */
    public static Cursor obtenerProfesoresConAcademia(SQLiteDatabase db) {
        String query = "SELECT p." + COL_NUMERO_EMPLEADO + ", " +
                "p." + COL_NOMBRE + ", " +
                "p." + COL_APELLIDO1 + ", " +
                "p." + COL_APELLIDO2 + ", " +
                "p." + COL_CORREO + ", " +
                "p." + COL_TELEFONO + ", " +
                "p." + COL_ESTADO + ", " +
                "a." + AcademiasBD.COL_NOMBRE + " as nombre_academia " +
                "FROM " + TABLE_NAME + " p " +
                "LEFT JOIN " + AcademiasBD.TABLE_NAME + " a ON p." + COL_ID_ACADEMIA + " = a." + AcademiasBD.COL_ID_ACADEMIA + " " +
                "ORDER BY p." + COL_NOMBRE + " ASC";
        return db.rawQuery(query, null);
    }
}