package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.sqlite.SQLiteException;

public class DbmsSQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbmsSQLiteHelper";
    // Versión de la base de datos
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "EscuelaDB";

    public DbmsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear todas las tablas
        db.execSQL(ProfesorBD.CREATE_TABLE);
        db.execSQL(EstudianteBD.CREATE_TABLE);
        db.execSQL(ProyectoBD.CREATE_TABLE);
        db.execSQL(AdministradorBD.CREATE_TABLE);
        db.execSQL(EquipoDB.CREATE_TABLE);

        // Insertar administrador por defecto (opcional)
        insertarAdminPorDefecto(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Agregar la tabla de proyectos si se está actualizando desde versión 1
            db.execSQL(ProyectoBD.CREATE_TABLE);
            db.execSQL(AdministradorBD.CREATE_TABLE);
            db.execSQL(EquipoDB.CREATE_TABLE);
        }

    }

    // Metodo para insertar un administrador por defecto
    private void insertarAdminPorDefecto(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(AdministradorBD.COL_NOMBRE, "Administrador Principal");
        values.put(AdministradorBD.COL_NUM_EMPLEADO, "ADM001");
        values.put(AdministradorBD.COL_PASSWORD, "admin123"); // En producción usar hash

        try {
            db.insert(AdministradorBD.TABLE_NAME, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al insertar admin por defecto: " + e.getMessage());
        }
    }

    // ==================== MÉTODOS PARA PROFESOR ====================

    public long insertarProfesor(String nombre, String apellidos, String correo,
                                 int numEmpleado, int idDepto, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = ProfesorBD.insertarProfesor(db, nombre, apellidos, correo, numEmpleado, idDepto, password);
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

    // Metodo para buscar profesor por número de empleado
    public Cursor buscarProfesorPorNumEmpleado(String numEmpleado) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProfesorBD.buscarProfesorPorNumEmpleado(db, numEmpleado);
    }

    public int actualizarProfesor(int id, String nombre, String apellidos, String correo, int numEmpleado, int idDepto, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = ProfesorBD.actualizarProfesor(db, id, nombre, apellidos, correo, numEmpleado, idDepto, password);
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
                                   String boleta, String grupo, String semestre,
                                   String carrera, String turno,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = EstudianteBD.insertarEstudiante(db, nombre, apellidos, correo,
                boleta, grupo, semestre, carrera, turno, password);
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

    public int actualizarEstudiante(int id, String nombre, String apellidos, String correo, String boleta,
                                    String grupo, String semestre, String carrera, String turno, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EstudianteBD.actualizarEstudiante(db, id, nombre, apellidos, correo, boleta, grupo, semestre, carrera, turno, password);
        db.close();
        return rows;
    }

    public int eliminarEstudiante(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EstudianteBD.eliminarEstudiante(db, id);
        db.close();
        return rows;
    }

    // ==================== MÉTODOS PARA PROYECTO ====================

    public long insertarProyecto(String nombreProyecto, String descripcion,
                                 int idProfesor, int idEquipo, String fechaCreacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = ProyectoBD.insertarProyecto(db, nombreProyecto, descripcion,
                idProfesor, idEquipo, fechaCreacion);
        db.close();
        return id;
    }

    public Cursor obtenerTodosProyectos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.obtenerTodosProyectos(db);
    }

    public Cursor obtenerProyectoPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.obtenerProyectoPorId(db, id);
    }

    public Cursor obtenerProyectosPorProfesor(int idProfesor) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.obtenerProyectosPorProfesor(db, idProfesor);
    }

    public Cursor buscarProyectosPorNombre(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.buscarProyectosPorNombre(db, nombre);
    }

    public int actualizarProyecto(int id, String nombreProyecto, String descripcion,
                                  int idProfesor, int idEquipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = ProyectoBD.actualizarProyecto(db, id, nombreProyecto, descripcion,
                idProfesor, idEquipo);
        db.close();
        return rows;
    }

    public int eliminarProyecto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = ProyectoBD.eliminarProyecto(db, id);
        db.close();
        return rows;
    }

    // ==================== MÉTODOS PARA EQUIPO ====================

    public long insertarEquipo(String nombre, String nombreProyecto, int numAlumnos,
                               String descripcion, int lugar, String cartel,
                               int cantEval, float promedio, int cantVisitas) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = EquipoDB.insertarEquipo(db, nombre, nombreProyecto, numAlumnos,
                descripcion, lugar, cartel,
                cantEval, promedio, cantVisitas);
        db.close();
        return id;
    }

    public Cursor obtenerTodosEquipos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoDB.obtenerTodosEquipos(db);
    }

    public Cursor obtenerEquipoPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoDB.obtenerEquipoPorId(db, id);
    }

    public Cursor buscarEquiposPorNombre(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoDB.buscarEquiposPorNombre(db, nombre);
    }

    public Cursor buscarEquiposPorProyecto(String proyecto) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoDB.buscarEquiposPorProyecto(db, proyecto);
    }

    public int actualizarEquipo(int id, String nombre, String nombreProyecto, int numAlumnos,
                                String descripcion, int lugar, String cartel,
                                int cantEval, float promedio, int cantVisitas) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EquipoDB.actualizarEquipo(db, id, nombre, nombreProyecto, numAlumnos,
                descripcion, lugar, cartel,
                cantEval, promedio, cantVisitas);
        db.close();
        return rows;
    }

    public int actualizarPromedioEquipo(int id, float nuevoPromedio, int nuevaEvaluacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EquipoDB.actualizarPromedio(db, id, nuevoPromedio, nuevaEvaluacion);
        db.close();
        return rows;
    }

    public int incrementarVisitasEquipo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EquipoDB.incrementarVisitas(db, id);
        db.close();
        return rows;
    }

    public int eliminarEquipo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EquipoDB.eliminarEquipo(db, id);
        db.close();
        return rows;
    }

    public Cursor obtenerEquiposPorPromedio() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoDB.obtenerEquiposPorPromedio(db);
    }

    public Cursor obtenerEquiposPorLugar(int lugar) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoDB.obtenerEquiposPorLugar(db, lugar);
    }

    // ==================== MÉTODOS PARA ADMINISTRADOR ====================

    /**
     * Inserta un nuevo administrador en la base de datos
     * @param nombre Nombre completo del administrador
     * @param numEmpleado Número de empleado único
     * @param password Contraseña del administrador
     * @return ID del nuevo registro insertado o -1 si hubo error
     */
    public long insertarAdministrador(String nombre, String numEmpleado, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = AdministradorBD.insertarAdministrador(db, nombre, numEmpleado, password);
        db.close();
        return id;
    }

    /**
     * Obtiene todos los administradores de la base de datos
     * @return Cursor con los resultados ordenados por nombre
     */
    public Cursor obtenerTodosAdministradores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return AdministradorBD.obtenerTodosAdministradores(db);
    }

    /**
     * Obtiene un administrador por su ID
     * @param id ID del administrador
     * @return Cursor con el resultado o null si no existe
     */
    public Cursor obtenerAdministradorPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AdministradorBD.obtenerAdministradorPorId(db, id);
    }

    /**
     * Obtiene un administrador por su número de empleado
     * @param numEmpleado Número de empleado a buscar
     * @return Cursor con el resultado o null si no existe
     */
    public Cursor obtenerAdministradorPorNumEmpleado(String numEmpleado) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AdministradorBD.obtenerAdministradorPorNumEmpleado(db, numEmpleado);
    }

    /**
     * Actualiza los datos de un administrador
     * @param id ID del administrador a actualizar
     * @param nombre Nuevo nombre
     * @param numEmpleado Nuevo número de empleado
     * @param password Nueva contraseña
     * @return Número de filas afectadas
     */
    public int actualizarAdministrador(int id, String nombre, String numEmpleado, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = AdministradorBD.actualizarAdministrador(db, id, nombre, numEmpleado, password);
        db.close();
        return rows;
    }

    /**
     * Elimina un administrador de la base de datos
     * @param id ID del administrador a eliminar
     * @return Número de filas afectadas
     */
    public int eliminarAdministrador(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = AdministradorBD.eliminarAdministrador(db, id);
        db.close();
        return rows;
    }

    /**
     * Verifica las credenciales de un administrador
     * @param numEmpleado Número de empleado
     * @param password Contraseña
     * @return Cursor con los datos del administrador si las credenciales son correctas, null en caso contrario
     */
    public Cursor verificarCredencialesAdministrador(String numEmpleado, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AdministradorBD.verificarCredenciales(db, numEmpleado, password);
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
    // Metodo para verificar si la tabla PROYECTO existe
    public boolean verificarTablaProyecto() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='PROYECTO'", null);
            boolean existe = cursor != null && cursor.moveToFirst();
            Log.d(TAG, "Tabla PROYECTO existe: " + existe);
            return existe;
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar tabla PROYECTO: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Metodo para verificar la estructura de la tabla
    public void verificarEstructuraTablas() {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "=== VERIFICANDO ESTRUCTURA DE TABLAS ===");

        // Verificar todas las tablas
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor.moveToFirst()) {
            do {
                String tableName = cursor.getString(0);
                Log.d(TAG, "Tabla encontrada: " + tableName);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Verificar estructura específica de PROYECTO
        try {
            cursor = db.rawQuery("PRAGMA table_info(PROYECTO)", null);
            if (cursor.moveToFirst()) {
                Log.d(TAG, "Columnas de tabla PROYECTO:");
                do {
                    String columnName = cursor.getString(1);
                    String columnType = cursor.getString(2);
                    Log.d(TAG, "- " + columnName + " (" + columnType + ")");
                } while (cursor.moveToNext());
            } else {
                Log.e(TAG, "Tabla PROYECTO no existe o no tiene columnas");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar estructura de PROYECTO: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}