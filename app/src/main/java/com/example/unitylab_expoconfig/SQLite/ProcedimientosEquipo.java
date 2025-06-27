package com.example.unitylab_expoconfig.SQLite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Clase que implementa los procedimientos almacenados del script SQL original
 * como métodos Java, ya que SQLite no soporta procedimientos almacenados nativamente.
 */
public class ProcedimientosEquipo {
    private static final String TAG = "ProcedimientosEquipo";

    /**
     * Resultado de una operación de procedimiento
     */
    public static class ResultadoProcedimiento {
        public boolean exito;
        public String mensaje;
        public int idGenerado;

        public ResultadoProcedimiento(boolean exito, String mensaje, int idGenerado) {
            this.exito = exito;
            this.mensaje = mensaje;
            this.idGenerado = idGenerado;
        }

        public ResultadoProcedimiento(boolean exito, String mensaje) {
            this(exito, mensaje, 0);
        }
    }

    /**
     * Procedimiento para que un alumno cree un equipo
     * Equivalente al procedimiento CrearEquipo del SQL original
     *
     * @param dbHelper Helper de base de datos
     * @param boletaLider Boleta del alumno que será líder
     * @param nombreEquipo Nombre del equipo a crear
     * @param descripcion Descripción del equipo
     * @param turno Turno del equipo
     * @param lugar Lugar asignado al equipo
     * @return ResultadoProcedimiento con el resultado de la operación
     */
    public static ResultadoProcedimiento crearEquipo(DbmsSQLiteHelper dbHelper, int boletaLider,
                                                     String nombreEquipo, String descripcion,
                                                     String turno, String lugar) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            // Verificar que el alumno existe y no tiene equipo
            Cursor cursorAlumno = AlumnoBD.obtenerAlumnoPorBoleta(db, boletaLider);
            if (!cursorAlumno.moveToFirst()) {
                cursorAlumno.close();
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El alumno no existe");
            }

            String estadoRegistro = cursorAlumno.getString(cursorAlumno.getColumnIndexOrThrow(AlumnoBD.COL_ESTADO_REGISTRO));
            cursorAlumno.close();

