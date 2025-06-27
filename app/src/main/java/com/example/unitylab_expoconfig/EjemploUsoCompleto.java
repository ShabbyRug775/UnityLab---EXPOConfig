package com.example.unitylab_expoconfig;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.ProcedimientosEquipo;

/**
 * Ejemplo completo de uso de la base de datos ExpoConfig
 * Demuestra el flujo completo desde la creación hasta las evaluaciones
 */
public class EjemploUsoCompleto {
    private static final String TAG = "EjemploExpoConfig";
    private DbmsSQLiteHelper dbHelper;

    public EjemploUsoCompleto(Context context) {
        dbHelper = new DbmsSQLiteHelper(context);
    }

    /**
     * Ejecuta un ejemplo completo del flujo de la aplicación
     */
    public void ejecutarEjemploCompleto() {
        Log.d(TAG, "=== INICIANDO EJEMPLO COMPLETO ===");

        try {
            // 1. Verificar que los datos por defecto existen
            verificarDatosPorDefecto();

            // 2. Registrar estudiantes adicionales
            registrarEstudiantesEjemplo();

            // 3. Demostrar flujo de creación de equipos
            demostrarFlujoDEequipos();

            // 4. Registrar equipos a proyectos
            registrarEquiposAProyectos();

            // 5. Agregar visitantes y evaluaciones
            agregarEvaluaciones();

            // 6. Mostrar reportes y estadísticas
            mostrarReportes();

            Log.d(TAG, "=== EJEMPLO COMPLETO FINALIZADO ===");

        } catch (Exception e) {
            Log.e(TAG, "Error en ejemplo completo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verificarDatosPorDefecto() {
        Log.d(TAG, "--- Verificando datos por defecto ---");

        // Verificar academias
        Cursor academias = dbHelper.obtenerTodasAcademias();
        Log.d(TAG, "Academias encontradas: " + academias.getCount());
        academias.close();

        // Verificar administradores
        Cursor administradores = dbHelper.obtenerTodosAdministradores();
        Log.d(TAG, "Administradores encontrados: " + administradores.getCount());
        administradores.close();

        // Verificar profesores
        Cursor profesores = dbHelper.obtenerTodosProfesores();
        Log.d(TAG, "Profesores encontrados: " + profesores.getCount());
        profesores.close();

        // Verificar eventos
        Cursor eventos = dbHelper.obtenerTodosEventos();
        Log.d(TAG, "Eventos encontrados: " + eventos.getCount());
        eventos.close();

        // Verificar proyectos
        Cursor proyectos = dbHelper.obtenerTodosProyectos();
        Log.d(TAG, "Proyectos encontrados: " + proyectos.getCount());
        proyectos.close();
    }

    private void registrarEstudiantesEjemplo() {
        Log.d(TAG, "--- Registrando estudiantes de ejemplo ---");

        // Estudiantes que crearán equipos
        long alumno1 = dbHelper.insertarAlumno(2025630001, "Pedro", "Ramírez", "González",
                "pedro.ramirez@alumno.ipn.mx", "ISC", 6, "6CM1",
                "Matutino", "pass123", null, "SinEquipo");

        long alumno2 = dbHelper.insertarAlumno(2025630002, "Sofia", "Vargas", "Mendoza",
                "sofia.vargas@alumno.ipn.mx", "ISC", 6, "6CM1",
                "Matutino", "pass456", null, "SinEquipo");

        long alumno3 = dbHelper.insertarAlumno(2025630003, "Diego", "Torres", "Ruiz",
                "diego.torres@alumno.ipn.mx", "ISC", 6, "6CM2",
                "Matutino", "pass789", null, "SinEquipo");

        long alumno4 = dbHelper.insertarAlumno(2025630004, "Valentina", "Cruz", "Moreno",
                "valentina.cruz@alumno.ipn.mx", "ISC", 6, "6CM1",
                "Matutino", "pass000", null, "SinEquipo");

        long alumno5 = dbHelper.insertarAlumno(2025630005, "Andrés", "Flores", "Jiménez",
                "andres.flores@alumno.ipn.mx", "IME", 7, "7MM1",
                "Vespertino", "pass111", null, "SinEquipo");

        Log.d(TAG, "Estudiantes registrados: " + alumno1 + ", " + alumno2 + ", " + alumno3 + ", " + alumno4 + ", " + alumno5);
    }

    private void demostrarFlujoDEequipos() {
        Log.d(TAG, "--- Demostrando flujo de equipos ---");

        // EQUIPO 1: Pedro crea un equipo
        ProcedimientosEquipo.ResultadoProcedimiento resultado1 =
                ProcedimientosEquipo.crearEquipo(dbHelper, 2025630001, "Innovadores TI",
                        "Equipo especializado en desarrollo web full-stack",
                        "Matutino", "Laboratorio A-101");

        if (resultado1.exito) {
            Log.d(TAG, "Equipo 1 creado: " + resultado1.mensaje + " (ID: " + resultado1.idGenerado + ")");

            // Pedro agrega a Sofia
            ProcedimientosEquipo.ResultadoProcedimiento agregar1 =
                    ProcedimientosEquipo.agregarIntegrante(dbHelper, 2025630001, 2025630002);
            Log.d(TAG, "Agregar Sofia: " + agregar1.mensaje);

            // Pedro registra un nuevo alumno directamente
            ProcedimientosEquipo.ResultadoProcedimiento registrarNuevo =
                    ProcedimientosEquipo.registrarAlumnoEnEquipo(dbHelper, 2025630020, "Carlos", "Méndez", "Vázquez",
                            "carlos.mendez@alumno.ipn.mx", "ISC", 6, "6CM1",
                            "Matutino", "passnuevo", 2025630001);
            Log.d(TAG, "Registrar nuevo alumno: " + registrarNuevo.mensaje);

        } else {
            Log.e(TAG, "Error creando equipo 1: " + resultado1.mensaje);
        }

        // EQUIPO 2: Diego crea otro equipo
        ProcedimientosEquipo.ResultadoProcedimiento resultado2 =
                ProcedimientosEquipo.crearEquipo(dbHelper, 2025630003, "EcoTech Solutions",
                        "Grupo enfocado en robótica y sustentabilidad ambiental",
                        "Matutino", "Taller B-205");

        if (resultado2.exito) {
            Log.d(TAG, "Equipo 2 creado: " + resultado2.mensaje + " (ID: " + resultado2.idGenerado + ")");

            // Diego agrega a Valentina
            ProcedimientosEquipo.ResultadoProcedimiento agregar2 =
                    ProcedimientosEquipo.agregarIntegrante(dbHelper, 2025630003, 2025630004);
            Log.d(TAG, "Agregar Valentina: " + agregar2.mensaje);

        } else {
            Log.e(TAG, "Error creando equipo 2: " + resultado2.mensaje);
        }
    }

    private void registrarEquiposAProyectos() {
        Log.d(TAG, "--- Registrando equipos a proyectos ---");

        // Obtener los primeros dos proyectos disponibles
        Cursor proyectos = dbHelper.obtenerTodosProyectos();
        if (proyectos.moveToFirst()) {
            int proyecto1 = proyectos.getInt(0); // Primer proyecto

            // Registrar Equipo 1 al Proyecto 1
            ProcedimientosEquipo.ResultadoProcedimiento registro1 =
                    ProcedimientosEquipo.registrarEquipoAProyecto(dbHelper, 1, proyecto1);
            Log.d(TAG, "Registrar Equipo 1 a Proyecto 1: " + registro1.mensaje);

            if (proyectos.moveToNext()) {
                int proyecto2 = proyectos.getInt(0); // Segundo proyecto

                // Registrar Equipo 2 al Proyecto 2
                ProcedimientosEquipo.ResultadoProcedimiento registro2 =
                        ProcedimientosEquipo.registrarEquipoAProyecto(dbHelper, 2, proyecto2);
                Log.d(TAG, "Registrar Equipo 2 a Proyecto 2: " + registro2.mensaje);
            }
        }
        proyectos.close();
    }

    private void agregarEvaluaciones() {
        Log.d(TAG, "--- Agregando evaluaciones ---");

        // Registrar algunos visitantes
        long visitante1 = dbHelper.insertarVisitante("Juan", "Pérez", "Martínez", 1);
        long visitante2 = dbHelper.insertarVisitante("Laura", "García", "López", 1);

        // Evaluaciones por profesor
        long eval1 = dbHelper.insertarEvaluacionProfesor(9, "Excelente implementación del sistema", 1, 20001);
        long eval2 = dbHelper.insertarEvaluacionProfesor(8, "Buen trabajo en robótica, mejorar documentación", 2, 20002);

        // Evaluaciones por alumno (Andrés evalúa ambos equipos)
        long eval3 = dbHelper.insertarEvaluacionAlumno(8, "Me gustó mucho la interfaz del sistema", 1, 2025630005);
        long eval4 = dbHelper.insertarEvaluacionAlumno(9, "Tecnología muy innovadora", 2, 2025630005);

        // Evaluaciones por visitantes
        long eval5 = dbHelper.insertarEvaluacionVisitante(9, "Proyecto muy útil para estudiantes", 1, (int)visitante1);
        long eval6 = dbHelper.insertarEvaluacionVisitante(8, "Excelente contribución al medio ambiente", 2, (int)visitante2);

        Log.d(TAG, "Evaluaciones agregadas: " + eval1 + ", " + eval2 + ", " + eval3 + ", " + eval4 + ", " + eval5 + ", " + eval6);

        // Incrementar visitas a los equipos
        dbHelper.incrementarVisitasEquipo(1);
        dbHelper.incrementarVisitasEquipo(1);
        dbHelper.incrementarVisitasEquipo(2);
    }

    private void mostrarReportes() {
        Log.d(TAG, "--- Mostrando reportes y estadísticas ---");

        // Mostrar equipos con detalles
        Log.d(TAG, "=== EQUIPOS CON DETALLES ===");
        Cursor equiposDetalle = dbHelper.obtenerEquiposConDetalles();
        if (equiposDetalle.moveToFirst()) {
            do {
                String nombre = equiposDetalle.getString(equiposDetalle.getColumnIndexOrThrow("Nombre"));
                String proyecto = equiposDetalle.getString(equiposDetalle.getColumnIndexOrThrow("proyecto_nombre"));
                String lider = equiposDetalle.getString(equiposDetalle.getColumnIndexOrThrow("lider_nombre"));
                int visitas = equiposDetalle.getInt(equiposDetalle.getColumnIndexOrThrow("CantVisitas"));

                Log.d(TAG, "Equipo: " + nombre + " | Proyecto: " + proyecto + " | Líder: " + lider + " | Visitas: " + visitas);
            } while (equiposDetalle.moveToNext());
        }
        equiposDetalle.close();

        // Mostrar promedios por equipo
        Log.d(TAG, "=== PROMEDIOS POR EQUIPO ===");
        Cursor promedios = dbHelper.obtenerPromediosPorEquipo();
        if (promedios.moveToFirst()) {
            do {
                String nombreEquipo = promedios.getString(promedios.getColumnIndexOrThrow("nombre_equipo"));
                int totalEvaluaciones = promedios.getInt(promedios.getColumnIndexOrThrow("total_evaluaciones"));
                double promedio = promedios.getDouble(promedios.getColumnIndexOrThrow("promedio_calificacion"));

                Log.d(TAG, "Equipo: " + nombreEquipo + " | Evaluaciones: " + totalEvaluaciones + " | Promedio: " + String.format("%.2f", promedio));
            } while (promedios.moveToNext());
        }
        promedios.close();

        // Mostrar estadísticas generales
        Log.d(TAG, "=== ESTADÍSTICAS GENERALES ===");
        Cursor estadisticasEquipos = dbHelper.obtenerEstadisticasEquipos();
        if (estadisticasEquipos.moveToFirst()) {
            int totalEquipos = estadisticasEquipos.getInt(estadisticasEquipos.getColumnIndexOrThrow("total_equipos"));
            int equiposFormacion = estadisticasEquipos.getInt(estadisticasEquipos.getColumnIndexOrThrow("equipos_formacion"));
            int equiposRegistrados = estadisticasEquipos.getInt(estadisticasEquipos.getColumnIndexOrThrow("equipos_registrados"));
            int totalAlumnos = estadisticasEquipos.getInt(estadisticasEquipos.getColumnIndexOrThrow("total_alumnos"));

            Log.d(TAG, "Total equipos: " + totalEquipos);
            Log.d(TAG, "En formación: " + equiposFormacion);
            Log.d(TAG, "Registrados: " + equiposRegistrados);
            Log.d(TAG, "Total alumnos: " + totalAlumnos);
        }
        estadisticasEquipos.close();

        // Mostrar alumnos disponibles
        Log.d(TAG, "=== ALUMNOS DISPONIBLES (SIN EQUIPO) ===");
        Cursor alumnosDisponibles = dbHelper.obtenerAlumnosDisponibles();
        Log.d(TAG, "Alumnos sin equipo: " + alumnosDisponibles.getCount());
        if (alumnosDisponibles.moveToFirst()) {
            do {
                int boleta = alumnosDisponibles.getInt(alumnosDisponibles.getColumnIndexOrThrow("Boleta"));
                String nombre = alumnosDisponibles.getString(alumnosDisponibles.getColumnIndexOrThrow("Nombre"));
                String carrera = alumnosDisponibles.getString(alumnosDisponibles.getColumnIndexOrThrow("Carrera"));

                Log.d(TAG, "Disponible: " + nombre + " (" + boleta + ") - " + carrera);
            } while (alumnosDisponibles.moveToNext());
        }
        alumnosDisponibles.close();
    }

    /**
     * Metodo para limpiar datos de prueba (útil para testing)
     */
    public void limpiarDatosPrueba() {
        Log.d(TAG, "--- Limpiando datos de prueba ---");
        dbHelper.recrearBaseDatos();
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void cerrar() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}