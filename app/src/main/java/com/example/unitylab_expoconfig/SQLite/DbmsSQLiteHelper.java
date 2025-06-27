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
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "ExpoConfigDB";

    public DbmsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creando base de datos ExpoConfig...");

        try {
            // Crear las tablas en el orden correcto para evitar problemas de foreign key
            db.execSQL(AcademiasBD.CREATE_TABLE);
            Log.d(TAG, "Tabla Academias creada");

            db.execSQL(AdministradorBD.CREATE_TABLE);
            Log.d(TAG, "Tabla Administrador creada");

            db.execSQL(ProfesorBD.CREATE_TABLE);
            Log.d(TAG, "Tabla Profesor creada");

            db.execSQL(EventoBD.CREATE_TABLE);
            Log.d(TAG, "Tabla Evento creada");

            db.execSQL(ProyectoBD.CREATE_TABLE);
            Log.d(TAG, "Tabla Proyecto creada");

            // Crear Alumno primero (sin foreign key a Equipo por dependencia circular)
            db.execSQL(AlumnoBD.CREATE_TABLE);
            Log.d(TAG, "Tabla Alumno creada");

            // Crear Equipo después (con foreign key a Alumno)
            db.execSQL(EquipoBD.CREATE_TABLE);
            Log.d(TAG, "Tabla Equipo creada");

            db.execSQL(VisitanteBD.CREATE_TABLE);
            Log.d(TAG, "Tabla Visitante creada");

            db.execSQL(EvaluacionBD.CREATE_TABLE);
            Log.d(TAG, "Tabla Evaluacion creada");

            // Insertar datos de prueba
            insertarDatosPorDefecto(db);

            Log.d(TAG, "Base de datos ExpoConfig creada exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error al crear la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Actualizando base de datos de versión " + oldVersion + " a " + newVersion);

        if (oldVersion < 3) {
            // Eliminar todas las tablas y recrear
            db.execSQL("DROP TABLE IF EXISTS " + EvaluacionBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + VisitanteBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AlumnoBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + EquipoBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ProyectoBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ProfesorBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AdministradorBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + EventoBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AcademiasBD.TABLE_NAME);

            // Recrear todas las tablas
            onCreate(db);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Habilitar foreign keys
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    // Metodo para insertar datos por defecto
    private void insertarDatosPorDefecto(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Insertando datos por defecto...");

            // Insertar Academias
            AcademiasBD.insertarAcademia(db, "Academia de Ingeniería en Sistemas");
            AcademiasBD.insertarAcademia(db, "Academia de Ciencias Básicas");
            AcademiasBD.insertarAcademia(db, "Academia de Matemáticas");
            AcademiasBD.insertarAcademia(db, "Academia de Ingeniería Industrial");
            AcademiasBD.insertarAcademia(db, "Academia de Ciencias Sociales");

            // Insertar Administradores
            AdministradorBD.insertarAdministrador(db, 10001, "Carlos", "Mendoza", "López", "admin123");
            AdministradorBD.insertarAdministrador(db, 10002, "María", "González", "Ramírez", "admin456");
            AdministradorBD.insertarAdministrador(db, 10003, "Roberto", "Sánchez", "Torres", "admin789");

            // Insertar Profesores
            ProfesorBD.insertarProfesor(db, 20001, "Ana", "García", "Hernández", "prof123", "ana.garcia@instituto.edu.mx", "555-0101", "Activo", 1, 10001);
            ProfesorBD.insertarProfesor(db, 20002, "Luis", "Martínez", "Pérez", "prof456", "luis.martinez@instituto.edu.mx", "555-0102", "Activo", 1, 10001);
            ProfesorBD.insertarProfesor(db, 20003, "Elena", "Rodríguez", "Morales", "prof789", "elena.rodriguez@instituto.edu.mx", "555-0103", "Activo", 2, 10002);

            // Insertar Eventos
            EventoBD.insertarEvento(db, "Expo Ciencias 2025", "Exposición anual de proyectos científicos", "Auditorio Principal", "2025-05-15", "2025-05-17", "Lunes a Miércoles", "09:00:00", "18:00:00", 0);
            EventoBD.insertarEvento(db, "Feria Tecnológica", "Muestra de proyectos tecnológicos innovadores", "Centro de Convenciones", "2025-06-10", "2025-06-12", "Martes a Jueves", "10:00:00", "19:00:00", 0);

            // Insertar Proyectos
            ProyectoBD.insertarProyecto(db, "Sistema de Gestión Académica", "Plataforma web para administración de calificaciones y horarios estudiantiles", "SGA-2025-001", 20001, 1);
            ProyectoBD.insertarProyecto(db, "Robot Clasificador de Residuos", "Sistema automatizado para separación inteligente de basura reciclable", "RCR-2025-002", 20002, 2);

            Log.d(TAG, "Datos por defecto insertados exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error al insertar datos por defecto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== METODOS PARA ACADEMIAS ====================

    public long insertarAcademia(String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = AcademiasBD.insertarAcademia(db, nombre);
        db.close();
        return id;
    }

    public Cursor obtenerTodasAcademias() {
        SQLiteDatabase db = this.getReadableDatabase();
        return AcademiasBD.obtenerTodasAcademias(db);
    }

    public Cursor obtenerAcademiaPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AcademiasBD.obtenerAcademiaPorId(db, id);
    }

    public int actualizarAcademia(int id, String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = AcademiasBD.actualizarAcademia(db, id, nombre);
        db.close();
        return rows;
    }

    public int eliminarAcademia(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = AcademiasBD.eliminarAcademia(db, id);
        db.close();
        return rows;
    }

    // ==================== MetodoS PARA ADMINISTRADOR ====================

    public long insertarAdministrador(int numeroEmpleado, String nombre, String apellido1, String apellido2, String contraseña) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = AdministradorBD.insertarAdministrador(db, numeroEmpleado, nombre, apellido1, apellido2, contraseña);
        db.close();
        return id;
    }

    public Cursor obtenerTodosAdministradores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return AdministradorBD.obtenerTodosAdministradores(db);
    }

    public Cursor obtenerAdministradorPorNumeroEmpleado(int numeroEmpleado) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AdministradorBD.obtenerAdministradorPorNumeroEmpleado(db, numeroEmpleado);
    }

    public Cursor verificarCredencialesAdministrador(int numeroEmpleado, String contraseña) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AdministradorBD.verificarCredenciales(db, numeroEmpleado, contraseña);
    }

    public int actualizarAdministrador(int numeroEmpleado, String nombre, String apellido1, String apellido2, String contraseña) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = AdministradorBD.actualizarAdministrador(db, numeroEmpleado, nombre, apellido1, apellido2, contraseña);
        db.close();
        return rows;
    }

    public int eliminarAdministrador(int numeroEmpleado) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = AdministradorBD.eliminarAdministrador(db, numeroEmpleado);
        db.close();
        return rows;
    }

    // ==================== MetodoS PARA PROFESOR ====================

    public long insertarProfesor(int numeroEmpleado, String nombre, String apellido1, String apellido2,
                                 String contraseña, String correo, String telefono, String estado,
                                 Integer idAcademia, Integer numeroEmpleadoAdministrador) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = ProfesorBD.insertarProfesor(db, numeroEmpleado, nombre, apellido1, apellido2, contraseña, correo, telefono, estado, idAcademia, numeroEmpleadoAdministrador);
        db.close();
        return id;
    }

    public Cursor obtenerTodosProfesores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProfesorBD.obtenerTodosProfesores(db);
    }

    public Cursor obtenerProfesorPorNumeroEmpleado(int numeroEmpleado) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProfesorBD.obtenerProfesorPorNumeroEmpleado(db, numeroEmpleado);
    }

    public Cursor obtenerNombreCompletoProfesor(int numeroEmpleado) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProfesorBD.obtenerNombreCompletoProfesor(db, numeroEmpleado);
    }

    public Cursor obtenerProfesoresPorAcademia(int idAcademia) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProfesorBD.obtenerProfesoresPorAcademia(db, idAcademia);
    }

    public Cursor verificarCredencialesProfesor(int numeroEmpleado, String contraseña) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProfesorBD.verificarCredenciales(db, numeroEmpleado, contraseña);
    }

    public Cursor obtenerProfesoresConAcademia() {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProfesorBD.obtenerProfesoresConAcademia(db);
    }

    public int actualizarProfesor(int numeroEmpleado, String nombre, String apellido1, String apellido2,
                                  String contraseña, String correo, String telefono, String estado,
                                  Integer idAcademia, Integer numeroEmpleadoAdministrador) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = ProfesorBD.actualizarProfesor(db, numeroEmpleado, nombre, apellido1, apellido2, contraseña, correo, telefono, estado, idAcademia, numeroEmpleadoAdministrador);
        db.close();
        return rows;
    }

    public int eliminarProfesor(int numeroEmpleado) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = ProfesorBD.eliminarProfesor(db, numeroEmpleado);
        db.close();
        return rows;
    }

    // ==================== MetodoS PARA EVENTO ====================

    public long insertarEvento(String nombre, String descripcion, String ubicacion, String fechaInicio,
                               String fechaFin, String diasPresentacion, String horaInicio, String horaFin, int asistencia) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = EventoBD.insertarEvento(db, nombre, descripcion, ubicacion, fechaInicio, fechaFin, diasPresentacion, horaInicio, horaFin, asistencia);
        db.close();
        return id;
    }

    public Cursor obtenerTodosEventos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EventoBD.obtenerTodosEventos(db);
    }

    public Cursor obtenerEventoPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EventoBD.obtenerEventoPorId(db, id);
    }

    public Cursor obtenerEventosActivos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EventoBD.obtenerEventosActivos(db);
    }

    public Cursor obtenerEventosProximos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EventoBD.obtenerEventosProximos(db);
    }

    public int actualizarEvento(int id, String nombre, String descripcion, String ubicacion, String fechaInicio,
                                String fechaFin, String diasPresentacion, String horaInicio, String horaFin, int asistencia) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EventoBD.actualizarEvento(db, id, nombre, descripcion, ubicacion, fechaInicio, fechaFin, diasPresentacion, horaInicio, horaFin, asistencia);
        db.close();
        return rows;
    }

    public int incrementarAsistenciaEvento(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EventoBD.incrementarAsistencia(db, id);
        db.close();
        return rows;
    }

    public int eliminarEvento(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EventoBD.eliminarEvento(db, id);
        db.close();
        return rows;
    }

    // ==================== MetodoS PARA PROYECTO ====================

    public long insertarProyecto(String nombre, String descripcion, String clave, Integer numeroEmpleadoProfesor, Integer idEvento) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = ProyectoBD.insertarProyecto(db, nombre, descripcion, clave, numeroEmpleadoProfesor, idEvento);
        db.close();
        return id;
    }

    //Obtener proyecto por clave
    public Cursor buscarProyectosPorClave(String clave) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.buscarProyectosPorClave(db, clave);
    }
    public Cursor buscarProyectosPorNombre(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.buscarProyectosPorNombre(db, nombre);
    }


    public Cursor obtenerTodosProyectos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.obtenerTodosProyectos(db);
    }

    public Cursor obtenerProyectoPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.obtenerProyectoPorId(db, id);
    }

    public Cursor obtenerProyectosPorProfesor(int numeroEmpleadoProfesor) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.obtenerProyectosPorProfesor(db, numeroEmpleadoProfesor);
    }

    public Cursor obtenerProyectosPorEvento(int idEvento) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.obtenerProyectosPorEvento(db, idEvento);
    }

    public Cursor obtenerProyectosConDetalles() {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.obtenerProyectosConDetalles(db);
    }

    public Cursor obtenerProyectosConConteoEquipos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.obtenerProyectosConConteoEquipos(db);
    }

    public boolean existeProyectoPorClave(String clave) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ProyectoBD.existeProyectoPorClave(db, clave);
    }

    public int actualizarProyecto(int id, String nombre, String descripcion, String clave, Integer numeroEmpleadoProfesor, Integer idEvento) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = ProyectoBD.actualizarProyecto(db, id, nombre, descripcion, clave, numeroEmpleadoProfesor, idEvento);
        db.close();
        return rows;
    }

    public int eliminarProyecto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = ProyectoBD.eliminarProyecto(db, id);
        db.close();
        return rows;
    }

    // ==================== MetodoS PARA EQUIPO ====================

    public long insertarEquipo(String nombre, String nombreProyecto, int numeroAlumnos, String descripcion,
                               String turno, String lugar, String cartel, int cantEval, double promedio,
                               int cantVisitas, Integer idProyecto, Integer boletaLider, String estadoEquipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = EquipoBD.insertarEquipo(db, nombre, nombreProyecto, numeroAlumnos, descripcion, turno, lugar, cartel, cantEval, promedio, cantVisitas, idProyecto, boletaLider, estadoEquipo);
        db.close();
        return id;
    }

    public Cursor obtenerTodosEquipos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoBD.obtenerTodosEquipos(db);
    }

    public Cursor obtenerEquipoPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoBD.obtenerEquipoPorId(db, id);
    }

    public Cursor obtenerEquiposPorProyecto(int idProyecto) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoBD.obtenerEquiposPorProyecto(db, idProyecto);
    }

    public Cursor obtenerEquiposPorLider(int boletaLider) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoBD.obtenerEquiposPorLider(db, boletaLider);
    }

    public Cursor obtenerEquiposPorEstado(String estado) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, p.Nombre as NombreProyecto " +
                "FROM " + EquipoBD.TABLE_NAME + " e " +
                "LEFT JOIN " + ProyectoBD.TABLE_NAME + " p ON e.IdProyecto = p.IdProyecto " +
                "WHERE e.EstadoEquipo = ? " +
                "ORDER BY e.Promedio DESC";
        return db.rawQuery(query, new String[]{estado});
    }

    public Cursor obtenerEquiposPorPromedio() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoBD.obtenerEquiposPorPromedio(db);
    }

    public Cursor obtenerEquiposConDetalles() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoBD.obtenerEquiposConDetalles(db);
    }

    public Cursor obtenerEstadisticasEquipos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EquipoBD.obtenerEstadisticasEquipos(db);
    }

    public int actualizarEquipo(int id, String nombre, String nombreProyecto, int numeroAlumnos, String descripcion,
                                String turno, String lugar, String cartel, int cantEval, double promedio,
                                int cantVisitas, Integer idProyecto, Integer boletaLider, String estadoEquipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EquipoBD.actualizarEquipo(db, id, nombre, nombreProyecto, numeroAlumnos, descripcion, turno, lugar, cartel, cantEval, promedio, cantVisitas, idProyecto, boletaLider, estadoEquipo);
        db.close();
        return rows;
    }


    public int incrementarVisitasEquipo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EquipoBD.incrementarVisitas(db, id);
        db.close();
        return rows;
    }

    public int actualizarEstadoEquipo(int id, String nuevoEstado) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EquipoBD.actualizarEstadoEquipo(db, id, nuevoEstado);
        db.close();
        return rows;
    }

    public int eliminarEquipo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EquipoBD.eliminarEquipo(db, id);
        db.close();
        return rows;
    }

    // ==================== MetodoS PARA ALUMNO ====================

    public long insertarAlumno(int boleta, String nombre, String apellido1, String apellido2, String correo,
                               String carrera, int semestre, String grupo, String turno, String contraseña,
                               Integer idEquipo, String estadoRegistro) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = AlumnoBD.insertarAlumno(db, boleta, nombre, apellido1, apellido2, correo, carrera, semestre, grupo, turno, contraseña, idEquipo, estadoRegistro);
        db.close();
        return id;
    }

    public Cursor obtenerTodosAlumnos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return AlumnoBD.obtenerTodosAlumnos(db);
    }

    public Cursor obtenerAlumnoPorBoleta(int boleta) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AlumnoBD.obtenerAlumnoPorBoleta(db, boleta);
    }

    public Cursor obtenerAlumnosPorEquipo(int idEquipo) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AlumnoBD.obtenerAlumnosPorEquipo(db, idEquipo);
    }

    public Cursor obtenerAlumnosPorEstado(String estado) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AlumnoBD.obtenerAlumnosPorEstado(db, estado);
    }

    public Cursor obtenerAlumnosDisponibles() {
        SQLiteDatabase db = this.getReadableDatabase();
        return AlumnoBD.obtenerAlumnosDisponibles(db);
    }

    public Cursor obtenerAlumnosConEquipo() {
        SQLiteDatabase db = this.getReadableDatabase();
        return AlumnoBD.obtenerAlumnosConEquipo(db);
    }

    public Cursor verificarCredencialesAlumno(int boleta, String contraseña) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AlumnoBD.verificarCredenciales(db, boleta, contraseña);
    }

    public boolean existeAlumnoPorBoleta(int boleta) {
        SQLiteDatabase db = this.getReadableDatabase();
        return AlumnoBD.existeAlumnoPorBoleta(db, boleta);
    }

    public int asignarAlumnoAEquipo(int boleta, int idEquipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = AlumnoBD.asignarAlumnoAEquipo(db, boleta, idEquipo);
        db.close();
        return rows;
    }

    public int removerAlumnoDeEquipo(int boleta) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = AlumnoBD.removerAlumnoDeEquipo(db, boleta);
        db.close();
        return rows;
    }

    public int actualizarAlumno(int boleta, String nombre, String apellido1, String apellido2, String correo,
                                String carrera, int semestre, String grupo, String turno, String contraseña,
                                Integer idEquipo, String estadoRegistro) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = AlumnoBD.actualizarAlumno(db, boleta, nombre, apellido1, apellido2, correo, carrera, semestre, grupo, turno, contraseña, idEquipo, estadoRegistro);
        db.close();
        return rows;
    }

    public int eliminarAlumno(int boleta) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = AlumnoBD.eliminarAlumno(db, boleta);
        db.close();
        return rows;
    }

    // ==================== MetodoS PARA VISITANTE ====================

    public long insertarVisitante(String nombre, String apellido1, String apellido2, int idEvento) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = VisitanteBD.insertarVisitante(db, nombre, apellido1, apellido2, idEvento);
        db.close();
        return id;
    }

    public Cursor obtenerTodosVisitantes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return VisitanteBD.obtenerTodosVisitantes(db);
    }

    public Cursor obtenerVisitantePorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return VisitanteBD.obtenerVisitantePorId(db, id);
    }

    public Cursor obtenerVisitantesPorEvento(int idEvento) {
        SQLiteDatabase db = this.getReadableDatabase();
        return VisitanteBD.obtenerVisitantesPorEvento(db, idEvento);
    }

    public Cursor obtenerVisitantesConEvento() {
        SQLiteDatabase db = this.getReadableDatabase();
        return VisitanteBD.obtenerVisitantesConEvento(db);
    }

    public int actualizarVisitante(int id, String nombre, String apellido1, String apellido2, int idEvento) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = VisitanteBD.actualizarVisitante(db, id, nombre, apellido1, apellido2, idEvento);
        db.close();
        return rows;
    }

    public int eliminarVisitante(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = VisitanteBD.eliminarVisitante(db, id);
        db.close();
        return rows;
    }

    // ==================== MÉTODOS ADICIONALES PARA DbmsSQLiteHelper.java ====================