            if (!"SinEquipo".equals(estadoRegistro)) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El alumno ya tiene equipo");
            }

            // Crear el equipo
            long equipoId = EquipoBD.insertarEquipo(db, nombreEquipo, "Por definir", 1, descripcion,
                    turno, lugar, null, 0, 0.00, 0, null, boletaLider, "EnFormacion");

            if (equipoId == -1) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: No se pudo crear el equipo");
            }

            // Asignar al líder al equipo
            int filasActualizadas = AlumnoBD.asignarAlumnoAEquipo(db, boletaLider, (int) equipoId);

            if (filasActualizadas == 0) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: No se pudo asignar el líder al equipo");
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            Log.d(TAG, "Equipo creado exitosamente con ID: " + equipoId);
            return new ResultadoProcedimiento(true, "Equipo creado exitosamente", (int) equipoId);

        } catch (Exception e) {
            db.endTransaction();
            Log.e(TAG, "Error al crear equipo: " + e.getMessage());
            return new ResultadoProcedimiento(false, "Error al crear equipo: " + e.getMessage());
        }
    }

    /**
     * Procedimiento para agregar integrante a equipo
     * Equivalente al procedimiento AgregarIntegrante del SQL original
     *
     * @param dbHelper Helper de base de datos
     * @param boletaLider Boleta del líder del equipo
     * @param boletaNuevoIntegrante Boleta del nuevo integrante
     * @return ResultadoProcedimiento con el resultado de la operación
     */
    public static ResultadoProcedimiento agregarIntegrante(DbmsSQLiteHelper dbHelper, int boletaLider,
                                                           int boletaNuevoIntegrante) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            // Obtener el equipo del líder
            Cursor cursorEquipo = EquipoBD.obtenerEquiposPorLider(db, boletaLider);
            if (!cursorEquipo.moveToFirst()) {
                cursorEquipo.close();
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El líder no tiene equipo válido");
            }

            int equipoId = cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_ID_EQUIPO));
            cursorEquipo.close();

            // Verificar que el nuevo integrante existe y no tiene equipo
            Cursor cursorNuevoIntegrante = AlumnoBD.obtenerAlumnoPorBoleta(db, boletaNuevoIntegrante);
            if (!cursorNuevoIntegrante.moveToFirst()) {
                cursorNuevoIntegrante.close();
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El nuevo integrante no existe");
            }

            String estadoRegistro = cursorNuevoIntegrante.getString(cursorNuevoIntegrante.getColumnIndexOrThrow(AlumnoBD.COL_ESTADO_REGISTRO));
            cursorNuevoIntegrante.close();

            if (!"SinEquipo".equals(estadoRegistro)) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El integrante ya tiene equipo");
            }

            // Agregar al equipo
            int filasActualizadas = AlumnoBD.asignarAlumnoAEquipo(db, boletaNuevoIntegrante, equipoId);

            if (filasActualizadas == 0) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: No se pudo agregar el integrante al equipo");
            }

            // Actualizar contador de alumnos en el equipo
            Cursor cursorConteo = AlumnoBD.obtenerAlumnosPorEquipo(db, equipoId);
            int nuevoConteo = cursorConteo.getCount();
            cursorConteo.close();

            EquipoBD.actualizarContadorAlumnos(db, equipoId, nuevoConteo);

            db.setTransactionSuccessful();
            db.endTransaction();

            Log.d(TAG, "Integrante agregado exitosamente al equipo " + equipoId);
            return new ResultadoProcedimiento(true, "Integrante agregado exitosamente");

        } catch (Exception e) {
            db.endTransaction();
            Log.e(TAG, "Error al agregar integrante: " + e.getMessage());
            return new ResultadoProcedimiento(false, "Error al agregar integrante: " + e.getMessage());
        }
    }

    /**
     * Procedimiento para registrar nuevo alumno y agregarlo directamente a un equipo
     * Equivalente al procedimiento RegistrarAlumnoEnEquipo del SQL original
     *
     * @param dbHelper Helper de base de datos
     * @param boleta Boleta del nuevo alumno
     * @param nombre Nombre del alumno
     * @param apellido1 Primer apellido
     * @param apellido2 Segundo apellido
     * @param correo Correo del alumno
     * @param carrera Carrera del alumno
     * @param semestre Semestre del alumno
     * @param grupo Grupo del alumno
     * @param turno Turno del alumno
     * @param contraseña Contraseña del alumno
     * @param boletaLider Boleta del líder del equipo al que se unirá
     * @return ResultadoProcedimiento con el resultado de la operación
     */
    public static ResultadoProcedimiento registrarAlumnoEnEquipo(DbmsSQLiteHelper dbHelper, int boleta,
                                                                 String nombre, String apellido1, String apellido2,
                                                                 String correo, String carrera, int semestre,
                                                                 String grupo, String turno, String contraseña,
                                                                 int boletaLider) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            // Obtener el equipo del líder
            Cursor cursorEquipo = EquipoBD.obtenerEquiposPorLider(db, boletaLider);
            if (!cursorEquipo.moveToFirst()) {
                cursorEquipo.close();
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El líder no tiene equipo válido");
            }

            int equipoId = cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_ID_EQUIPO));
            cursorEquipo.close();

            // Verificar que la nueva boleta no existe
            if (AlumnoBD.existeAlumnoPorBoleta(db, boleta)) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: La boleta ya existe");
            }

            // Registrar al nuevo alumno directamente en el equipo
            long alumnoId = AlumnoBD.insertarAlumno(db, boleta, nombre, apellido1, apellido2, correo,
                    carrera, semestre, grupo, turno, contraseña,
                    equipoId, "ConEquipo");

            if (alumnoId == -1) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: No se pudo registrar el alumno");
            }

            // Actualizar contador de alumnos en el equipo
            Cursor cursorConteo = AlumnoBD.obtenerAlumnosPorEquipo(db, equipoId);
            int nuevoConteo = cursorConteo.getCount();
            cursorConteo.close();

            EquipoBD.actualizarContadorAlumnos(db, equipoId, nuevoConteo);

            db.setTransactionSuccessful();
            db.endTransaction();

            Log.d(TAG, "Alumno registrado y agregado al equipo exitosamente");
            return new ResultadoProcedimiento(true, "Alumno registrado y agregado al equipo exitosamente");

        } catch (Exception e) {
            db.endTransaction();
            Log.e(TAG, "Error al registrar alumno en equipo: " + e.getMessage());
            return new ResultadoProcedimiento(false, "Error al registrar alumno en equipo: " + e.getMessage());
        }
    }

    /**
     * Procedimiento para registrar equipo a un proyecto
     * Equivalente al procedimiento RegistrarEquipoAProyecto del SQL original
     *
     * @param dbHelper Helper de base de datos
     * @param idEquipo ID del equipo a registrar
     * @param idProyecto ID del proyecto al que se registrará
     * @return ResultadoProcedimiento con el resultado de la operación
     */
    public static ResultadoProcedimiento registrarEquipoAProyecto(DbmsSQLiteHelper dbHelper, int idEquipo,
                                                                  int idProyecto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            // Verificar que el equipo existe y no está registrado a otro proyecto
            Cursor cursorEquipo = EquipoBD.obtenerEquipoPorId(db, idEquipo);
            if (!cursorEquipo.moveToFirst()) {
                cursorEquipo.close();
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El equipo no existe");
            }

            int proyectoActual = cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_ID_PROYECTO));
            cursorEquipo.close();

            // Verificar que no esté ya registrado a otro proyecto (permitir NULL o 0)
            if (proyectoActual != 0) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El equipo ya está registrado a otro proyecto");
            }

            // Verificar que el proyecto existe
            Cursor cursorProyecto = ProyectoBD.obtenerProyectoPorId(db, idProyecto);
            if (!cursorProyecto.moveToFirst()) {
                cursorProyecto.close();
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El proyecto no existe");
            }

            String nombreProyecto = cursorProyecto.getString(cursorProyecto.getColumnIndexOrThrow(ProyectoBD.COL_NOMBRE));
            cursorProyecto.close();

            // Registrar equipo al proyecto
            Cursor equipoActual = EquipoBD.obtenerEquipoPorId(db, idEquipo);
            if (equipoActual.moveToFirst()) {
                String nombre = equipoActual.getString(equipoActual.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE));
                int numeroAlumnos = equipoActual.getInt(equipoActual.getColumnIndexOrThrow(EquipoBD.COL_NUMERO_ALUMNOS));
                String descripcion = equipoActual.getString(equipoActual.getColumnIndexOrThrow(EquipoBD.COL_DESCRIPCION));
                String turno = equipoActual.getString(equipoActual.getColumnIndexOrThrow(EquipoBD.COL_TURNO));
                String lugar = equipoActual.getString(equipoActual.getColumnIndexOrThrow(EquipoBD.COL_LUGAR));
                String cartel = equipoActual.getString(equipoActual.getColumnIndexOrThrow(EquipoBD.COL_CARTEL));
                int cantEval = equipoActual.getInt(equipoActual.getColumnIndexOrThrow(EquipoBD.COL_CANT_EVAL));
                double promedio = equipoActual.getDouble(equipoActual.getColumnIndexOrThrow(EquipoBD.COL_PROMEDIO));
                int cantVisitas = equipoActual.getInt(equipoActual.getColumnIndexOrThrow(EquipoBD.COL_CANT_VISITAS));
                int boletaLider = equipoActual.getInt(equipoActual.getColumnIndexOrThrow(EquipoBD.COL_BOLETA_LIDER));

                int filasActualizadas = EquipoBD.actualizarEquipo(db, idEquipo, nombre, nombreProyecto, numeroAlumnos,
                        descripcion, turno, lugar, cartel, cantEval, promedio,
                        cantVisitas, idProyecto, boletaLider, "Registrado");

                if (filasActualizadas == 0) {
                    equipoActual.close();
                    db.endTransaction();
                    return new ResultadoProcedimiento(false, "Error: No se pudo registrar el equipo al proyecto");
                }

                equipoActual.close();
            } else {
                equipoActual.close();
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: No se pudo obtener la información del equipo");
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            Log.d(TAG, "Equipo registrado al proyecto exitosamente");
            return new ResultadoProcedimiento(true, "Equipo registrado al proyecto exitosamente");

        } catch (Exception e) {
            db.endTransaction();
            Log.e(TAG, "Error al registrar equipo a proyecto: " + e.getMessage());
            return new ResultadoProcedimiento(false, "Error al registrar equipo a proyecto: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para obtener información de un equipo por su líder
     *
     * @param dbHelper Helper de base de datos
     * @param boletaLider Boleta del líder
     * @return Cursor con la información del equipo o null si no existe
     */
    public static Cursor obtenerEquipoPorLider(DbmsSQLiteHelper dbHelper, int boletaLider) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return EquipoBD.obtenerEquiposPorLider(db, boletaLider);
    }

    /**
     * Método auxiliar para obtener todos los integrantes de un equipo
     *
     * @param dbHelper Helper de base de datos
     * @param idEquipo ID del equipo
     * @return Cursor con todos los integrantes del equipo
     */
    public static Cursor obtenerIntegrantesEquipo(DbmsSQLiteHelper dbHelper, int idEquipo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return AlumnoBD.obtenerAlumnosPorEquipo(db, idEquipo);
    }

    /**
     * Método para remover un integrante de un equipo
     *
     * @param dbHelper Helper de base de datos
     * @param boletaLider Boleta del líder del equipo
     * @param boletaIntegrante Boleta del integrante a remover
     * @return ResultadoProcedimiento con el resultado de la operación
     */
    public static ResultadoProcedimiento removerIntegrante(DbmsSQLiteHelper dbHelper, int boletaLider,
                                                           int boletaIntegrante) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            // Verificar que el líder tiene un equipo
            Cursor cursorEquipo = EquipoBD.obtenerEquiposPorLider(db, boletaLider);
            if (!cursorEquipo.moveToFirst()) {
                cursorEquipo.close();
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El líder no tiene equipo válido");
            }

            int equipoId = cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_ID_EQUIPO));
            cursorEquipo.close();

            // Verificar que el integrante está en el equipo y no es el líder
            if (boletaIntegrante == boletaLider) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: No se puede remover al líder del equipo");
            }

            Cursor cursorIntegrante = AlumnoBD.obtenerAlumnoPorBoleta(db, boletaIntegrante);
            if (!cursorIntegrante.moveToFirst()) {
                cursorIntegrante.close();
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El integrante no existe");
            }

            int equipoDelIntegrante = cursorIntegrante.getInt(cursorIntegrante.getColumnIndexOrThrow(AlumnoBD.COL_ID_EQUIPO));
            cursorIntegrante.close();

            if (equipoDelIntegrante != equipoId) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: El integrante no pertenece al equipo");
            }

            // Remover al integrante del equipo
            int filasActualizadas = AlumnoBD.removerAlumnoDeEquipo(db, boletaIntegrante);

            if (filasActualizadas == 0) {
                db.endTransaction();
                return new ResultadoProcedimiento(false, "Error: No se pudo remover el integrante del equipo");
            }

            // Actualizar contador de alumnos en el equipo
            Cursor cursorConteo = AlumnoBD.obtenerAlumnosPorEquipo(db, equipoId);
            int nuevoConteo = cursorConteo.getCount();
            cursorConteo.close();

            EquipoBD.actualizarContadorAlumnos(db, equipoId, nuevoConteo);

            db.setTransactionSuccessful();
            db.endTransaction();

            Log.d(TAG, "Integrante removido exitosamente del equipo " + equipoId);
            return new ResultadoProcedimiento(true, "Integrante removido exitosamente");

        } catch (Exception e) {
            db.endTransaction();
            Log.e(TAG, "Error al remover integrante: " + e.getMessage());
            return new ResultadoProcedimiento(false, "Error al remover integrante: " + e.getMessage());
        }
    }
}