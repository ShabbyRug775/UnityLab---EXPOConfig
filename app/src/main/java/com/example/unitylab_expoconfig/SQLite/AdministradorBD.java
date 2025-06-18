package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AdministradorBD {
    // Constantes para la tabla ADMINISTRADOR
    public static final String TABLE_NAME = "ADMINISTRADOR";
    public static final String COL_ID = "id_administrador";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_NUM_EMPLEADO = "num_empleado";
    public static final String COL_PASSWORD = "password";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE + " TEXT NOT NULL, " +
                    COL_NUM_EMPLEADO + " TEXT UNIQUE NOT NULL, " +  // Número de empleado único
                    COL_PASSWORD + " TEXT NOT NULL)";  // Contraseña en texto (debería ser hash en producción)

    /**
     * Inserta un nuevo administrador en la base de datos
     * @param db Instancia de SQLiteDatabase
     * @param nombre Nombre completo del administrador
     * @param numEmpleado Número de empleado único
     * @param password Contraseña del administrador
     * @return ID del nuevo registro insertado o -1 si hubo error
     */
    public static long insertarAdministrador(SQLiteDatabase db, String nombre, String numEmpleado, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_NUM_EMPLEADO, numEmpleado);
        values.put(COL_PASSWORD, password);

        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Obtiene todos los administradores de la base de datos
     * @param db Instancia de SQLiteDatabase
     * @return Cursor con los resultados
     */
    public static Cursor obtenerTodosAdministradores(SQLiteDatabase db) {
        return db.query(TABLE_NAME,
                null, null, null, null, null, COL_NOMBRE + " ASC");
    }

    /**
     * Obtiene un administrador por su ID
     * @param db Instancia de SQLiteDatabase
     * @param id ID del administrador
     * @return Cursor con el resultado o null si no existe
     */
    public static Cursor obtenerAdministradorPorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    /**
     * Obtiene un administrador por su número de empleado
     * @param db Instancia de SQLiteDatabase
     * @param numEmpleado Número de empleado a buscar
     * @return Cursor con el resultado o null si no existe
     */
    public static Cursor obtenerAdministradorPorNumEmpleado(SQLiteDatabase db, String numEmpleado) {
        return db.query(TABLE_NAME,
                null,
                COL_NUM_EMPLEADO + " = ?",
                new String[]{numEmpleado},
                null, null, null);
    }

    /**
     * Actualiza los datos de un administrador
     * @param db Instancia de SQLiteDatabase
     * @param id ID del administrador a actualizar
     * @param nombre Nuevo nombre
     * @param numEmpleado Nuevo número de empleado
     * @param password Nueva contraseña
     * @return Número de filas afectadas
     */
    public static int actualizarAdministrador(SQLiteDatabase db, int id, String nombre, String numEmpleado, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_NUM_EMPLEADO, numEmpleado);
        values.put(COL_PASSWORD, password);

        return db.update(TABLE_NAME, values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Elimina un administrador de la base de datos
     * @param db Instancia de SQLiteDatabase
     * @param id ID del administrador a eliminar
     * @return Número de filas afectadas
     */
    public static int eliminarAdministrador(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Verifica las credenciales de un administrador
     * @param db Instancia de SQLiteDatabase
     * @param numEmpleado Número de empleado
     * @param password Contraseña
     * @return Cursor con los datos del administrador si las credenciales son correctas, null en caso contrario
     */
    public static Cursor verificarCredenciales(SQLiteDatabase db, String numEmpleado, String password) {
        return db.query(TABLE_NAME,
                null,
                COL_NUM_EMPLEADO + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{numEmpleado, password},
                null, null, null);
    }
}