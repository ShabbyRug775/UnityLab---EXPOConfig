package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfesorBD {
    // Constantes para la tabla PROFESOR
    public static final String TABLE_NAME = "PROFESOR";
    public static final String COL_ID = "id_profesor";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_APELLIDOS = "apellidos";
    public static final String COL_CORREO = "correo";
    public static final String COL_ID_DEPTO = "id_departamento";
    public static final String COL_NUM_EMPLEADO = "num_empleado";
    public static final String COL_PASSWORD = "password";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE + " TEXT NOT NULL, " +
                    COL_APELLIDOS + " TEXT NOT NULL, " +
                    COL_CORREO + " TEXT UNIQUE, " +
                    COL_NUM_EMPLEADO + " TEXT UNIQUE, " +  // Nueva columna
                    COL_ID_DEPTO + " INTEGER, " +
                    COL_PASSWORD + " TEXT)";  // Nueva columna

    // Métodos CRUD
    public static long insertarProfesor(SQLiteDatabase db, String nombre, String apellidos, String correo, int numEmpleado,int idDepto, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDOS, apellidos);
        values.put(COL_CORREO, correo);
        values.put(COL_NUM_EMPLEADO, numEmpleado);
        values.put(COL_ID_DEPTO, idDepto);
        values.put(COL_PASSWORD, password);

        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosProfesores(SQLiteDatabase db) {
        return db.query(TABLE_NAME,
                null, null, null, null, null, null);
    }

    public static Cursor obtenerProfesorPorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static int actualizarProfesor(SQLiteDatabase db, int id, String nombre, String apellidos, String correo, int numEmpleado, int idDepto, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDOS, apellidos);
        values.put(COL_CORREO, correo);
        values.put(COL_NUM_EMPLEADO, numEmpleado);
        values.put(COL_ID_DEPTO, idDepto);
        values.put(COL_PASSWORD, password);
        return db.update(TABLE_NAME, values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarProfesor(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static Cursor buscarProfesorPorNumEmpleado(SQLiteDatabase db, String numEmpleado) {
        return db.query(TABLE_NAME,
                null,
                COL_NUM_EMPLEADO + " = ?",
                new String[]{numEmpleado},
                null, null, null);
    }
}