// Agregar estos métodos a la clase DbmsSQLiteHelper

    // ==================== MÉTODOS ESPECÍFICOS PARA VISITANTE ====================

    /**
     * Obtiene equipos con proyectos ordenados por promedio de evaluación
     * @return Cursor con los equipos ordenados por promedio descendente
     */
    public Cursor obtenerEquiposConProyecto() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, p.Nombre as NombreProyecto, p.Descripcion as DescripcionProyecto " +
                "FROM " + EquipoBD.TABLE_NAME + " e " +
                "LEFT JOIN " + ProyectoBD.TABLE_NAME + " p ON e.IdProyecto = p.IdProyecto " +
                "WHERE e.EstadoEquipo = 'Registrado' " +
                "ORDER BY e.Promedio DESC, e.CantEval DESC";
        return db.rawQuery(query, null);
    }

    /**
     * Actualiza el promedio y cantidad de evaluaciones de un equipo
     * @param idEquipo ID del equipo
     * @param promedio Nuevo promedio
     * @param cantEvaluaciones Nueva cantidad de evaluaciones
     * @return Número de filas afectadas
     */
    public int actualizarPromedioEquipo(int idEquipo, double promedio, int cantEvaluaciones) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Promedio", promedio);
        values.put("CantEval", cantEvaluaciones);

        int rows = db.update(EquipoBD.TABLE_NAME, values, "IdEquipo = ?",
                new String[]{String.valueOf(idEquipo)});
        db.close();
        return rows;
    }

    /**
     * Obtiene estadísticas generales para visitantes
     * @return Cursor con estadísticas
     */
    public Cursor obtenerEstadisticasVisitante() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "(SELECT COUNT(*) FROM " + ProyectoBD.TABLE_NAME + ") as TotalProyectos, " +
                "(SELECT COUNT(*) FROM " + EquipoBD.TABLE_NAME + " WHERE EstadoEquipo = 'Registrado') as TotalEquipos, " +
                "(SELECT COUNT(*) FROM " + EvaluacionBD.TABLE_NAME + " WHERE IdVisitanteEvaluador IS NOT NULL) as TotalEvaluacionesVisitantes, " +
                "(SELECT ROUND(AVG(Calificacion), 2) FROM " + EvaluacionBD.TABLE_NAME + ") as PromedioGeneral";
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene los proyectos más populares (más evaluados)
     * @param limite Número máximo de proyectos a retornar
     * @return Cursor con los proyectos más populares
     */
    public Cursor obtenerProyectosMasPopulares(int limite) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, p.Nombre as NombreProyecto " +
                "FROM " + EquipoBD.TABLE_NAME + " e " +
                "LEFT JOIN " + ProyectoBD.TABLE_NAME + " p ON e.IdProyecto = p.IdProyecto " +
                "WHERE e.EstadoEquipo = 'Registrado' AND e.CantEval > 0 " +
                "ORDER BY e.CantEval DESC, e.Promedio DESC " +
                "LIMIT " + limite;
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene los proyectos mejor calificados
     * @param limite Número máximo de proyectos a retornar
     * @return Cursor con los proyectos mejor calificados
     */
    public Cursor obtenerProyectosMejorCalificados(int limite) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, p.Nombre as NombreProyecto " +
                "FROM " + EquipoBD.TABLE_NAME + " e " +
                "LEFT JOIN " + ProyectoBD.TABLE_NAME + " p ON e.IdProyecto = p.IdProyecto " +
                "WHERE e.EstadoEquipo = 'Registrado' AND e.CantEval >= 1 " +
                "ORDER BY e.Promedio DESC, e.CantEval DESC " +
                "LIMIT " + limite;
        return db.rawQuery(query, null);
    }

    /**
     * Cuenta las evaluaciones realizadas por un visitante específico
     * @param idVisitante ID del visitante
     * @return Número de evaluaciones realizadas
     */
    public int contarEvaluacionesVisitante(int idVisitante) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + EvaluacionBD.TABLE_NAME +
                " WHERE IdVisitanteEvaluador = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idVisitante)});

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    /**
     * Obtiene las evaluaciones realizadas por un visitante
     * @param idVisitante ID del visitante
     * @return Cursor con las evaluaciones del visitante
     */
    public Cursor obtenerEvaluacionesVisitante(int idVisitante) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ev.*, e.Nombre as NombreEquipo, e.NombreProyecto " +
                "FROM " + EvaluacionBD.TABLE_NAME + " ev " +
                "JOIN " + EquipoBD.TABLE_NAME + " e ON ev.IdEquipo = e.IdEquipo " +
                "WHERE ev.IdVisitanteEvaluador = ? " +
                "ORDER BY ev.FechaEvaluacion DESC";
        return db.rawQuery(query, new String[]{String.valueOf(idVisitante)});
    }

    /**
     * Verifica si existe un visitante por ID
     * @param idVisitante ID del visitante
     * @return true si existe, false en caso contrario
     */
    public boolean existeVisitante(int idVisitante) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + VisitanteBD.TABLE_NAME + " WHERE IdVisitante = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idVisitante)});

        boolean existe = false;
        if (cursor != null) {
            existe = cursor.getCount() > 0;
            cursor.close();
        }
        return existe;
    }

    /**
     * Obtiene feedback del evento (evaluaciones con IdEquipo = -1)
     * @return Cursor con todo el feedback del evento
     */
    public Cursor obtenerFeedbackEvento() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ev.*, v.Nombre as NombreVisitante " +
                "FROM " + EvaluacionBD.TABLE_NAME + " ev " +
                "LEFT JOIN " + VisitanteBD.TABLE_NAME + " v ON ev.IdVisitanteEvaluador = v.IdVisitante " +
                "WHERE ev.IdEquipo = -1 " +
                "ORDER BY ev.FechaEvaluacion DESC";
        return db.rawQuery(query, null);
    }

    /**
     * Obtiene estadísticas del feedback del evento
     * @return Cursor con estadísticas de feedback
     */
    public Cursor obtenerEstadisticasFeedback() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "COUNT(*) as TotalFeedbacks, " +
                "ROUND(AVG(Calificacion), 2) as PromedioSatisfaccion, " +
                "COUNT(CASE WHEN Calificacion >= 4 THEN 1 END) as FeedbacksPositivos, " +
                "COUNT(CASE WHEN Calificacion <= 2 THEN 1 END) as FeedbacksNegativos " +
                "FROM " + EvaluacionBD.TABLE_NAME + " " +
                "WHERE IdEquipo = -1";
        return db.rawQuery(query, null);
    }

    /**
     * Limpia visitantes anónimos antiguos (más de 1 día)
     * @return Número de visitantes eliminados
     */
    public int limpiarVisitantesAntiguos() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Primero eliminar evaluaciones de visitantes antiguos
        String deleteEvaluaciones = "DELETE FROM " + EvaluacionBD.TABLE_NAME + " " +
                "WHERE IdVisitanteEvaluador IN (" +
                "SELECT IdVisitante FROM " + VisitanteBD.TABLE_NAME + " " +
                "WHERE Nombre = 'Visitante' AND Apellido1 = 'Anónimo' " +
                "AND datetime('now', '-1 day') > datetime('now')" +
                ")";
        db.execSQL(deleteEvaluaciones);

        // Luego eliminar visitantes antiguos
        String deleteVisitantes = "DELETE FROM " + VisitanteBD.TABLE_NAME + " " +
                "WHERE Nombre = 'Visitante' AND Apellido1 = 'Anónimo' " +
                "AND datetime('now', '-1 day') > datetime('now')";
        db.execSQL(deleteVisitantes);

        // Retornar el número de filas afectadas
        Cursor cursor = db.rawQuery("SELECT changes()", null);
        int changes = 0;
        if (cursor.moveToFirst()) {
            changes = cursor.getInt(0);
        }
        cursor.close();
        return changes;
    }

    // ==================== MÉTODOS DE UTILIDAD ADICIONALES ====================

    /**
     * Obtiene información detallada de un equipo con su proyecto
     * @param idEquipo ID del equipo
     * @return Cursor con información detallada
     */
    public Cursor obtenerDetalleCompletoEquipo(int idEquipo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, p.*, pr.Nombre as NombreProfesor, pr.Apellido1 as ApellidoProfesor " +
                "FROM " + EquipoBD.TABLE_NAME + " e " +
                "LEFT JOIN " + ProyectoBD.TABLE_NAME + " p ON e.IdProyecto = p.IdProyecto " +
                "LEFT JOIN " + ProfesorBD.TABLE_NAME + " pr ON p.NumeroEmpleadoAsesor = pr.NumeroEmpleado " +
                "WHERE e.IdEquipo = ?";
        return db.rawQuery(query, new String[]{String.valueOf(idEquipo)});
    }

    /**
     * Busca equipos por nombre de proyecto o equipo
     * @param termino Término de búsqueda
     * @return Cursor con los equipos que coinciden
     */
    public Cursor buscarEquipos(String termino) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, p.Nombre as NombreProyecto " +
                "FROM " + EquipoBD.TABLE_NAME + " e " +
                "LEFT JOIN " + ProyectoBD.TABLE_NAME + " p ON e.IdProyecto = p.IdProyecto " +
                "WHERE e.EstadoEquipo = 'Registrado' AND (" +
                "e.Nombre LIKE ? OR " +
                "e.NombreProyecto LIKE ? OR " +
                "p.Nombre LIKE ? OR " +
                "e.Descripcion LIKE ?" +
                ") ORDER BY e.Promedio DESC";

        String searchTerm = "%" + termino + "%";
        return db.rawQuery(query, new String[]{searchTerm, searchTerm, searchTerm, searchTerm});
    }

    /**
     * Obtiene equipos filtrados por ubicación
     * @param lugar Lugar/ubicación a filtrar
     * @return Cursor con equipos en esa ubicación
     */
    public Cursor obtenerEquiposPorLugar(String lugar) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, p.Nombre as NombreProyecto " +
                "FROM " + EquipoBD.TABLE_NAME + " e " +
                "LEFT JOIN " + ProyectoBD.TABLE_NAME + " p ON e.IdProyecto = p.IdProyecto " +
                "WHERE e.EstadoEquipo = 'Registrado' AND e.Lugar = ? " +
                "ORDER BY e.Promedio DESC";
        return db.rawQuery(query, new String[]{lugar});
    }

    /**
     * Obtiene todas las ubicaciones únicas donde hay equipos
     * @return Cursor con las ubicaciones
     */
    public Cursor obtenerUbicacionesEquipos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT Lugar FROM " + EquipoBD.TABLE_NAME + " " +
                "WHERE EstadoEquipo = 'Registrado' AND Lugar IS NOT NULL " +
                "ORDER BY Lugar ASC";
        return db.rawQuery(query, null);
    }

    // ==================== MetodoS PARA EVALUACION ====================

    public long insertarEvaluacionProfesor(int calificacion, String comentarios, int idEquipo, int numeroEmpleadoEvaluador) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = EvaluacionBD.insertarEvaluacionProfesor(db, calificacion, comentarios, idEquipo, numeroEmpleadoEvaluador);
        db.close();
        return id;
    }

    public long insertarEvaluacionAlumno(int calificacion, String comentarios, int idEquipo, int boletaEvaluador) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = EvaluacionBD.insertarEvaluacionAlumno(db, calificacion, comentarios, idEquipo, boletaEvaluador);
        db.close();
        return id;
    }

    public long insertarEvaluacionVisitante(int calificacion, String comentarios, int idEquipo, int idVisitanteEvaluador) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = EvaluacionBD.insertarEvaluacionVisitante(db, calificacion, comentarios, idEquipo, idVisitanteEvaluador);
        db.close();
        return id;
    }

    public Cursor obtenerTodasEvaluaciones() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EvaluacionBD.obtenerTodasEvaluaciones(db);
    }

    public Cursor obtenerEvaluacionesPorEquipo(int idEquipo) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EvaluacionBD.obtenerEvaluacionesPorEquipo(db, idEquipo);
    }

    public Cursor obtenerEvaluacionesConDetalles() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EvaluacionBD.obtenerEvaluacionesConDetalles(db);
    }

    public Cursor obtenerPromediosPorEquipo() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EvaluacionBD.obtenerPromediosPorEquipo(db);
    }

    public Cursor obtenerEstadisticasEvaluaciones() {
        SQLiteDatabase db = this.getReadableDatabase();
        return EvaluacionBD.obtenerEstadisticasEvaluaciones(db);
    }

    public boolean yaEvaluoEquipo(int idEquipo, Integer numeroEmpleadoEvaluador, Integer boletaEvaluador, Integer idVisitanteEvaluador) {
        SQLiteDatabase db = this.getReadableDatabase();
        return EvaluacionBD.yaEvaluoEquipo(db, idEquipo, numeroEmpleadoEvaluador, boletaEvaluador, idVisitanteEvaluador);
    }

    public int actualizarEvaluacion(int id, int calificacion, String comentarios) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EvaluacionBD.actualizarEvaluacion(db, id, calificacion, comentarios);
        db.close();
        return rows;
    }

    public int eliminarEvaluacion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = EvaluacionBD.eliminarEvaluacion(db, id);
        db.close();
        return rows;
    }

    // ==================== MetodoS ADICIONALES ====================

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

    // Metodo para verificar la estructura de las tablas
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
    }

    // Metodo para recrear la base de datos (solo para testing)
    public void recrearBaseDatos() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Log.d(TAG, "Recreando base de datos...");

            // Eliminar todas las tablas en orden inverso por las foreign keys
            db.execSQL("DROP TABLE IF EXISTS " + EvaluacionBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + VisitanteBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AlumnoBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + EquipoBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ProyectoBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + EventoBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ProfesorBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AdministradorBD.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AcademiasBD.TABLE_NAME);

            // Recrear todas las tablas
            onCreate(db);
            Log.d(TAG, "Base de datos recreada exitosamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al recrear base de datos: " + e.getMessage());
        }
    }
}