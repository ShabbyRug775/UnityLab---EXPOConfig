package com.example.unitylab_expoconfig.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DbmsSQLiteHelper extends SQLiteOpenHelper {
    // Versión de la base de datos
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "EscuelaDB";

    public DbmsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear todas las tablas
        db.execSQL(ProfesorBD.CREATE_TABLE);
        db.execSQL(EstudianteBD.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar todas las tablas existentes
        db.execSQL("DROP TABLE IF EXISTS " + ProfesorBD.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EstudianteBD.TABLE_NAME);

        // Crear las tablas nuevamente
        onCreate(db);
    }

    // ==================== MÉTODOS PARA PROFESOR ====================

    public long insertarProfesor(String nombre, String apellidos, String correo, int idDepto) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = ProfesorBD.insertarProfesor(db, nombre, apellidos, correo, idDepto);
        db.close();
        return id;
    }

    public Cursor obtenerTodosProfesores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProfesorBD.obtenerTodosProfesores(db);
    }

    public Cursor obtenerProfesorPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProfesorBD.obtenerProfesorPorId(db, id);
    }

    public Cursor buscarProfesorPorNumEmpleado(String numEmpleado) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProfesorBD.buscarProfesorPorNumEmpleado(db, numEmpleado);
    }

    public int actualizarProfesor(int id, String nombre, String apellidos, String correo, int idDepto) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = ProfesorBD.actualizarProfesor(db, id, nombre, apellidos, correo, idDepto);
        db.close();
        return rows;
    }

    public int eliminarProfesor(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = ProfesorBD.eliminarProfesor(db, id);
        db.close();
        return rows;
    }

    // ==================== MÉTODOS PARA ESTUDIANTE ====================

    public long insertarEstudiante(String nombre, String apellidos, String correo,
                                   String grupo, String semestre, String carrera) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = EstudianteBD.insertarEstudiante(db, nombre, apellidos, correo, grupo, semestre, carrera);
        db.close();
        return id;
    }

    public Cursor obtenerTodosEstudiantes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EstudianteBD.obtenerTodosEstudiantes(db);
    }

    public Cursor obtenerEstudiantePorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EstudianteBD.obtenerEstudiantePorId(db, id);
    }

    public Cursor buscarEstudiantesPorNombre(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EstudianteBD.buscarEstudiantesPorNombre(db, nombre);
    }

    public Cursor buscarEstudiantesPorBoleta(String boleta) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EstudianteBD.buscarEstudiantePorBoleta(db, boleta);
    }

    public int actualizarEstudiante(int id, String nombre, String apellidos, String correo,
                                    String grupo, String semestre, String carrera) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EstudianteBD.actualizarEstudiante(db, id, nombre, apellidos, correo, grupo, semestre, carrera);
        db.close();
        return rows;
    }

    public int eliminarEstudiante(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EstudianteBD.eliminarEstudiante(db, id);
        db.close();
        return rows;
    }

    // ==================== MÉTODOS ADICIONALES ====================

    public void cerrarConexion() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public void ejecutarConsultaSQL(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }
}