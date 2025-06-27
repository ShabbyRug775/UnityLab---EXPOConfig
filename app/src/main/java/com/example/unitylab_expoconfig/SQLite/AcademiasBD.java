package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AcademiasBD {
    // Constantes para la tabla ACADEMIAS
    public static final String TABLE_NAME = "Academias";
    public static final String COL_ID_ACADEMIA = "IdAcademia";
    public static final String COL_NOMBRE = "Nombre";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_ACADEMIA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE + " VARCHAR(50) NOT NULL)";

    // Métodos CRUD
    public static long insertarAcademia(SQLiteDatabase db, String nombre) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodasAcademias(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null, COL_NOMBRE + " ASC");
    }

    public static Cursor obtenerAcademiaPorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_ACADEMIA + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static Cursor buscarAcademiasPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE + " LIKE ?",
                new String[]{"%" + nombre + "%"},
                null, null, COL_NOMBRE + " ASC");
    }

    public static int actualizarAcademia(SQLiteDatabase db, int id, String nombre) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        return db.update(TABLE_NAME, values,
                COL_ID_ACADEMIA + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarAcademia(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID_ACADEMIA + " = ?",
                new String[]{String.valueOf(id)});
    }
}