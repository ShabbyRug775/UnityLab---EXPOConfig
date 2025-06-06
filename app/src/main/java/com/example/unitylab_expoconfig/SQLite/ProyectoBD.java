package com.example.unitylab_expoconfig.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProyectoBD {
    // Constantes para la tabla PROYECTO
    public static final String TABLE_NAME = "PROYECTO";
    public static final String COL_ID = "id_proyecto";
    public static final String COL_NOMBRE_PROYECTO = "nombre_proyecto";
    public static final String COL_DESCRIPCION = "descripcion";
    public static final String COL_NOMBRE_EQUIPO = "nombre_equipo";
    public static final String COL_MATERIA = "materia";
    public static final String COL_GRUPO = "grupo";
    public static final String COL_SEMESTRE = "semestre";
    public static final String COL_CARRERA = "carrera";
    public static final String COL_HERRAMIENTAS = "herramientas_utilizadas";
    public static final String COL_ARQUITECTURA = "arquitectura";
    public static final String COL_FUNCIONES = "funciones_principales";
    public static final String COL_ID_PROFESOR = "id_profesor";
    public static final String COL_ID_ESTUDIANTE_LIDER = "id_estudiante_lider";
    public static final String COL_FECHA_CREACION = "fecha_creacion";
    public static final String COL_ESTADO = "estado";
    public static final String COL_URL_CARTEL = "url_cartel";

    // Sentencia SQL para crear la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE_PROYECTO + " TEXT NOT NULL, " +
                    COL_DESCRIPCION + " TEXT, " +
                    COL_NOMBRE_EQUIPO + " TEXT NOT NULL, " +
                    COL_MATERIA + " TEXT NOT NULL, " +
                    COL_GRUPO + " TEXT NOT NULL, " +
                    COL_SEMESTRE + " TEXT NOT NULL, " +
                    COL_CARRERA + " TEXT NOT NULL, " +
                    COL_HERRAMIENTAS + " TEXT, " +
                    COL_ARQUITECTURA + " TEXT, " +
                    COL_FUNCIONES + " TEXT, " +
                    COL_ID_PROFESOR + " INTEGER, " +
                    COL_ID_ESTUDIANTE_LIDER + " INTEGER, " +
                    COL_FECHA_CREACION + " TEXT, " +
                    COL_ESTADO + " TEXT DEFAULT 'ACTIVO', " +
                    COL_URL_CARTEL + " TEXT, " +
                    "FOREIGN KEY(" + COL_ID_PROFESOR + ") REFERENCES " + ProfesorBD.TABLE_NAME + "(" + ProfesorBD.COL_ID + "), " +
                    "FOREIGN KEY(" + COL_ID_ESTUDIANTE_LIDER + ") REFERENCES " + EstudianteBD.TABLE_NAME + "(" + EstudianteBD.COL_ID + "))";

    // Metodos CRUD
    public static long insertarProyecto(SQLiteDatabase db, String nombreProyecto, String descripcion,
                                        String nombreEquipo, String materia, String grupo, String semestre,
                                        String carrera, String herramientas, String arquitectura,
                                        String funciones, int idProfesor, int idEstudianteLider,
                                        String fechaCreacion, String estado, String urlCartel) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE_PROYECTO, nombreProyecto);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_NOMBRE_EQUIPO, nombreEquipo);
        values.put(COL_MATERIA, materia);
        values.put(COL_GRUPO, grupo);
        values.put(COL_SEMESTRE, semestre);
        values.put(COL_CARRERA, carrera);
        values.put(COL_HERRAMIENTAS, herramientas);
        values.put(COL_ARQUITECTURA, arquitectura);
        values.put(COL_FUNCIONES, funciones);
        values.put(COL_ID_PROFESOR, idProfesor);
        values.put(COL_ID_ESTUDIANTE_LIDER, idEstudianteLider);
        values.put(COL_FECHA_CREACION, fechaCreacion);
        values.put(COL_ESTADO, estado);
        values.put(COL_URL_CARTEL, urlCartel);

        return db.insert(TABLE_NAME, null, values);
    }

    public static Cursor obtenerTodosProyectos(SQLiteDatabase db) {
        return db.query(TABLE_NAME, null, null, null, null, null,
                COL_FECHA_CREACION + " DESC");
    }

    public static Cursor obtenerProyectoPorId(SQLiteDatabase db, int id) {
        return db.query(TABLE_NAME,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public static Cursor obtenerProyectosPorEstudiante(SQLiteDatabase db, int idEstudiante) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_ESTUDIANTE_LIDER + " = ?",
                new String[]{String.valueOf(idEstudiante)},
                null, null, COL_FECHA_CREACION + " DESC");
    }

    public static Cursor obtenerProyectosPorProfesor(SQLiteDatabase db, int idProfesor) {
        return db.query(TABLE_NAME,
                null,
                COL_ID_PROFESOR + " = ?",
                new String[]{String.valueOf(idProfesor)},
                null, null, COL_FECHA_CREACION + " DESC");
    }

    public static Cursor buscarProyectosPorNombre(SQLiteDatabase db, String nombre) {
        return db.query(TABLE_NAME,
                null,
                COL_NOMBRE_PROYECTO + " LIKE ?",
                new String[]{"%" + nombre + "%"},
                null, null, COL_FECHA_CREACION + " DESC");
    }

    public static Cursor buscarProyectosPorMateria(SQLiteDatabase db, String materia) {
        return db.query(TABLE_NAME,
                null,
                COL_MATERIA + " LIKE ?",
                new String[]{"%" + materia + "%"},
                null, null, COL_FECHA_CREACION + " DESC");
    }

    public static Cursor obtenerProyectosPorCarrera(SQLiteDatabase db, String carrera) {
        return db.query(TABLE_NAME,
                null,
                COL_CARRERA + " = ?",
                new String[]{carrera},
                null, null, COL_FECHA_CREACION + " DESC");
    }

    public static int actualizarProyecto(SQLiteDatabase db, int id, String nombreProyecto, String descripcion,
                                         String nombreEquipo, String materia, String grupo, String semestre,
                                         String carrera, String herramientas, String arquitectura,
                                         String funciones, int idProfesor, String estado, String urlCartel) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE_PROYECTO, nombreProyecto);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_NOMBRE_EQUIPO, nombreEquipo);
        values.put(COL_MATERIA, materia);
        values.put(COL_GRUPO, grupo);
        values.put(COL_SEMESTRE, semestre);
        values.put(COL_CARRERA, carrera);
        values.put(COL_HERRAMIENTAS, herramientas);
        values.put(COL_ARQUITECTURA, arquitectura);
        values.put(COL_FUNCIONES, funciones);
        values.put(COL_ID_PROFESOR, idProfesor);
        values.put(COL_ESTADO, estado);
        values.put(COL_URL_CARTEL, urlCartel);

        return db.update(TABLE_NAME, values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int cambiarEstadoProyecto(SQLiteDatabase db, int id, String nuevoEstado) {
        ContentValues values = new ContentValues();
        values.put(COL_ESTADO, nuevoEstado);

        return db.update(TABLE_NAME, values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int eliminarProyecto(SQLiteDatabase db, int id) {
        return db.delete(TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Metodo para obtener proyectos con información de estudiante y profesor (JOIN)
    public static Cursor obtenerProyectosConDetalles(SQLiteDatabase db) {
        String query = "SELECT p." + COL_ID + ", " +
                "p." + COL_NOMBRE_PROYECTO + ", " +
                "p." + COL_NOMBRE_EQUIPO + ", " +
                "p." + COL_MATERIA + ", " +
                "p." + COL_GRUPO + ", " +
                "p." + COL_ESTADO + ", " +
                "e." + EstudianteBD.COL_NOMBRE + " || ' ' || e." + EstudianteBD.COL_APELLIDOS + " as nombre_estudiante, " +
                "pr." + ProfesorBD.COL_NOMBRE + " || ' ' || pr." + ProfesorBD.COL_APELLIDOS + " as nombre_profesor " +
                "FROM " + TABLE_NAME + " p " +
                "LEFT JOIN " + EstudianteBD.TABLE_NAME + " e ON p." + COL_ID_ESTUDIANTE_LIDER + " = e." + EstudianteBD.COL_ID + " " +
                "LEFT JOIN " + ProfesorBD.TABLE_NAME + " pr ON p." + COL_ID_PROFESOR + " = pr." + ProfesorBD.COL_ID + " " +
                "ORDER BY p." + COL_FECHA_CREACION + " DESC";

        return db.rawQuery(query, null);
    }
}