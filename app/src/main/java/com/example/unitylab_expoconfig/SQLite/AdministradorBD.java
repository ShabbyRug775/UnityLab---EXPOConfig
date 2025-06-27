package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AdministradorBD {
    // Constantes para la tabla ADMINISTRADOR
    public static final String TABLE_NAME = "Administrador";
    public static final String COL_NUMERO_EMPLEADO = "NumeroEmpleado";
    public static final String COL_NOMBRE = "Nombre";
    public static final String COL_APELLIDO1 = "Apellido1";
    public static final String COL_APELLIDO2 = "Apellido2";
    public static final String COL_CONTRASEÑA = "Contraseña";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_NUMERO_EMPLEADO + " INTEGER PRIMARY KEY, " +
                    COL_NOMBRE + " VARCHAR(50), " +
                    COL_APELLIDO1 + " VARCHAR(50), " +
                    COL_APELLIDO2 + " VARCHAR(50), " +
                    COL_CONTRASEÑA + " VARCHAR(255))";

    /**
     * Inserta un nuevo administrador en la base de datos
     * @param db Instancia de SQLiteDatabase
     * @param numeroEmpleado Número de empleado (PRIMARY KEY)
     * @param nombre Nombre del administrador
     * @param apellido1 Primer apellido
     * @param apellido2 Segundo apellido
     * @param contraseña Contraseña del administrador
     * @return ID del nuevo registro insertado o -1 si hubo error
     */
    public static long insertarAdministrador(SQLiteDatabase db, int numeroEmpleado, String nombre,
                                             String apellido1, String apellido2, String contraseña) {
        ContentValues values = new ContentValues();
        values.put(COL_NUMERO_EMPLEADO, numeroEmpleado);
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDO1, apellido1);
        values.put(COL_APELLIDO2, apellido2);
        values.put(COL_CONTRASEÑA, contraseña);
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
     * Obtiene un administrador por su número de empleado
     * @param db Instancia de SQLiteDatabase
     * @param numeroEmpleado Número de empleado
     * @return Cursor con el resultado o null si no existe
     */
    public static Cursor obtenerAdministradorPorNumeroEmpleado(SQLiteDatabase db, int numeroEmpleado) {
        return db.query(TABLE_NAME,
                null,
                COL_NUMERO_EMPLEADO + " = ?",
                new String[]{String.valueOf(numeroEmpleado)},
                null, null, null);
    }

    /**
     * Actualiza los datos de un administrador
     * @param db Instancia de SQLiteDatabase
     * @param numeroEmpleado Número de empleado del administrador a actualizar
     * @param nombre Nuevo nombre
     * @param apellido1 Nuevo primer apellido
     * @param apellido2 Nuevo segundo apellido
     * @param contraseña Nueva contraseña
     * @return Número de filas afectadas
     */
    public static int actualizarAdministrador(SQLiteDatabase db, int numeroEmpleado, String nombre,
                                              String apellido1, String apellido2, String contraseña) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_APELLIDO1, apellido1);
        values.put(COL_APELLIDO2, apellido2);
        values.put(COL_CONTRASEÑA, contraseña);
        return db.update(TABLE_NAME, values,
                COL_NUMERO_EMPLEADO + " = ?",
                new String[]{String.valueOf(numeroEmpleado)});
    }

    /**
     * Elimina un administrador de la base de datos
     * @param db Instancia de SQLiteDatabase
     * @param numeroEmpleado Número de empleado del administrador a eliminar
     * @return Número de filas afectadas
     */
    public static int eliminarAdministrador(SQLiteDatabase db, int numeroEmpleado) {
        return db.delete(TABLE_NAME,
                COL_NUMERO_EMPLEADO + " = ?",
                new String[]{String.valueOf(numeroEmpleado)});
    }

    /**
     * Verifica las credenciales de un administrador
     * @param db Instancia de SQLiteDatabase
     * @param numeroEmpleado Número de empleado
     * @param contraseña Contraseña
     * @return Cursor con los datos del administrador si las credenciales son correctas, null en caso contrario
     */
    public static Cursor verificarCredenciales(SQLiteDatabase db, int numeroEmpleado, String contraseña) {
        return db.query(TABLE_NAME,
                null,
                COL_NUMERO_EMPLEADO + " = ? AND " + COL_CONTRASEÑA + " = ?",
                new String[]{String.valueOf(numeroEmpleado), contraseña},
                null, null, null);
    }

    /**
     * Busca administradores por nombre
     * @param db Instancia de SQLiteDatabase
     * @param nombre Nombre a buscar
     * @return Cursor con los resultados
     */
    public static Cursor buscarAdministradoresPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE + " LIKE ? OR " + COL_APELLIDO1 + " LIKE ? OR " + COL_APELLIDO2 + " LIKE ?",
                new String[]{"%" + nombre + "%", "%" + nombre + "%", "%" + nombre + "%"},
                null, null, COL_NOMBRE + " ASC");
    }
}